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

    public V2d intersection(final TheParticle particle) {
        return distanceLinePoint(particle.getPosition());
    }

    @Override
    public String toString() {
        return "Wall [p0=" + p0 + ", p1=" + p1 + "]";
    }


    public V2d distanceLinePoint(final V2d point) {
        V2d v1 = point.substract(p0);
        V2d vp = p1.substract(p0);
        double dot = v1.dot(vp);
        double len = vp.module() * vp.module();
        double param = -1;

        if (len != 0) { //in case of 0 length line
            param = dot / len;
        }

        if (param < 0) {
            return p0;
        } else if (param > 1) {
            return p1;
        } else {
            return vp.scale(param).add(p0);
        }
    }

}
