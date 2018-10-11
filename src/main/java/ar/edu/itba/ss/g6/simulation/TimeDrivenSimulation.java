package ar.edu.itba.ss.g6.simulation;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public interface TimeDrivenSimulation<T extends Particle, F extends SimulationFrame<T>> extends Simulation<T, F> {
    double getDeltaT();

    double getFPS();

    default boolean shouldCaptureFrame(double timestamp) {
        return timestamp % (1 / getFPS()) < getDeltaT();
    }

    default int totalFrameCount(double length) {
        return (int) Math.floor(length * getFPS()) + 1;
    }

    default int frameNumber(SimulationFrame<T> frame) {
        return frameNumber(frame.getTimestamp());
    }

    default int frameNumber(double timestamp) {
        return shouldCaptureFrame(timestamp) ? (int) Math.floor(timestamp * getFPS()) + 1 : -1;
    }

    double getMaxHeight();

    void resetNormalForce();
}
