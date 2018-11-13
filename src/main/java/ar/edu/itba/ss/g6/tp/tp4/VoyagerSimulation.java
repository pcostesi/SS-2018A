package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.CelestialBody2D;

import java.util.Arrays;

public class VoyagerSimulation implements TimeDrivenSimulation<CelestialBody2D, VoyagerSimulationFrame> {
    private final double deltaT;
    private double timestamp;
    private final CelestialBody2D[] firmament;
    private final ForceSimulator forceSimulator;
    private TrajectoryData[] simulationData;

    public VoyagerSimulation(double deltaT, CelestialBody2D... firmament) {
        this.deltaT = deltaT;
        this.firmament = firmament;

        // forceSimulator = new GPCo5ForceSimulator(deltaT);
        forceSimulator = new BeemanForceSimulator(deltaT);
    }

    @Override
    public VoyagerSimulationFrame getNextStep() {
        double oldTime = timestamp;
        timestamp += deltaT;

        // if this is the first frame, return the initial positions
        if (simulationData == null) {
            simulationData = forceSimulator.initParticles(firmament);
            return toSimulationFrame(oldTime, simulationData);
        }

        // if we're using the previous simulation frame, move the particles.
        simulationData = forceSimulator.move(simulationData);
        return toSimulationFrame(oldTime, simulationData);
    }

    private VoyagerSimulationFrame toSimulationFrame(double timestamp, TrajectoryData[] data) {

        CelestialBody2D[] bodies = new CelestialBody2D[data.length];
        for (int i = 0; i < data.length; i++) {
            TrajectoryData b = data[i];

            double rx = b.getRx()[0];
            double ry = b.getRy()[0];
            double vx = b.getRx()[1];
            double vy = b.getRy()[1];
            String id = b.getId();
            double mass = b.getMass();
            double radius = b.getRadius();
            bodies[i] = new CelestialBody2D(id, rx, ry, vx, vy, radius, mass);
        }
        return new VoyagerSimulationFrame(timestamp, bodies);
    }

    @Override
    public double getDeltaT() {
        return deltaT;
    }

    @Override
    public double getFPS() {
        return 0;
    }

    @Override
    public double getMaxHeight() {
        return 0;
    }

    @Override
    public void resetNormalForce() {

    }
}
