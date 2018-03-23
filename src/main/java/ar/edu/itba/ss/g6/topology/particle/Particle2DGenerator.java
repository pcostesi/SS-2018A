package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.grid.Grid;

import java.util.HashSet;
import java.util.Set;

public class Particle2DGenerator implements ParticleGenerator<Particle2D> {
    private final double radius;
    private final long side;
    private final int amount;

    public Particle2DGenerator(double radius, long side, int amount) {
        this.radius = radius;
        this.side = side;
        this.amount = amount;
    }

    @Override
    public Set<Particle2D> generate() {
        Set<Particle2D> particles = new HashSet<>();
        for (int idx = 0; idx < amount; idx++) {
            double x = Math.random() * side;
            double y = Math.random() * side;
            particles.add(new Particle2D(String.valueOf(idx), radius, x, y));
        }
        return particles;
    }
}
