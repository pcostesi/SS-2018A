package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Set;

public interface Grid<T extends Particle> {
    Grid<T> place(T particle);
    Grid<T> remove(T particle);
    Set<T> getNeighbors(T particle, double radius);
    boolean contains(T particle);
    boolean isPeriodic();
    int getBucketCount();
    long getSideLength();
    Set<T> getParticles();
    int countParticles();
}
