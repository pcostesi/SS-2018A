package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.topology.particle.CelestialBody2D;
import ar.edu.itba.ss.g6.tp.tp4.CelestialData;
import ar.edu.itba.ss.g6.tp.tp4.Ephemeris;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerData;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerSimulation;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerSimulationFrame;
import org.codehaus.jackson.map.ObjectMapper;
import ar.edu.itba.ss.g6.tp.tp4.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TP4b {

    public static void main(String... args) {
        File ephemerisFile = Paths.get("ephemeris.json").toFile();

        try {
            CelestialData data = loadEphemeris(ephemerisFile);
            final double deltaT = data.getDeltaT();

            int kms = 100;
            Stream<MinDistanceTrajectory> trajectories = IntStream.range(0, 10000 / kms)
                    .parallel()
                    .mapToObj(height ->
                        IntStream.range(0, 20).mapToObj(speed -> {
                            CelestialBody2D[] bodies;
                            bodies = loadBodies(data, height * kms, speed, 0);
                            Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator;
                            simulator = new VoyagerSimulation(deltaT, bodies);
                            double distance = simulate(simulator, data, bodies);
                            return new MinDistanceTrajectory(distance, height, speed);
                        }))
                    .flatMap(i -> i);

            MinDistanceTrajectory bestTrajectory = trajectories
                    .min(Comparator.comparingDouble(MinDistanceTrajectory::getBestDistance))
                    .orElse(null);

            System.out.println("Best trajectory:");
            System.out.println(" - distance: " + bestTrajectory.getBestDistance());
            System.out.println(" - height: " + bestTrajectory.getBestHeight());
            System.out.println(" - speed: " + bestTrajectory.getBestSpeed());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static double simulate (Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator, CelestialData data, CelestialBody2D[] bodies) {
        int days = data.getDays();
        double deltaT = data.getDeltaT();
        double fpd = data.getFpd();
        int SECONDS_IN_A_DAY = 60 * 60 * 24;
        double CAPTURE_EVERY_N_FRAMES = SECONDS_IN_A_DAY / fpd;

        Exporter<CelestialBody2D> exporter = new OvitoXYZExporter<>();
        List<Collection<CelestialBody2D>> frames = new LinkedList<>();
        long stop = Math.round(Math.ceil(days * SECONDS_IN_A_DAY / deltaT));
        List<Double> speedList = new ArrayList<>();
        double[] bestDistance = new double[] {Double.MAX_VALUE, Double.MAX_VALUE};


            try (BufferedWriter w = new BufferedWriter(new FileWriter(Paths.get("tp4b-out.xyz").toFile()))) {
                while (stop-- > 0) {
                    VoyagerSimulationFrame frame = simulator.getNextStep();

                    if (Math.round(frame.getTimestamp()) % CAPTURE_EVERY_N_FRAMES == 0) {
                        exporter.addFrameToFile(w, frame.getState(), 0);
                    }
                    frames.add(frame.getState());
                double speed = frame.getState().stream()
                    .filter(p -> p.getId().equals("100"))
                    .mapToDouble(p -> p.getSpeed()).findFirst().orElse(0);
                speedList.add(speed);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try (BufferedWriter w = new BufferedWriter(new FileWriter(Paths.get("velocity.out").toFile()))) {
                for (int i = 0; i < speedList.size(); i++) {
                    double speed = speedList.get(i);
                    w.write(String.format("%d \t %e\n", i * 100, speed));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return 0;
    }


    private static CelestialBody2D[] loadBodies(CelestialData data, int voyagerDistance, double voyagerSpeed, double voyagerAngle) {
        Ephemeris[] planets = data.getPlanets();
        CelestialBody2D voyager;
        CelestialBody2D sun = null;
        CelestialBody2D earth = null;
        CelestialBody2D[] bodies = new CelestialBody2D[planets.length + 1];

        for (int idx = 0; idx < planets.length; idx++) {
            Ephemeris planet = planets[idx];
            String id = planet.getId();
            double rx = planet.getRx();
            double ry = planet.getRy();
            double vx = planet.getVx();
            double vy = planet.getVy();
            double mass = planet.getMass();
            double radius = planet.getRadius();
            bodies[idx] = new CelestialBody2D(id, rx, ry, vx, vy, radius, mass);

            if (id.equals("0")) {
                sun = bodies[idx];
            } else if (id.equals("3")) {
                earth = bodies[idx];
            }
        }
        if (earth == null || sun == null) {
            throw new IllegalArgumentException("You're missing sun or earth");
        }
        VoyagerData v1 = data.getVoyager1();
        double distanceX = (sun.getXCoordinate() - earth.getXCoordinate());
        double distanceY = (sun.getYCoordinate() - earth.getYCoordinate());
        double angle = Math.atan2(distanceY, distanceX);
        double km = voyagerDistance + earth.getRadius();
        double v1rx = earth.getXCoordinate() + Math.cos(angle) * km;
        double v1ry = earth.getYCoordinate() + Math.sin(angle) * km;
        double v1angle = angle - Math.toRadians(90) + Math.toRadians(voyagerAngle);
        double v1vx = earth.getXSpeed() + voyagerSpeed * Math.cos(v1angle);
        double v1vy = earth.getYSpeed() + voyagerSpeed * Math.sin(v1angle);
        double v1m = v1.getWeight();
        bodies[bodies.length - 1] = new CelestialBody2D(v1.getId(), v1rx, v1ry, v1vx, v1vy, 10000, v1m);
        return bodies;
    }

    private static CelestialData loadEphemeris(File ephemerisFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ephemerisFile, CelestialData.class);
    }
}
