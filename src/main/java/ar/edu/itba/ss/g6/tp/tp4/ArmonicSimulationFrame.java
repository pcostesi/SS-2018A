package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedParticle;

import java.util.HashSet;
import java.util.Set;

public class ArmonicSimulationFrame implements SimulationFrame{

    double timestamp;
    Set<WeightedParticle> state;

    public ArmonicSimulationFrame(double timestamp, WeightedDynamicParticle2D particle) {
        this.timestamp = timestamp;
        this.state = new HashSet<WeightedParticle>();
        state.add(particle);
    }

    @Override
    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public Set getDelta() {
        return state;
    }

    @Override
    public Set getState() {
        return state;
    }
}
