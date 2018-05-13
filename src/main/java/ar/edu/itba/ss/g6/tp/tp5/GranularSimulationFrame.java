package ar.edu.itba.ss.g6.tp.tp5;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;

public class GranularSimulationFrame implements SimulationFrame<TheParticle> {

    private final double timestamp;
    private final Set<TheParticle> state;


    public GranularSimulationFrame(double timestamp, Set<TheParticle> state) {
        this.timestamp = timestamp;
        this.state = state;
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public Set<TheParticle> getDelta() {
        return getState();
    }

    @Override
    public Set<TheParticle> getState() {
        return state;
    }
}
