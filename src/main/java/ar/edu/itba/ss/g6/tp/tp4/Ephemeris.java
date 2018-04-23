package ar.edu.itba.ss.g6.tp.tp4;

public class Ephemeris {
    String id;
    double rx;
    double ry;
    double vx;
    double vy;
    double mass;
    double radius;

    public String getId() {
        return id;
    }

    public Ephemeris setId(String id) {
        this.id = id;
        return this;
    }

    public double getRx() {
        return rx;
    }

    public Ephemeris setRx(double rx) {
        this.rx = rx;
        return this;
    }

    public double getRy() {
        return ry;
    }

    public Ephemeris setRy(double ry) {
        this.ry = ry;
        return this;
    }

    public double getVx() {
        return vx;
    }

    public Ephemeris setVx(double vx) {
        this.vx = vx;
        return this;
    }

    public double getVy() {
        return vy;
    }

    public Ephemeris setVy(double vy) {
        this.vy = vy;
        return this;
    }

    public double getMass() {
        return mass;
    }

    public Ephemeris setMass(double mass) {
        this.mass = mass;
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public Ephemeris setRadius(double radius) {
        this.radius = radius;
        return this;
    }
}
