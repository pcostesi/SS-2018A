package ar.edu.itba.ss.g6.tp.tp5;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;

public class GranularSimulationFrame implements SimulationFrame<WeightedDynamicParticle2D> {

    private final double timestamp;
    private final Set<WeightedDynamicParticle2D> state;


    public GranularSimulationFrame(double timestamp, Set<WeightedDynamicParticle2D> state) {
        this.timestamp = timestamp;
        this.state = state;
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public Set<WeightedDynamicParticle2D> getDelta() {
        return getState();
    }

    @Override
    public Set<WeightedDynamicParticle2D> getState() {
        return state;
    }
}
