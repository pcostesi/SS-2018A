package ar.edu.itba.ss.g6.tp.tp4;

public class TrajectoryData {
    public final static int ORDER = 5;
    private final double[] rx;
    private final double[] ry;
    private final String id;
    private final double mass;
    private final double radius;

    public double[] getRx() {
        return rx;
    }

    public double[] getRy() {
        return ry;
    }

    public String getId() {
        return id;
    }

    public TrajectoryData(double[] rx, double[] ry, String id, double mass, double radius) {
        this.rx = rx;
        this.ry = ry;
        this.id = id;
        this.mass = mass;
        this.radius = radius;
    }

    public double[] getAxis(Axis axis) {
        if (axis.equals(Axis.X)) {
            return getRx();
        } else if (axis.equals(Axis.Y)) {
            return getRy();
        }
        throw new IllegalArgumentException();
    }

    public double getMass() {
        return mass;

    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrajectoryData that = (TrajectoryData) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    double getSpeed() {
        return Math.sqrt(Math.pow(getRx()[1], 2) + Math.pow(getRy()[1], 2));
    }
}
