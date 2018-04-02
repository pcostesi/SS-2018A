package ar.edu.itba.ss.g6.tp.TP3;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Collections;
import java.util.Set;

public class BrownianMovementSimulationFrame implements SimulationFrame<WeightedDynamicParticle2D> {
    private final double timestamp;
    private final Set<WeightedDynamicParticle2D> deltas;
    private final Set<WeightedDynamicParticle2D> state;

    public BrownianMovementSimulationFrame(double timestamp, Set<WeightedDynamicParticle2D> deltas, Set<WeightedDynamicParticle2D> state) {
        this.timestamp = timestamp;
        this.deltas = deltas;
        this.state = state;
    }

    @Override
    public double getTimestamp() {
        return 0;
    }

    @Override
    public Set<WeightedDynamicParticle2D> getDelta() {
        return null;
    }

    @Override
    public Set<WeightedDynamicParticle2D> getState() {
        return null;
    }
}
