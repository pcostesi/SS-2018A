package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MapGrid <T extends Particle, G extends Cell> implements Grid<T> {
    private final long side;
    private final int buckets;
    private final double radius;
    private final boolean isPeriodic;
    private final CellProvider<T, G> cellProvider;
    private final Map<T, Set<T>> neighborhoods = new HashMap<>();
    private final Map<T, Set<T>> neighborhoodsBF = new HashMap<>();
    private final boolean useCellIndexMethod = true;

    private void addBothWays(T particle, T neighbor) {
        Set<T> theirNeighborhood = neighborhoods.getOrDefault(neighbor, new HashSet<>());
        theirNeighborhood.add(particle);
        neighborhoods.put(neighbor, theirNeighborhood);

        Set<T> myNeighborhood = neighborhoods.getOrDefault(particle, new HashSet<>());
        myNeighborhood.add(neighbor);
        neighborhoods.put(particle, myNeighborhood);
    }


    private void addBothWaysBF(T particle, T neighbor) {
        Set<T> theirNeighborhood = neighborhoodsBF.getOrDefault(neighbor, new HashSet<>());
        theirNeighborhood.add(particle);
        neighborhoodsBF.put(neighbor, theirNeighborhood);

        Set<T> myNeighborhood = neighborhoodsBF.getOrDefault(particle, new HashSet<>());
        myNeighborhood.add(neighbor);
        neighborhoodsBF.put(particle, myNeighborhood);
    }

    private void bruteForceSet(Collection<T> particles) {
        for (T particle : particles) {
            for (T other : particles) {
                if (areWithinDistance(particle, other, radius)) {
                    addBothWaysBF(particle, other);
                }
            }
        }
    }


    abstract boolean areWithinDistance(T p1, T p2, double distance);

    private T place(Map<G, Set<T>> grid, T particle) {
        G cell = cellProvider.provide(this, particle);
        Set<T> particles = grid.get(cell);
        if (particles == null) {
            particles = new HashSet<>();
            grid.put(cell, particles);
        }
        particles.add(particle);
        return particle;
    }

    private void cellIndexMethod(Collection<T> particles) {
        Map<G, Set<T>> grid = new HashMap<>();
        particles.forEach(particle -> place(grid, particle));
        assert particles.size() == grid.values().stream().mapToInt(Set::size).sum();

        for (G cell : grid.keySet()) {
            Set<T> ownCellParticles = grid.get(cell);
            Set<T> neighboringCellParticles = cell.semisphereNeighborhood()
                .map(grid::get)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

            for (T particle : ownCellParticles) {
                for (T neighbor : neighboringCellParticles) {
                    if (areWithinDistance(particle, neighbor, radius)) {
                        addBothWays(particle, neighbor);
                    }
                }
            }
        }
    }

    public Grid<T> set(Collection<T> particles) {
        cellIndexMethod(particles);
        bruteForceSet(particles);
        return this;
    }

    public Set<T> getNeighbors(T particle) {
        Set<T> usingBF = neighborhoodsBF.get(particle);
        Set<T> usingCIM = neighborhoods.get(particle);
        assert usingBF.size() == usingCIM.size();
        for (T p : usingBF) {
            assert usingCIM.contains(p);
        }
        for (T p : usingCIM) {
            assert usingBF.contains(p);
        }
        return usingCIM;
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
    public long getSideLength() {
        return side;
    }

    public MapGrid(long side, int buckets, double radius, boolean isPeriodic, CellProvider<T, G> cellProvider) {
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
