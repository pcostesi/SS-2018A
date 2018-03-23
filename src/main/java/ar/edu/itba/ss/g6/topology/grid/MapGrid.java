package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class MapGrid <T extends Particle, G extends Cell> implements Grid<T> {
    private final long side;
    private final int buckets;
    private final boolean isPeriodic;
    private final CellProvider<T, G> cellProvider;
    private Map<G, Set<T>> grid = new HashMap<>();

    public Grid<T> place(T particle) {
        G cell = cellProvider.provide(this, particle);
        Set<T> particles = grid.get(cell);
        if (particles == null) {
            particles = new HashSet<>();
            grid.put(cell, particles);
        }
        particles.add(particle);
        return this;
    }

    public Grid<T> remove(T particle) {
        G cell = cellProvider.provide(this, particle);
        Set<T> particles = grid.get(cell);
        if (particles != null && particles.contains(particle)) {
            particles.remove(particle);
        }
        return this;
    }

    /**
     * Implements cellIndexMethod
     * @param particle
     * @param radius
     * @return
     */
    public Set<T> getNeighbors(T particle, double radius) {
        G thisCell = cellProvider.provide(this, particle);
        return thisCell.semisphereNeighborhood()
         .map(cell -> grid.get(cell))
         .filter(Objects::nonNull)
         .flatMap(Set::parallelStream)
         .filter(p -> particle.isWithinRadius(radius, p))
         .collect(Collectors.toSet());
    }

    @Override
    public boolean contains(T particle) {
        G cell = cellProvider.provide(this, particle);
        Set<T> particles = grid.get(cell);
        return particles != null && particles.contains(particle);
    }

    @Override
    public boolean isPeriodic() {
        return isPeriodic;
    }

    @Override
    public int getBucketCount() {
        return buckets;
    }

    @Override
    public long getSideLength() {
        return side;
    }

    public MapGrid(long side, int buckets, boolean isPeriodic, CellProvider<T, G> cellProvider) {
        this.side = side;
        this.buckets = buckets;
        this.isPeriodic = isPeriodic;
        this.cellProvider = cellProvider;
    }

    @Override
    public int countParticles() {
        return grid.values().parallelStream().mapToInt(s -> s.size()).sum();
    }


    @Override
    public Set<T> getParticles() {
        return grid.values().parallelStream()
            .flatMap(Set::parallelStream)
            .collect(Collectors.toSet());
    }
}
