package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public interface CellProvider<T extends Particle, G extends Cell> {
    G provide(Grid<T> grid, T particle);
}
