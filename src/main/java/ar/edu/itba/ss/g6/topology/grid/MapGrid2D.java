package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;


public class MapGrid2D<T extends Particle2D> extends MapGrid<T, Cell2D> {
    public MapGrid2D(long side, int buckets, double radius, boolean isPeriodic) {
        super(side, buckets, radius, isPeriodic, Cell2D::new);
    }
}
