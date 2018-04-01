package ar.edu.itba.ss.g6.simulation;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public interface EventDrivenSimulation<T extends Particle, F extends SimulationFrame<T>> extends Simulation<T, F> {
    TimeDrivenSimulation<T, F> toTimeDrivenSimulation();
}
