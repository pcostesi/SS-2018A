package ar.edu.itba.ss.g6.loader;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public interface ParticleLoader<T extends Particle> {
    T fromStringValues(String[] values);
}
