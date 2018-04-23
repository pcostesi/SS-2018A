package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.CelestialBody2D;

import java.util.Set;

public class VoyagerSimulationFrame implements SimulationFrame<CelestialBody2D> {
    private final double timestamp;
    private Set<CelestialBody2D> firmament;

    public VoyagerSimulationFrame(double timestamp, CelestialBody2D... firmament) {
        this.timestamp = timestamp;
        this.firmament = Set.of(firmament);
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public Set<CelestialBody2D> getDelta() {
        return firmament;
    }

    @Override
    public Set<CelestialBody2D> getState() {
        return firmament;
    }
}
