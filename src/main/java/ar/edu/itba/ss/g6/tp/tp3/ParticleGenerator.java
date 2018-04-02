package ar.edu.itba.ss.g6.tp.tp3;

import ar.edu.itba.ss.g6.topology.particle.OverfedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.HashSet;
import java.util.Set;

public class ParticleGenerator {
    private double mapSize = 0.5;
    private double papaParticulaRadius = 0.05;
    private double papaParticulaMass = 100;
    private double commonParticleRadius = 0.005;
    private double commonParticleMass = 0.1;
    private double vmaxmod = 0.1;
    private double papaParticulaSpeed = 0;
    private int MAX_TRIES = Integer.MAX_VALUE >> 1;


    private boolean collides(Set<WeightedDynamicParticle2D> particles, WeightedDynamicParticle2D particle) {
        return particles.parallelStream().filter(p -> particle.overlapsWith(p)).count() > 0;
    }

    public Set<WeightedDynamicParticle2D> getParticles(int numberOfParticles) {
        Set<WeightedDynamicParticle2D> particles = new HashSet<>();
        double xpos = Math.random() * (mapSize - papaParticulaRadius * 2) + papaParticulaRadius;
        double ypos = Math.random() * (mapSize - papaParticulaRadius * 2) + papaParticulaRadius;
        WeightedDynamicParticle2D papaParticula = new WeightedDynamicParticle2D("0", xpos, ypos, papaParticulaSpeed, papaParticulaSpeed, papaParticulaRadius, papaParticulaMass);

        particles.add(papaParticula);

        int i = 1;
        int tries = 0;
        while (i < numberOfParticles + 1) {
            double x = Math.random() * (mapSize - commonParticleRadius * 2) + commonParticleRadius;
            double y = Math.random() * (mapSize - commonParticleRadius * 2) + commonParticleRadius;
            double vx = ((Math.random() * 2) - 1) * vmaxmod;
            double vy = ((Math.random() * 2) - 1) * vmaxmod;
            WeightedDynamicParticle2D p = new WeightedDynamicParticle2D(String.valueOf(i), x, y, vx, vy, commonParticleRadius, commonParticleMass);

            if (!collides(particles, p)) {
                particles.add(p);
                i++;
                tries = 0;
            } else {
                tries++;
            }

            if (tries > MAX_TRIES) {
                throw new IllegalArgumentException("Too many particles for such a small place...");
            }
        }
        return particles;
    }
}
