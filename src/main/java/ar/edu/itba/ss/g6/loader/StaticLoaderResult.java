package ar.edu.itba.ss.g6.loader;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Set;

public class StaticLoaderResult<T extends Particle> {
    private final int particleCount;
    private final String header;
    private final Set<T> particles;

    public StaticLoaderResult(int particleCount, String header, Set<T> particles) {
        this.particleCount = particleCount;
        this.header = header;
        this.particles = particles;
    }

    public int getParticleCount() {

        return particleCount;
    }

    public String getHeader() {
        return header;
    }

    public Set<T> getParticles() {
        return particles;
    }
}
