package ar.edu.itba.ss.g6.topology.vector;

import mikera.vectorz.Vector2;

public class V2d2 {

    private final Vector2 vector;

    public V2d2(double x, double y) {
        this.vector = Vector2.of(x, y);
    }

    private V2d2(Vector2 vector) {
        this.vector = vector;
    }

    public V2d2 scale(double scale) {
        Vector2 v2 = vector.clone();
        v2.scale(scale);
        return new V2d2(v2);
    }

    public V2d2 add(final V2d2 v2) {
        Vector2 v = vector.clone();
        v.add(v2.vector);
        return new V2d2(v);
    }

    public V2d2 substract(V2d2 v2) {
        Vector2 v = vector.clone();
        v.sub(v2.vector);
        return new V2d2(v);
    }

    public V2d2 normalize() {
        Vector2 v = vector.toNormal();
        return new V2d2(v);
    }

    public double module() {
        return vector.magnitude();
    }

    public double dot(final V2d2 v2) {
        return vector.dotProduct(v2.vector);
    }

    public double distance(final V2d2 v2) {
        return v2.vector.distance(vector);
    }

    public double distance2(final V2d2 v2) {
        return v2.vector.distanceSquared(vector);
    }



    @Override
    public String toString() {
        return "V2d [x=" + vector.getX() + ", y=" + vector.getY() + "]";
    }

    public double getX() {
        return vector.getX();
    }

    public double getY() {
        return vector.getY();
    }
}