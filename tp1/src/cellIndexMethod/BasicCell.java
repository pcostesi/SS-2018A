package cellIndexMethod;

import java.util.List;

public class BasicCell implements Cell {

    private int position;
    private List<Particle> particles;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public BasicCell(int position, List<Particle> particles, double minX, double maxX, double minY, double maxY) {
        this.position = position;
        this.particles = particles;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public List<Particle> getParticleList() {
        return particles;
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }
}
