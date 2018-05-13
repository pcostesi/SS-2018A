package ar.edu.itba.ss.g6.topology.geometry;

import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;

public class Wall {
    // We're gonna build a wall and we're gonna build it big and tall.
    // We're gonna build a wall and we're gonna make Mexico pay for it.
    // It's gonna be beautiful, it's gonna be the best wall, believe me.

    private final V2d p0;
    private final V2d p1;

    public Wall(final V2d p0, final V2d p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    public V2d getP0() {
        return p0;
    }

    public V2d getP1() {
        return p1;
    }

    public V2d intersection(final TheParticle particle) {
        return distanceLinePoint(particle.getPosition());
    }

    @Override
    public String toString() {
        return "Wall [p0=" + p0 + ", p1=" + p1 + "]";
    }


    public V2d distanceLinePoint(final V2d point) {

        double a = point.x - p0.x;
        double b = point.y - p0.y;
        double c = p1.x - p0.x;
        double d = p1.y - p0.y;

        double dot = a * c + b * d;
        double lenSq = c * c + d * d;
        double param = -1;

        if (lenSq != 0) { //in case of 0 length line
            param = dot / lenSq;
        }

        if (param < 0) {
            return new V2d(p0.x, p0.y);
        } else if (param > 1) {
            return new V2d(p1.x, p1.y);
        } else {
            return new V2d(p0.x + param * c, p0.y + param * d);
        }
    }

}
