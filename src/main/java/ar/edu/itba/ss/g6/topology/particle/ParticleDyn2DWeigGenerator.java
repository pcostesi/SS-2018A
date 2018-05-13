package ar.edu.itba.ss.g6.topology.particle;

import java.util.HashSet;
import java.util.Set;

public class ParticleDyn2DWeigGenerator implements ParticleGenerator<TheParticle> {

    double weight;
    double W;
    double L;
    int N;
    double MAX_DIAMETER = 0.03;  // m
    double MIN_DIAMETER =  0.02; // m
    

    public ParticleDyn2DWeigGenerator(double weight, double w, double l, int n, double minDiameter, double maxDiameter) {
        this.weight = weight;
        this.W = w;
        this.L = l;
        this.N = n;
        this.MAX_DIAMETER = maxDiameter;
        this.MIN_DIAMETER = minDiameter;
    }

    @Override
    public Set<TheParticle> generate() {
        Set<TheParticle> particles = new HashSet<>();
        while (particles.size() < N) {
            final double particleRadius = (Math.random() * (MAX_DIAMETER - MIN_DIAMETER) + MIN_DIAMETER) / 2;
            final double particleX = Math.random() * (W - 2 * particleRadius) + particleRadius;
            final double particleY = Math.random() * (L - 2 * particleRadius) + particleRadius;
            final TheParticle newParticle = new TheParticle(String.valueOf(particles.size()),
                    particleX, particleY, 0, 0, particleRadius, weight);
            boolean collidedAnotherParticle = false;
            for (TheParticle particle: particles) {
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
