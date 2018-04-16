package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.HashSet;
import java.util.Set;

public class ArmonicSimulationFrame implements SimulationFrame{

    double timestamp;
    Set<WeightedDynamicParticle2D> state;

    public ArmonicSimulationFrame(double timestamp, WeightedDynamicParticle2D particle) {
        this.timestamp = timestamp;
        this.state = new HashSet<WeightedDynamicParticle2D>();
        state.add(particle);
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public Set<WeightedDynamicParticle2D> getDelta() {
        return state;
    }

    @Override
    public Set<WeightedDynamicParticle2D> getState() {
        return state;
    }
}
