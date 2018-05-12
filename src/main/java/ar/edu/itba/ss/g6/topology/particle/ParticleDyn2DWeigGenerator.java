package ar.edu.itba.ss.g6.topology.particle;

import java.util.HashSet;
import java.util.Set;

public class ParticleDyn2DWeigGenerator implements ParticleGenerator<WeightedDynamicParticle2D> {

    double weight;
    double W;
    double L;
    int N;
    double MAX_DIAMETER = 0.03;  // m
    double MIN_DIAMETER =  0.02; // m
    

    public ParticleDyn2DWeigGenerator(double weight, double w, double l, int n) {
        this.weight = weight;
        this.W = w;
        this.L = l;
        this.N = n;
    }

    @Override
    public Set<WeightedDynamicParticle2D> generate() {
        Set<WeightedDynamicParticle2D> particles = new HashSet<>();
        while (particles.size() < N) {
            final double particleRadius = (Math.random() * (MAX_DIAMETER - MIN_DIAMETER) + MIN_DIAMETER) / 2;
            final double particleX = Math.random() * (W - 2 * particleRadius) + particleRadius;
            final double particleY = Math.random() * (L - 2 * particleRadius) + particleRadius;
            final WeightedDynamicParticle2D newParticle = new WeightedDynamicParticle2D(String.valueOf(particles.size()),
                    particleX, particleY, 0, 0, particleRadius, weight);
            boolean collidedAnotherParticle = false;
            for (WeightedDynamicParticle2D particle: particles) {
                if (particle.collides(newParticle)) {
                    collidedAnotherParticle = true;
                    break;
                }
            }
            if (collidedAnotherParticle) {
                continue;
            } else {
                particles.add(newParticle);
            }
        }
        return particles;
    }
}
