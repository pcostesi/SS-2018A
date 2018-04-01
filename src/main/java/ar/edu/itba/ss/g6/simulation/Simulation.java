package ar.edu.itba.ss.g6.simulation;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public interface Simulation<T extends Particle, F extends SimulationFrame<T>> {
    F getNextStep();
}
