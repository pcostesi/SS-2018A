package ar.edu.itba.ss.g6.tp.tp3;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ParticleGenerator {

    static int PAPA_PARTICULA_WEIGHT_MULTIPLIER = 1000;
    static int PAPA_PARTICULA_SIZE_MULTIPLIER = 10;
    private double mapSize = 0.5;
    private double papaParticulaRadius = 0.05;
    private double papaParticulaMass = 100;
    private double commonParticleRadius = 0.005;
    private double commonParticleMass = 0.1;
    private double vmaxmod = 0.1;
    private double papaParticulaSpeed = 0;
    private int MAX_TRIES = Integer.MAX_VALUE >> 1;
    private Random generator;

    public ParticleGenerator(double worldSize, double maxSpeed, double weight, double radius){
        this(worldSize, maxSpeed, weight, radius, System.currentTimeMillis());
    }

    public ParticleGenerator(double worldSize, double maxSpeed, double weight, double radius, long seed){
        this.mapSize = worldSize;
        this.vmaxmod = maxSpeed;
        this.commonParticleMass = weight;
        this.commonParticleRadius = radius;
        this.papaParticulaRadius = radius * PAPA_PARTICULA_SIZE_MULTIPLIER; //TODO check this
        this.papaParticulaMass = weight * PAPA_PARTICULA_WEIGHT_MULTIPLIER; //TODO check this
        this.generator = new Random(seed);
    }

    private boolean collides(Set<WeightedDynamicParticle2D> particles, WeightedDynamicParticle2D particle) {
        return particles.parallelStream().filter(p -> particle.overlapsWith(p)).count() > 0;
    }

    private double getDoubleFromRange(double minRange, double maxRange) {
        return generator.nextDouble() * (maxRange - minRange) + minRange;
    }

    public Set<WeightedDynamicParticle2D> getParticles(int numberOfParticles) {
        Set<WeightedDynamicParticle2D> particles = new HashSet<>();
        double xpos = getDoubleFromRange(papaParticulaRadius, mapSize - papaParticulaRadius);
        double ypos = getDoubleFromRange(papaParticulaRadius, mapSize - papaParticulaRadius);
        WeightedDynamicParticle2D papaParticula = new WeightedDynamicParticle2D("0", xpos, ypos, papaParticulaSpeed, papaParticulaSpeed, papaParticulaRadius, papaParticulaMass);

        particles.add(papaParticula);

        int i = 1;
        int tries = 0;
        while (i < numberOfParticles + 1) {
            double x = getDoubleFromRange(commonParticleRadius, mapSize - commonParticleRadius);
            double y = getDoubleFromRange(commonParticleRadius, mapSize - commonParticleRadius);
            double vx = getDoubleFromRange(-vmaxmod, vmaxmod);
            double vy = getDoubleFromRange(-vmaxmod, vmaxmod);
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
