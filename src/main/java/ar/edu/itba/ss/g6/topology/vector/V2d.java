package ar.edu.itba.ss.g6.topology.particle;

public class Vector2d {

    public double x;
    public double y;

    public Vector2d (double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d scale(double scale) {
        return new Vector2d(x * scale, y * scale);
    }

    public Vector2d add(final Vector2d v2) {
        return new Vector2d(x + v2.x, y + v2.y);
    }

    public Vector2d substract(Vector2d v2) {
        return new Vector2d(x - v2.x, y - v2.y);
    }

    public Vector2d normalize() {
        double module = module();
        return new Vector2d(x / module, y / module);
    }

    public double module() {
        return Math.sqrt(x * x + y * y);
    }

    public double dot(final Vector2d v2) {
        return x * v2.x + y * v2.y;
    }

    public double distance(final Vector2d v2) {
        double distX = v2.x - x;
        double distY = v2.y - y;
        return Math.sqrt(distX * distX + distY * distY);
    }

    @Override
    public String toString() {
        return "Vector2d [x=" + x + ", y=" + y + "]";
    }

    public Vector2d add(double s) {
        return new Vector2d(x + s, y + s);
    }

    public Vector2d rotateCounterClockwise(double angle) {
        double theta = Math.toRadians(angle);
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vector2d(x * cos - y * sin, x * sin + y * cos);
    }

}