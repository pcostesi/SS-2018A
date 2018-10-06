package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Collection;
import java.util.Set;

public interface Grid<T extends Particle> {
    Grid<T> set(Collection<T> particles);
    Collection<T> getNeighbors(T particle);
    boolean contains(T particle);
    boolean isPeriodic();
    int getBucketCount();
    double getSideLength();
    Collection<T> getParticles();
    int countParticles();
}
