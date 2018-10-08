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
        return particle.distanceTo(this) < 0;
    }

    public double distanceTo(Particle2D particle) {
        double distanceX = this.getXCoordinate() - particle.getXCoordinate();
        double distanceY = this.getYCoordinate() - particle.getYCoordinate();
        double radiusDistance = this.getRadius() + particle.getRadius();
        double rawDistance =  Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        return rawDistance - radiusDistance;
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
