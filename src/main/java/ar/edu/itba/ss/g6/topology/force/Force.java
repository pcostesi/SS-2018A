package ar.edu.itba.ss.g6.topology.force;

import ar.edu.itba.ss.g6.topology.geometry.Wall;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;

public interface Force {

    V2d getForce(TheParticle particle, TheParticle other);

    V2d getForce(TheParticle particle, Wall wall);

}
