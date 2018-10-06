package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;

import java.util.Objects;
import java.util.stream.Stream;

public class CellV2d<T extends TheParticle> implements Cell {
    private double side;
    private int buckets;
    private double xStart;
    private double yStart;
    private Grid grid;

    @Override
    public String toString() {
        return String.format("<x=%f, y=%f>", xStart, yStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellV2d<?> cellV2d = (CellV2d<?>) o;
        return Double.compare(cellV2d.side, side) == 0 &&
                buckets == cellV2d.buckets &&
                Double.compare(cellV2d.xStart, xStart) == 0 &&
                Double.compare(cellV2d.yStart, yStart) == 0 &&
                Objects.equals(grid, cellV2d.grid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(side, buckets, xStart, yStart, grid);
    }

    public CellV2d(Grid grid, TheParticle particle) {
        this(grid, particle.getPosition().getX(), particle.getPosition().getY());
    }


    private CellV2d(Grid grid, double xCoord, double yCoord) {
        this.side = grid.getSideLength();
        this.buckets = grid.getBucketCount();
        this.grid = grid;

        double lSize = side / buckets;
        xStart = Math.floor(Math.floor(xCoord / lSize) * lSize);
        yStart = Math.floor(Math.floor(yCoord / lSize) * lSize);

        xStart = grid.isPeriodic() ? (xStart % side + side) % side : Math.max(0, Math.min(xStart, side));
        yStart = grid.isPeriodic() ? (yStart % side + side) % side : Math.max(0, Math.min(yStart, side));
    }

    public CellV2d getNeighbor(int x, int y) {
        double delta = side / buckets;
        return new CellV2d(this.grid, xStart + delta * x, yStart + delta * y);
    }

    public Stream<? extends Cell> semisphereNeighborhood() {
        CellV2d upperCell = this.getNeighbor(0, 1);
        CellV2d upperRightCell = this.getNeighbor(1, 1);
        CellV2d rightCell = this.getNeighbor(1, 0);
        CellV2d bottomRightCell = this.getNeighbor(1, -1);
        return Stream.of(this, upperCell, upperRightCell, rightCell, bottomRightCell).filter(Objects::nonNull);
    }
}