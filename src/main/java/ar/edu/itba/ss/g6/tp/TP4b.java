package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.topology.particle.CelestialBody2D;
import ar.edu.itba.ss.g6.tp.tp4.CelestialData;
import ar.edu.itba.ss.g6.tp.tp4.Ephemeris;
import ar.edu.itba.ss.g6.tp.tp4.TrajectoryData;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerData;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerSimulation;
import ar.edu.itba.ss.g6.tp.tp4.VoyagerSimulationFrame;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TP4b {

    public static void main(String... args) {
        File ephemerisFile = Paths.get("ephemeris.json").toFile();
        Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator;
        CelestialBody2D[] bodies;
        double deltaT = 1;
        int days = 366 * 4;

        try {
            CelestialData data = loadEphemeris(ephemerisFile);
            deltaT = data.getDeltaT();
            days = data.getDays();
            bodies = loadBodies(data);

            simulator = new VoyagerSimulation(deltaT, bodies);
            simulate(simulator, deltaT, days, bodies);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    private static void simulate (Simulation<CelestialBody2D, VoyagerSimulationFrame> simulator, double deltaT, int days, CelestialBody2D[] bodies) {
        Exporter<CelestialBody2D> exporter = new OvitoXYZExporter<>();
        List<Collection<CelestialBody2D>> frames = new LinkedList<>();
        long stop = Math.round(Math.ceil(days * 24 * 60 / deltaT));
        List<Double> speedList = new ArrayList<>();
        while (stop --> 0) {
            VoyagerSimulationFrame frame = simulator.getNextStep();
            frames.add(frame.getState());
            double speed = frame.getState().stream()
                .filter(p -> p.getId().equals("100"))
                .mapToDouble(p -> p.getSpeed()).findFirst().orElse(0);
            speedList.add(speed);
        }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(Paths.get("velocity.out").toFile()))){
            exporter.saveAnimationToFile("tp4b-out.xyz", frames, 1);
            for (int i = 0; i < speedList.size(); i++) {
                double speed = speedList.get(i);
                w.write(String.format("%d \t %e\n", i * 100, speed));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static CelestialBody2D[] loadBodies(CelestialData data) {
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
        double km = 1500;
        double v1rx = earth.getXCoordinate() + Math.cos(angle) * km;
        double v1ry = earth.getYCoordinate() + Math.sin(angle) * km;
        double v1vx = earth.getXSpeed() + v1.getSpeed() * Math.cos(angle + Math.PI / 2 + v1.getAngle());
        double v1vy = earth.getYSpeed() + v1.getSpeed() * Math.sin(angle + Math.PI / 2 + v1.getAngle());
        double v1m = v1.getWeight();
        bodies[bodies.length - 1] = new CelestialBody2D(v1.getId(), v1rx, v1ry, v1vx, v1vy, 10000, v1m);
        return bodies;
    }

    private static CelestialData loadEphemeris(File ephemerisFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(ephemerisFile, CelestialData.class);
    }
}
