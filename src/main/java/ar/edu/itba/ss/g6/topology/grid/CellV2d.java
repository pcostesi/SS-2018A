package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;

import java.util.Objects;
import java.util.stream.Stream;

public class CellV2d<T extends TheParticle> implements Cell {
    private long side;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CellV2d cell2D = (CellV2d) o;

        if (side != cell2D.side) {
            return false;
        }
        if (buckets != cell2D.buckets) {
            return false;
        }
        if (Double.compare(cell2D.xStart, xStart) != 0) {
            return false;
        }
        return Double.compare(cell2D.yStart, yStart) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (side ^ (side >>> 32));
        result = 31 * result + buckets;
        temp = Double.doubleToLongBits(xStart);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yStart);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public CellV2d(Grid grid, TheParticle particle) {
        this(grid, particle.getPosition().getX(), particle.getPosition().getY());
    }


    private CellV2d(Grid grid, double xCoord, double yCoord) {
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

    public CellV2d getNeighbor(int x, int y) {
        long delta = side / buckets;
        CellV2d newCell = new CellV2d(this.grid, xStart + delta * x, yStart + delta * y);
        return newCell;
    }

    public Stream<? extends Cell> semisphereNeighborhood() {
        CellV2d upperCell = this.getNeighbor(0, 1);
        CellV2d upperRightCell = this.getNeighbor(1, 1);
        CellV2d rightCell = this.getNeighbor(1, 0);
        CellV2d bottomRightCell = this.getNeighbor(1, -1);
        return Stream.of(this, upperCell, upperRightCell, rightCell, bottomRightCell).filter(Objects::nonNull);
    }
}