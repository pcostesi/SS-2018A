package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Arrays;

public interface ForceSimulator {
    TrajectoryData moveParticle(TrajectoryData target, TrajectoryData... system);

    default TrajectoryData[] move(TrajectoryData... particles) {
        TrajectoryData[] newParticles = new TrajectoryData[particles.length];
        for (int i = 0; i < particles.length; i++) {
            newParticles[i] = moveParticle(particles[i], particles);
        }
        return newParticles;
    }

    TrajectoryData[] initParticles(WeightedDynamicParticle2D... system);

    double computeForceInAxis(Axis axis, TrajectoryData body1, TrajectoryData body2);

    default double computeEffectiveForceInAxis(Axis axis, TrajectoryData target, TrajectoryData... system) {
        double force = 0;
        for (TrajectoryData particle : system) {
            force += computeForceInAxis(axis, target, particle);
        }
        return force;
    }
}
