package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.*;

public abstract class MapGrid <T extends Particle, G extends Cell> implements Grid<T> {
    private final double side;
    private final int buckets;
    private final double radius;
    private final boolean isPeriodic;
    private final CellProvider<T, G> cellProvider;
    private final Map<T, Collection<T>> neighborhoods = new HashMap<>();
    private final Map<G, Collection<T>> grid = new HashMap();

    synchronized private void addBothWays(T particle, T neighbor) {

        Collection<T> theirNeighborhood = neighborhoods.get(neighbor);
        theirNeighborhood.add(particle);
        neighborhoods.put(neighbor, theirNeighborhood);

        Collection<T> myNeighborhood = neighborhoods.get(neighbor);
        myNeighborhood.add(neighbor);
        neighborhoods.put(particle, myNeighborhood);
    }

    abstract boolean areWithinDistance(T p1, T p2, double distance);

    private void cellIndexMethod(Collection<T> particles) {
        particles.forEach(particle -> {
            neighborhoods.put(particle, new HashSet<>());

            G cell = cellProvider.provide(this, particle);
            Collection<T> bucket = grid.computeIfAbsent(cell, k -> new HashSet<>());
            bucket.add(particle);
        });

        grid.keySet().forEach(cell -> {
            Collection<T> ownCellParticles = grid.get(cell);
            if (ownCellParticles == null) {
                return;
            }

            cell.semisphereNeighborhood()
                    .parallel()
                .map(grid::get)
                .filter(Objects::nonNull)
                    .forEach(neighborhood -> {
                        neighborhood.forEach(neighbor -> {
                            for (T particle : ownCellParticles) {
                                if (areWithinDistance(particle, neighbor, radius)) {
                                    addBothWays(particle, neighbor);
                                }
                            }
                        });
                    });

        });
    }

    public Grid<T> set(Collection<T> particles) {
        cellIndexMethod(particles);
        return this;
    }

    public Collection<T> getNeighbors(T particle) {
        return neighborhoods.get(particle);
    }

    @Override
    public boolean contains(T particle) {
        return neighborhoods.containsKey(particle);
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
    public double getSideLength() {
        return side;
    }

    public MapGrid(double side, int buckets, double radius, boolean isPeriodic, CellProvider<T, G> cellProvider) {
        this.side = side;
        this.buckets = buckets;
        this.isPeriodic = isPeriodic;
        this.cellProvider = cellProvider;
        this.radius = radius;
    }

    @Override
    public int countParticles() {
        return neighborhoods.size();
    }

    @Override
    public Set<T> getParticles() {
        return neighborhoods.keySet();
    }
}
