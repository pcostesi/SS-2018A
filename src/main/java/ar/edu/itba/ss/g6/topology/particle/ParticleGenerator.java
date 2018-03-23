package ar.edu.itba.ss.g6.topology.particle;

import java.util.Set;

public interface ParticleGenerator<T extends Particle> {
    Set<T> generate();
}
