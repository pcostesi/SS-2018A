package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.grid.Grid;

import java.util.Set;

public class DynamicParticle2DGenerator implements ParticleGenerator<DynamicParticle2D> {
    private final double radius;
    private final long side;
    private final double speed;
    private final int amount;

    @Override
    public Set<DynamicParticle2D> generate(Grid<DynamicParticle2D> grid) {
        int particles = 0;
        while ((particles = grid.countParticles()) < amount) {
            double x = Math.random() * side;
            double y = Math.random() * side;
            double theta = Math.random();
            double vx = speed * Math.cos(theta);
            double vy = speed * Math.sin(theta);
            DynamicParticle2D particle = new DynamicParticle2D(String.valueOf(particles), x, y, vx, vy, radius);
            if (grid.getNeighbors(particle, 0).isEmpty()) {
                grid.place(particle);
            }
        }
        return grid.getParticles();
    }

    public DynamicParticle2DGenerator(double speed, double radius, long side, int amount) {
        this.radius = radius;
        this.side = side;
        this.amount = amount;
        this.speed = speed;
    }
}
