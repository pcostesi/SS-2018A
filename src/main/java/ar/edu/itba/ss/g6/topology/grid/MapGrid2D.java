package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;

import java.util.Arrays;
import java.util.Set;


public class MapGrid2D<T extends Particle2D> extends MapGrid<T, Cell2D> {
    public MapGrid2D(long side, int buckets, boolean isPeriodic, T... particles) {
        super(side, buckets, isPeriodic, Cell2D::new);
        Arrays.stream(particles).forEach(p -> place(p));
    }
}
