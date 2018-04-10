package ar.edu.itba.ss.g6.topology.particle;

public class DynamicParticle2D extends Particle2D implements DynamicParticle {
    private double xSpeed;
    private double ySpeed;
    private double xAcceleration;
    private double yAcceleration;

    @Override
    public double getSpeed() {
        return Math.sqrt(getYSpeed() * getYSpeed() + getXSpeed()* getXSpeed());
    }

    @Override
    public <T extends DynamicParticle> double timeToCollision(T other) {
        if (!(other instanceof DynamicParticle2D)) {
            throw new IllegalArgumentException("Wrong particle type :)");
        }
        DynamicParticle2D p = (DynamicParticle2D) other;
        double deltaVX = this.getXSpeed() - p.getXSpeed();
        double deltaVY = this.getYSpeed() - p.getYSpeed();
        double deltaX = this.getXCoordinate() - p.getXCoordinate();
        double deltaY = this.getYCoordinate() - p.getYCoordinate();
        double auxVR = deltaX * deltaVX + deltaY * deltaVY;
        double auxVV = Math.pow(deltaVX, 2) +  Math.pow(deltaVY, 2);
        double auxRR = Math.pow(deltaX, 2) + Math.pow(deltaY, 2);

        if(auxVR >= 0){
            return Double.POSITIVE_INFINITY;
        }

        double d = Math.pow((auxVR),2) - auxVV * (auxRR - Math.pow(this.getRadius() + p.getRadius(),2));

        if (d < 0){
            return Double.POSITIVE_INFINITY;
        }

        return -(auxVR + Math.sqrt(d))/auxVV;
    }

    @Override
    public double timeToX(double xLimit) {
        return (xLimit - this.getRadius() - this.getXCoordinate()) / this.getXSpeed();
    }

    @Override
    public double timeToY(double yLimit) {
        return (yLimit - this.getRadius() - this.getYCoordinate()) / this.getYSpeed();
    }

    @Override
    public double getAcceleration() {
        return Math.sqrt(xAcceleration*xAcceleration + yAcceleration*yAcceleration);
    }

    public double getxAcceleration() {
        return xAcceleration;
    }

    public double getyAcceleration() {
        return yAcceleration;
    }

    @Override
    public String toString() {
        return String.format("DynamicParticle2D <%5s> (%.3e, %.3e) -> (vx: %.3e, vy: %.3e) rad %.3e",
         getId(), getXCoordinate(), getYCoordinate(), getXSpeed(), getYSpeed(), getRadius());
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

    public DynamicParticle2D(String id, double x, double y, double vx, double vy, double radius) {
        super(id, radius, x, y);
        this.xSpeed = vx;
        this.ySpeed = vy;
    }

    public DynamicParticle2D(String id, double x, double y, double vx, double vy, double ax, double ay, double radius) {
        super(id, radius, x, y);
        this.xSpeed = vx;
        this.ySpeed = vy;
        this.xAcceleration = ax;
        this.yAcceleration = ay;
    }
}
