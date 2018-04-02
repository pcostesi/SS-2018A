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
        if (!(obj instanceof Particle)) {
            return false;
        }
        return id.equals(((Particle) obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Particle2D <%5s> (%.3e, %.3e) rad %.3e",
            getId(), getXCoordinate(), getYCoordinate(), getRadius());
    }

    @Override
    public String[] values() {
        return new String[] {
            getId(),
            String.format("%3f", getXCoordinate()),
            String.format("%3f", getYCoordinate()),
            String.format("%3f", getRadius())
        };
    }

    @Override
    public boolean overlapsWith(Particle p) {
        if (!(p instanceof Particle2D)) {
            throw new IllegalArgumentException("Wrong class :P");
        }
        Particle2D particle = (Particle2D) p;
        double distanceInX = this.getXCoordinate() - particle.getXCoordinate();
        double distanceInY = this.getYCoordinate() - particle.getYCoordinate();
        double sumOfRadius = this.getRadius() + particle.getRadius();
        double rawDistance = Math.sqrt(distanceInX * distanceInX + distanceInY * distanceInY);

        return rawDistance < sumOfRadius;
    }

    public Particle2D(String id, double radius, double xCoord, double yCoord) {
        this.yCoord = yCoord;
        this.xCoord = xCoord;
        this.radius = radius;
        this.id = id;
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null");
        }
    }
}
