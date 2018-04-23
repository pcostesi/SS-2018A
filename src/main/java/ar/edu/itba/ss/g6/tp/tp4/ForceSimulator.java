package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Arrays;

public interface ForceSimulator {
    TrajectoryData moveParticle(TrajectoryData target, TrajectoryData... system);

    default TrajectoryData[] move(TrajectoryData... particles) {
        return Arrays.stream(particles)
            .map(target -> moveParticle(target, particles))
            .toArray(n -> new TrajectoryData[n]);
    }

    TrajectoryData[] initParticles(WeightedDynamicParticle2D... system);

    double computeForceInAxis(Axis axis, TrajectoryData body1, TrajectoryData body2);

    default double computeEffectiveForceInAxis(Axis axis, TrajectoryData target, TrajectoryData... system) {
        return Arrays.stream(system)
            .mapToDouble(particle -> computeForceInAxis(axis, target, particle))
            .sum();
    }
}
