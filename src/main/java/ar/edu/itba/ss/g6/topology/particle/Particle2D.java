package ar.edu.itba.ss.g6.topology.particle;

public class Particle2D implements Particle {
    private final double radius;
    private final double xCoord;
    private final double yCoord;
    private final String id;

    public double getXCoordinate() {
        return xCoord;
    }

    public double getYCoordinate() {
        return yCoord;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(obj);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean isWithinRadius(double radius, Particle p) {
        if (!(p instanceof Particle2D)) {
            throw new IllegalArgumentException("Provided particle is not an instance of a comparable type");
        }

        double distanceInX = this.getXCoordinate() - ((Particle2D) p).getXCoordinate();
        double distanceInY = this.getYCoordinate() - ((Particle2D) p).getYCoordinate();
        double sumOfRadius = this.getRadius() + p.getRadius();
        double rawDistance = Math.sqrt(distanceInX * distanceInX + distanceInY * distanceInY);

        return rawDistance - sumOfRadius < radius;
    }

    public Particle2D(String id, double radius, double xCoord, double yCoord) {
        this.yCoord = yCoord;
        this.xCoord = xCoord;
        this.radius = radius;
        this.id = id;
    }
}
