package cellIndexMethod;

public class StaticParticle implements Particle{
    public double radius;
    public double xPosition;
    public double yPosition;

    public StaticParticle(double radius, double xPosition, double yPosition) {
        this.radius = radius;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
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
}
