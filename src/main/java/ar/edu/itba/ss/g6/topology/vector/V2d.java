package ar.edu.itba.ss.g6.topology.vector;

public class V2d {

    public double x;
    public double y;

    public V2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public V2d scale(double scale) {
        return new V2d(x * scale, y * scale);
    }

    public V2d add(final V2d v2) {
        return new V2d(x + v2.x, y + v2.y);
    }

    public V2d substract(V2d v2) {
        return new V2d(x - v2.x, y - v2.y);
    }

    public V2d normalize() {
        double module = module();
        return new V2d(x / module, y / module);
    }

    public double module() {
        return Math.sqrt(x * x + y * y);
    }

    public double dot(final V2d v2) {
        return x * v2.x + y * v2.y;
    }

    public double cross(final V2d v2){
        return y*v2.x - x*v2.y;
    }

    public double distance(final V2d v2) {
        return Math.sqrt(distance2(v2));
    }

    public double distance2(final V2d v2) {
        double distX = v2.x - x;
        double distY = v2.y - y;
        return distX * distX + distY * distY;
    }

    @Override
    public String toString() {
        return "V2d [x=" + x + ", y=" + y + "]";
    }

    public V2d add(double s) {
        return new V2d(x + s, y + s);
    }

    public V2d rotateCounterClockwise(double angle) {
        double theta = Math.toRadians(angle);
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new V2d(x * cos - y * sin, x * sin + y * cos);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public V2d sign(){
        return new V2d(Math.signum(x), Math.signum(y));
    }
}