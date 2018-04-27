package ar.edu.itba.ss.g6.tp.tp2;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.DynamicParticle2D;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OffLatticeSimulationFrame implements SimulationFrame {

    double timestamp;
    Set<DynamicParticle2D> state;

    public OffLatticeSimulationFrame(List<DynamicParticle2D> particles, double timestamp) {
        this.timestamp = timestamp;
        this.state = new HashSet<>();
        state.addAll(particles);
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
