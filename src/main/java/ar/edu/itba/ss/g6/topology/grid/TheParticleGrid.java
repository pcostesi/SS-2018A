package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

public class TheParticleGrid implements Grid<TheParticle> {
    private final int bucketCount;
    private final double sideSize;
    private final double actionRadius;
    private final Map<TheParticle, Collection<TheParticle>> particles = new HashMap<>();
    private boolean checkBruteForce = false;

    public TheParticleGrid(int bucketCount, double sideSize, double actionRadius) {
        this.bucketCount = bucketCount;
        this.sideSize = sideSize;
        this.actionRadius = actionRadius;
        if (actionRadius > sideSize / bucketCount) {
            String msg = "action radius ({0}) can't be larger than half the bucket side size ({1})";
            throw new IllegalArgumentException(MessageFormat.format(msg, actionRadius, sideSize / bucketCount));
        }
    }

    private void addBothWays(TheParticle particle, TheParticle neighbor) {
        synchronized (this) {
            Collection<TheParticle> theirNeighborhood = particles.get(neighbor);
            Collection<TheParticle> myNeighborhood = particles.get(particle);

            theirNeighborhood.add(particle);
            myNeighborhood.add(neighbor);
        }
    }

    private void connectNeighborParticles(Map<Cell, Collection<TheParticle>> buckets, Cell cell) {
        Collection<TheParticle> particles = buckets.get(cell);

        semisphereNeighborhood(cell)
                .parallel()
                .map(buckets::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(neighbor -> {
                    for (TheParticle particle : particles) {
                        if (particle.distanceTo(neighbor) < actionRadius) {
                            addBothWays(particle, neighbor);
                        }
                    }
                });
    }

    @Override
    public Grid<TheParticle> set(Collection<TheParticle> originalParticles) {
        final Map<Cell, Collection<TheParticle>> buckets = new HashMap<>();
        particles.clear();

        originalParticles.forEach(theParticle -> {
            V2d position = theParticle.getPosition();
            int row = (int) Math.floor(position.getX() * getBucketCount() / getSideLength());
            int col = (int) Math.floor(position.getY() * getBucketCount() / getSideLength());
            buckets.computeIfAbsent(new Cell(row, col), (cell) -> new LinkedList<>());
            particles.put(theParticle, new LinkedList<>());
        });

        buckets.forEach((cell, bucket) -> {
            connectNeighborParticles(buckets, cell);
        });


        if (checkBruteForce) bruteForceCheck(originalParticles);

        return this;
    }

    private void bruteForceCheck(Collection<TheParticle> allParticles) {
        for (TheParticle someParticle : allParticles) {
            for (TheParticle anotherParticle : allParticles) {
                assert !(someParticle.distanceTo(anotherParticle) < actionRadius) || getNeighbors(someParticle).contains(anotherParticle);
            }
        }
        allParticles.forEach(particle -> {
            getNeighbors(particle).forEach(neighbor -> {
                assert particle.distanceTo(neighbor) < actionRadius;
            });
        });
        assert particles.size() == allParticles.size();
        assert actionRadius > allParticles.stream().mapToDouble(TheParticle::getRadius).max().orElse(0);
    }

    private Stream<Cell> semisphereNeighborhood(Cell cell) {
        int row = cell.getRow();
        int col = cell.getCol();
        Cell selfCell = new Cell(row, col);
        Cell upperCell = row < bucketCount - 1 ? new Cell(row + 1, col) : null;
        Cell upperRightCell = row < bucketCount - 1 && col < bucketCount - 1 ? new Cell(row + 1, col + 1) : null;
        Cell rightCell = col < bucketCount - 1 ? new Cell(row, col + 1) : null;
        Cell bottomRightCell = row > 0 && col < bucketCount - 1 ? new Cell(row - 1, col + 1) : null;
        return Stream.of(selfCell, upperCell, upperRightCell, rightCell, bottomRightCell).filter(Objects::nonNull);
    }

    @Override
    public Collection<TheParticle> getNeighbors(TheParticle particle) {
        return particles.getOrDefault(particle, Collections.emptyList());
    }

    @Override
    public boolean contains(TheParticle particle) {
        return particles.containsKey(particle);
    }

    @Override
    public boolean isPeriodic() {
        return false;
    }

    @Override
    public int getBucketCount() {
        return bucketCount;
    }

    @Override
    public double getSideLength() {
        return sideSize;
    }

    @Override
    public Collection<TheParticle> getParticles() {
        return particles.keySet();
    }

    @Override
    public int countParticles() {
        return particles.size();
    }

    private static final class Cell {
        private final int row;
        private final int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return row == cell.row &&
                    col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }
}
