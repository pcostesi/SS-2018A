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
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TP4b {

    public static final int EARTH_ESCAPE_VELOCITY = 10;

    public static void main(String... args) throws IOException {
        File ephemerisFile = Paths.get("ephemeris.json").toFile();

        MinDistanceTrajectory bestTrajectory = exerciseThreePointOne(ephemerisFile);
        int bestDay = exerciseThreePointFour(ephemerisFile, bestTrajectory);
        int bestYear = exerciseThreePointFive(ephemerisFile, bestTrajectory);
        int bestAngle = exerciseThreePointSix(ephemerisFile, bestTrajectory);
    }

    private static int exerciseThreePointSix(File ephemerisFile, MinDistanceTrajectory bestTrajectory) throws IOException {
        CelestialData data = loadEphemeris(ephemerisFile);

        System.out.println("Simulating angles using the optimal trajectory data");
        System.out.println("This will perform " + 180 + " simulations.");
        int bestAngle = IntStream.range(0, 180).parallel().mapToObj(angle -> {
            System.out.println("launching at " + angle + " deg");
            CelestialBody2D[] bodies = loadBodies(data, bestTrajectory.getBestHeight(), bestTrajectory.getBestSpeed(), angle);
            VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
            double[] distance = sim(simulator, data, bodies);
            return new double[]{angle, distance[0], distance[1]};
        }).min(Comparator.comparingDouble(o -> {
            double[] t = o;
            return t[1] + t[2];
        })).map(e -> (int) e[0]).orElse(0);

        CelestialBody2D[] bodies = loadBodies(data, bestTrajectory.getBestHeight(), bestTrajectory.getBestSpeed(), bestAngle);
        VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
        simulate(simulator, data, bodies, "3.6");

        System.out.println("best angle: " + bestAngle);
        return bestAngle;
    }


    private static int exerciseThreePointFive(File ephemerisFile, MinDistanceTrajectory bestTrajectory) throws IOException {
        CelestialData data = loadEphemeris(ephemerisFile);

        System.out.println("Simulating alternative year using the optimal trajectory data");
        System.out.println("This will perform " + 250 + " simulations.");
        int bestYear = IntStream.range(1, 250).parallel().mapToObj(year -> {
            System.out.println(year + " years from launch");
            CelestialBody2D[] bodies = loadBodiesDelta(data, bestTrajectory, year * 365);
            VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
            double[] distance = sim(simulator, data, bodies);
            return new double[]{year, distance[0], distance[1]};
        }).min(Comparator.comparingDouble(o -> {
            double[] t = o;
            return t[1] + t[2];
        })).map(e -> (int) e[0]).orElse(0);

        CelestialBody2D[] bodies = loadBodiesDelta(data, bestTrajectory, bestYear * 365);
        VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
        simulate(simulator, data, bodies, "3.5");

        System.out.println("best year: " + bestYear);
        return bestYear;
    }

    private static int exerciseThreePointFour(File ephemerisFile, MinDistanceTrajectory bestTrajectory) throws IOException {
        CelestialData data = loadEphemeris(ephemerisFile);

        System.out.println("Simulating alternative dates using the optimal trajectory data");
        System.out.println("This will perform " + (30 * 2) + " simulations.");
        int bestDay = IntStream.range(-30, 30).parallel().mapToObj(day -> {
            System.out.println(day + " days from launch");
            CelestialBody2D[] bodies = loadBodiesDelta(data, bestTrajectory, day);
            VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
            double[] distance = sim(simulator, data, bodies);
            return new double[]{day, distance[0], distance[1]};
        }).min(Comparator.comparingDouble(o -> {
            double[] t = o;
            return t[1] + t[2];
        })).map(e -> (int) e[0]).orElse(0);

        CelestialBody2D[] bodies = loadBodiesDelta(data, bestTrajectory, bestDay);
        VoyagerSimulation simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
        simulate(simulator, data, bodies, "3.4");

        System.out.println("best day: " + bestDay);
        return bestDay;
    }

    private static MinDistanceTrajectory exerciseThreePointOne(File ephemerisFile) {
        try {
            CelestialData data = loadEphemeris(ephemerisFile);
            System.out.println("Coarse - Building simulation grid");
            int kms = 1000;
            int speedIncrement = 1;
            int minSpeed = EARTH_ESCAPE_VELOCITY;
            int minHeight = 0;
            int maxSpeed = 20;
            int maxHeigh = 10000;

            // coarse
            List<List<MinDistanceTrajectory>> trajectories = trajectoryParametricHeatmap(data, kms, speedIncrement, minSpeed, maxSpeed, minHeight, maxHeigh);

            System.out.println("Coarse - Exporting heatmaps");
            exportToHeatmap(ephemerisFile, kms, 1, minSpeed, maxSpeed, minHeight, maxHeigh, trajectories, 0, "planet_distance_data_jupiter_initial.json");
            exportToHeatmap(ephemerisFile, kms, 1, minSpeed, maxSpeed, minHeight, maxHeigh, trajectories, 1, "planet_distance_data_saturn_initial.json");

            System.out.println("Coarse - Analyzing launch info for optimum trajectory");
            MinDistanceTrajectory bestTrajectory = findMinDistance(trajectories);

            System.out.println("Coarse - Plotting trajectory");
            simulateAndSave(data, bestTrajectory, "3.1-initial");

            // fine
            System.out.println("Fine - Building simulation grid");

            double scale = 0.1;
            double kmsFine = kms * scale * 2;
            double speedIncrementFine = speedIncrement * scale * 2;
            double minSpeedFine = Math.max(minSpeed, bestTrajectory.getBestSpeed() - speedIncrement);
            double minHeightFine = Math.max(minHeight, bestTrajectory.getBestHeight() - kms);
            double maxSpeedFine = Math.min(bestTrajectory.getBestSpeed() + speedIncrement, maxSpeed);
            double maxHeighFine = Math.min(bestTrajectory.getBestHeight() + kms, maxHeigh);

            trajectories = trajectoryParametricHeatmap(data, kmsFine, speedIncrementFine, minSpeedFine, maxSpeedFine, minHeightFine, maxHeighFine);

            System.out.println("Fine - Exporting heatmaps");
            exportToHeatmap(ephemerisFile, kms, 1, minSpeed, maxSpeed, minHeight, maxHeigh, trajectories, 0, "planet_distance_data_jupiter_fine.json");
            exportToHeatmap(ephemerisFile, kms, 1, minSpeed, maxSpeed, minHeight, maxHeigh, trajectories, 1, "planet_distance_data_saturn_fine.json");

            System.out.println("Fine - Analyzing launch info for optimum trajectory");
            bestTrajectory = findMinDistance(trajectories);

            System.out.println("Fine - Plotting trajectory");
            simulateAndSave(data, bestTrajectory, "3.1-fine");

            return bestTrajectory;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static List<List<MinDistanceTrajectory>> trajectoryParametricHeatmap(CelestialData data, double kms, double speedIncrement, double minSpeed, double maxSpeed, double minHeight, double maxHeight) {
        System.out.println("This will perform " + ((maxSpeed - minSpeed) / speedIncrement) * ((maxHeight - minHeight) / kms) + " simulations.");
        return DoubleStream.iterate(minHeight, d -> d < maxHeight, d -> d + kms)
                        .mapToObj(height ->
                            DoubleStream.iterate(minSpeed, d -> d + speedIncrement)
                                    .parallel()
                                    .takeWhile(d -> d < maxSpeed)
                                    .mapToObj(speed -> {
                                        System.out.println(String.format("s=%f, h=%f", speed, height));
                                        CelestialBody2D[] bodies;
                                        bodies = loadBodies(data, height, speed, 0);
                                        Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator;
                                        simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
                                        double[] distance = sim(simulator, data, bodies);
                                        return new MinDistanceTrajectory(distance, height, speed, 0);
                                    })
                                    .sorted(Comparator.comparingDouble(MinDistanceTrajectory::getBestSpeed))
                                    .collect(Collectors.toList())
                        ).collect(Collectors.toList());
    }

    private static void simulateAndSave(CelestialData data, MinDistanceTrajectory bestTrajectory, String name) {
        CelestialBody2D[] bodies;
        bodies = loadBodies(data, bestTrajectory.getBestHeight(), bestTrajectory.getBestSpeed(), 0);
        Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator;
        simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
        simulate(simulator, data, bodies, name);
    }

    @NotNull
    private static MinDistanceTrajectory findMinDistance(List<List<MinDistanceTrajectory>> trajectories) {
        Stream<MinDistanceTrajectory> trajStream = trajectories.stream().flatMap(l -> l.stream());
        MinDistanceTrajectory bestTrajectory = trajStream
                .min(Comparator.comparingDouble(o -> {
                    double t[] = o.getBestDistance();
                    return t[0] + t[1];
                }))
                .orElse(null);

        System.out.println("Best trajectory:");
        System.out.println(" - distance to Jupiter: " + bestTrajectory.getBestDistance()[0]);
        System.out.println(" - distance to Saturn: " + bestTrajectory.getBestDistance()[1]);
        System.out.println(" - time to Juputer: " + bestTrajectory.getBestDistance()[2]);
        System.out.println(" - time to Saturn: " + bestTrajectory.getBestDistance()[3]);
        System.out.println(" - height: " + bestTrajectory.getBestHeight());
        System.out.println(" - speed: " + bestTrajectory.getBestSpeed());
        return bestTrajectory;
    }

    private static void exportToHeatmap(File ephemerisFile, double kms, double speedIncrement, double minSpeed, double maxSpeed, double minHeight, double maxHeigh, List<List<MinDistanceTrajectory>> trajectories, int idx, String name) throws IOException {
        List<List<Double>> distancesToSaturn = trajectories.stream()
                .map(l -> l.stream().map(v -> v.getBestDistance()[idx]).collect(Collectors.toList())
                ).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();

        mapper.readValue(ephemerisFile, CelestialData.class);
        List<Double> lInfo = List.of(minHeight, maxHeigh, kms);
        List<Double> vInfo = List.of(minSpeed, maxSpeed, speedIncrement);
        mapper.writer().writeValue(Paths.get(name).toFile(), List.of(lInfo, vInfo, distancesToSaturn));
    }


    private static double[] sim (Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator, CelestialData data, CelestialBody2D[] bodies) {
        int days = data.getDays();
        double deltaT = data.getDeltaT();
        int SECONDS_IN_A_DAY = 60 * 60 * 24;

        long stop = Math.round(Math.ceil(days * SECONDS_IN_A_DAY / deltaT));
        double[] bestDistance = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, 0, 0};

        while (stop-- > 0) {
            VoyagerSimulationFrame frame = simulator.getNextStep();

            CelestialBody2D voyager = frame.getState().stream()
                    .filter(p -> p.getId().equals("100")).findFirst().get();

            CelestialBody2D jupiter = frame.getState().stream()
                    .filter(p -> p.getId().equals("5")).findFirst().get();

            CelestialBody2D saturn = frame.getState().stream()
                    .filter(p -> p.getId().equals("6")).findFirst().get();

            double distanceToJupiter = voyager.distanceTo(jupiter);
            double distanceToSaturn = voyager.distanceTo(saturn);

            if (distanceToJupiter < bestDistance[0]) {
                bestDistance[0] = distanceToJupiter;
                bestDistance[2] = frame.getTimestamp();
            }
            if (distanceToSaturn < bestDistance[1]) {
                bestDistance[1] = distanceToSaturn;
                bestDistance[3] = frame.getTimestamp();
            }
        }
        return bestDistance;
    }



    private static double simulate (Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator, CelestialData data, CelestialBody2D[] bodies, String name) {
        int days = data.getDays();
        double deltaT = data.getDeltaT();
        double fpd = data.getFpd();
        int SECONDS_IN_A_DAY = 60 * 60 * 24;
        double CAPTURE_EVERY_N_FRAMES = SECONDS_IN_A_DAY / fpd;

        Exporter<CelestialBody2D> exporter = new OvitoXYZExporter<>();
        List<Collection<CelestialBody2D>> frames = new LinkedList<>();
        long stop = Math.round(Math.ceil(days * SECONDS_IN_A_DAY / deltaT));
        List<Double> speedList = new ArrayList<>();


        try (BufferedWriter w = new BufferedWriter(new FileWriter(Paths.get("tp4b-" + name + ".xyz").toFile()))) {
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
        try (BufferedWriter w = new BufferedWriter(new FileWriter(Paths.get("velocity-" + name + ".out").toFile()))) {
            for (int i = 0; i < speedList.size(); i++) {
                double speed = speedList.get(i);
                w.write(String.format("%d \t %e\n", i * 100, speed));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static CelestialBody2D[] reverseSpeed(CelestialBody2D[] bodies) {
        return Arrays.stream(bodies)
                    .map(t -> new CelestialBody2D(t.getId(), t.getXCoordinate(), t.getYCoordinate(),
                            -t.getXSpeed(), -t.getYSpeed(), t.getRadius(), t.getWeight()))
                    .toArray(i -> new CelestialBody2D[i]);
    }

    private static CelestialBody2D[] loadBodiesDelta(CelestialData data, MinDistanceTrajectory voyagerData, final int days) {
        Ephemeris[] planets = data.getPlanets();
        CelestialBody2D voyager;
        CelestialBody2D sun = null;
        CelestialBody2D earth = null;
        CelestialBody2D[] bodies = loadPlanets(planets);

        bodies = runOrbitalSimulation(data, days, bodies);

        for (int idx = 0; idx < planets.length; idx++) {
            Ephemeris planet = planets[idx];
            String id = planet.getId();
            if (id.equals("0")) {
                sun = bodies[idx];
            } else if (id.equals("3")) {
                earth = bodies[idx];
            }
        }

        if (sun == null || earth == null) {
            throw new IllegalArgumentException("Missing sun or earth");
        }

        voyager = loadVoyager(data, voyagerData.getBestHeight(), voyagerData.getBestSpeed(), voyagerData.getAngle(), sun, earth);

        bodies = Arrays.copyOf(bodies, bodies.length + 1);
        bodies[bodies.length - 1] = voyager;
        return bodies;
    }

    private static CelestialBody2D[] runOrbitalSimulation(CelestialData data, int days, CelestialBody2D[] bodies) {
        double deltaT = data.getDeltaT();
        int SECONDS_IN_A_DAY = 60 * 60 * 24;

        long stop = Math.round(Math.ceil(Math.abs(days) * SECONDS_IN_A_DAY / deltaT));

        if (days < 0) {
            bodies = reverseSpeed(bodies);
        }
        Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator;
        simulator = new VoyagerSimulation(data.getDeltaT(), bodies);
        VoyagerSimulationFrame frame = null;

        while (stop --> 0) {
            frame = simulator.getNextStep();
        }
        bodies = frame == null ? bodies : frame.getState().toArray(new CelestialBody2D[bodies.length]);
        if (days < 0) {
            bodies = reverseSpeed(bodies);
        }
        return bodies;
    }


    private static CelestialBody2D[] loadBodies(CelestialData data, double voyagerDistance, double voyagerSpeed, double voyagerAngle) {
        Ephemeris[] planets = data.getPlanets();
        CelestialBody2D voyager;
        CelestialBody2D sun = null;
        CelestialBody2D earth = null;
        CelestialBody2D[] bodies = loadPlanets(planets);

        for (int idx = 0; idx < planets.length; idx++) {
            Ephemeris planet = planets[idx];
            String id = planet.getId();
            if (id.equals("0")) {
                sun = bodies[idx];
            } else if (id.equals("3")) {
                earth = bodies[idx];
            }
        }

        if (sun == null || earth == null) {
            throw new IllegalArgumentException("Missing sun or earth");
        }
        voyager = loadVoyager(data, voyagerDistance, voyagerSpeed, voyagerAngle, sun, earth);

        bodies = Arrays.copyOf(bodies, bodies.length + 1);
        bodies[bodies.length - 1] = voyager;
        return bodies;
    }

    private static CelestialBody2D[] loadPlanets(Ephemeris[] planets) {
        CelestialBody2D[] bodies = new CelestialBody2D[planets.length];
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
        }
        return bodies;
    }

    @NotNull
    private static CelestialBody2D loadVoyager(CelestialData data, double voyagerDistance, double voyagerSpeed, double voyagerAngle, CelestialBody2D sun, CelestialBody2D earth) {
        CelestialBody2D voyager;VoyagerData v1 = data.getVoyager1();
        double distanceX = (earth.getXCoordinate() - sun.getXCoordinate());
        double distanceY = (earth.getYCoordinate() - sun.getYCoordinate());
        double angle = Math.atan2(distanceY, distanceX);
        double km = voyagerDistance + earth.getRadius();
        double v1rx = earth.getXCoordinate() + Math.cos(angle) * km;
        double v1ry = earth.getYCoordinate() + Math.sin(angle) * km;
        double v1angle = angle + Math.toRadians(90) - Math.toRadians(voyagerAngle);
        double v1vx = earth.getXSpeed() + voyagerSpeed * Math.cos(v1angle);
        double v1vy = earth.getYSpeed() + voyagerSpeed * Math.sin(v1angle);
        double v1m = v1.getWeight();
        voyager = new CelestialBody2D(v1.getId(), v1rx, v1ry, v1vx, v1vy, 1000, v1m);
        return voyager;
    }

    private static CelestialData loadEphemeris(File ephemerisFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ephemerisFile, CelestialData.class);
    }
}
