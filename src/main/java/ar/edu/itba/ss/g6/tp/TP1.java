package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.topology.grid.Grid;
import ar.edu.itba.ss.g6.topology.grid.MapGrid2D;
import ar.edu.itba.ss.g6.topology.particle.Particle2D;
import ar.edu.itba.ss.g6.topology.particle.Particle2DGenerator;
import ar.edu.itba.ss.g6.topology.particle.ParticleGenerator;

import java.util.Set;

public class TP1 {

    public static void main(String ...args) {
        double speed = 0.3;
        double radius = 1;
        long side = 300;
        int amount = 1000;
        int buckets = 50;
        boolean isPeriodic = true;

        ParticleGenerator<Particle2D> generator = new Particle2DGenerator(radius, side, amount);
        Grid<Particle2D> grid = new MapGrid2D<>(side, buckets, isPeriodic);

        Set<Particle2D> particles = generator.generate(grid);
        particles.stream().forEach(p -> System.out.println(p));
    }
}
