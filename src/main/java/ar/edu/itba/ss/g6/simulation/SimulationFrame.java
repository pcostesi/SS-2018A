package ar.edu.itba.ss.g6.simulation;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Set;

public interface SimulationFrame<T extends Particle> {
    double getTimestamp();
    Set<T> getDelta();
    Set<T> getState();
}
