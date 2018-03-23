package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.grid.Grid;

import java.util.Set;

public interface ParticleGenerator<T extends Particle> {
    Set<T> generate(Grid<T> grid);
}
