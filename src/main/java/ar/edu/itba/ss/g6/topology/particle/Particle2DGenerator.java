package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.grid.Grid;

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
    public Set<Particle2D> generate(Grid<Particle2D> grid) {
        int particles;
        while ((particles = grid.countParticles()) < amount) {
            double x = Math.random() * side;
            double y = Math.random() * side;
            Particle2D particle = new Particle2D(String.valueOf(particles), x, y, radius);
            grid.place(particle);
        }
        return grid.getParticles();
    }
}
