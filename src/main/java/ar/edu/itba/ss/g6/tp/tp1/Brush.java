package ar.edu.itba.ss.g6.tp.tp1;

import ar.edu.itba.ss.g6.topology.particle.ColoredParticle2D;
import ar.edu.itba.ss.g6.topology.particle.Particle2D;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Brush {
    Set<ColoredParticle2D> particles;

    public Brush(Set<Particle2D> all, int r, int g, int b) {
        particles = all.parallelStream()
            .map(particle -> new ColoredParticle2D(particle.getId(), particle.getRadius(), particle.getXCoordinate(),
                particle.getYCoordinate(), String.format("%d\t%d\t%d", r, g, b)))
            .collect(Collectors.toSet());
    }

    public Brush paint(Optional<Particle2D> particle, int r, int g, int b) {
        particle.ifPresent(p -> {
            particles.forEach(s -> {
                if (s.getId().equals(p.getId())) {
                    s.colorize(String.format("%d\t%d\t%d", r, g, b));
                }
            });
        });
        return this;
    }

    public Brush paint(Set<Particle2D> selected, int r, int g, int b) {
        particles.parallelStream()
         .filter(selected::contains)
         .forEach(particle -> particle.colorize(String.format("%d\t%d\t%d", r, g, b)));
        return this;
    }

    public Set<ColoredParticle2D> all() {
        return particles;
    }
}
