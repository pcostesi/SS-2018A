package cellIndexMethod;

import jdk.jshell.spi.ExecutionControl;

public class StaticParticle implements Particle{
    private double radius;
    private double xPosition;
    private double yPosition;
    private int id;

    public StaticParticle(double radius, double xPosition, double yPosition, int id) {
        this.radius = radius;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getxPosition() {
        return xPosition;
    }

    @Override
    public double getyPosition() {
        return yPosition;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Particle updatePosition(double timeDelta, double theta) {
        return null;
    }

    @Override
    public double getAngle() {
        throw new Error("Static particles cannot move");
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public double getxSpeed() {
        return 0;
    }

    @Override
    public double getySpeed() {
        return 0;
    }
}
