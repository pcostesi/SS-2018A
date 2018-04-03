package ar.edu.itba.ss.g6.topology.particle;

public class DynamicParticle2D extends Particle2D implements DynamicParticle {
    private double xSpeed;
    private double ySpeed;

    @Override
    public double getSpeed() {
        return Math.sqrt(getYSpeed() * getYSpeed() + getXSpeed()* getXSpeed());
    }

    @Override
    public <T extends DynamicParticle> double timeToCollision(T other) {
        if (!(other instanceof DynamicParticle2D)) {
            throw new IllegalArgumentException("Wrong particle type :)");
        }
        double timeToCollision;
        DynamicParticle2D p = (DynamicParticle2D) other;
        double sigma = this.getRadius() + p.getRadius();
        double DeltaRX = this.getXCoordinate() - p.getXCoordinate();
        double DeltaRY = this.getYCoordinate() - p.getYCoordinate();
        double DeltaVX = this.getXSpeed() - p.getXSpeed();
        double DeltaVY = this.getYSpeed() - p.getYSpeed();
        double DeltaRDotDeltaR = DeltaRX*DeltaRX + DeltaRY*DeltaRY;
        double DeltaVDotDeltaV = DeltaVX*DeltaVX + DeltaVY*DeltaVY;
        double DeltaVDotDeltaR = DeltaVX*DeltaRX + DeltaVY*DeltaRY;
        double delta = (DeltaVDotDeltaR*DeltaVDotDeltaR) - DeltaVDotDeltaV*(DeltaRDotDeltaR-sigma*sigma);
        if(DeltaVDotDeltaR >= 0) {
            timeToCollision = Integer.MAX_VALUE;
        }else if(delta < 0){
            timeToCollision = Integer.MAX_VALUE;
        }else {
            timeToCollision =  (-1) * ( (DeltaVDotDeltaR + Math.sqrt(delta)) / (DeltaVDotDeltaV));
        }
        return timeToCollision < 0 ? Integer.MAX_VALUE : timeToCollision;
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
}
