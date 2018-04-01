package ar.edu.itba.ss.g6.topology.particle;

public class DynamicParticle2D extends Particle2D implements DynamicParticle {
    private double xSpeed;
    private double ySpeed;

    @Override
    public double getSpeed() {
        return Math.sqrt(getYSpeed() * getYSpeed() + getXSpeed()* getXSpeed());
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public double getDirection() {
        return Math.atan2(getYSpeed(), getXSpeed());
    }

    @Override
    public String getId() {
        return null;
    }

    public DynamicParticle2D(String id, double x, double y, double vx, double vy, double radius) {
        super(id, x, y, radius);
        this.xSpeed = vx;
        this.ySpeed = vy;
    }
}
