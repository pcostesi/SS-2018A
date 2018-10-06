package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Cell2D<T extends Particle2D> implements Cell {
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
        Cell2D<?> cell2D = (Cell2D<?>) o;
        return Double.compare(cell2D.side, side) == 0 &&
                buckets == cell2D.buckets &&
                Double.compare(cell2D.xStart, xStart) == 0 &&
                Double.compare(cell2D.yStart, yStart) == 0 &&
                Objects.equals(grid, cell2D.grid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(side, buckets, xStart, yStart, grid);
    }

    public Cell2D(Grid grid, Particle2D particle) {
        this(grid, particle.getXCoordinate(), particle.getYCoordinate());
    }


    private Cell2D(Grid grid, double xCoord, double yCoord) {
        this.side = grid.getSideLength();
        this.buckets = grid.getBucketCount();
        this.grid = grid;

        if (side % buckets != 0) {
            throw new IllegalArgumentException("Size should be a multiple of the number of buckets");
        }
        double lSize = side / buckets;
        xStart = Math.floor(Math.floor(xCoord / lSize) * lSize);
        yStart = Math.floor(Math.floor(yCoord / lSize) * lSize);

        xStart = grid.isPeriodic() ? (xStart % side + side) % side : Math.max(0, Math.min(xStart, side));
        yStart = grid.isPeriodic() ? (yStart % side + side) % side : Math.max(0, Math.min(yStart, side));
    }

    public Cell2D getNeighbor(int x, int y) {
        double delta = side / buckets;
        Cell2D newCell = new Cell2D(this.grid, xStart + delta * x, yStart + delta * y);
        return newCell;
    }

    public Stream<? extends Cell> semisphereNeighborhood() {
        Cell2D upperCell = this.getNeighbor(0, 1);
        Cell2D upperRightCell = this.getNeighbor(1, 1);
        Cell2D rightCell = this.getNeighbor(1, 0);
        Cell2D bottomRightCell = this.getNeighbor(1, -1);
        return Stream.of(this, upperCell, upperRightCell, rightCell, bottomRightCell).filter(Objects::nonNull);
    }
}