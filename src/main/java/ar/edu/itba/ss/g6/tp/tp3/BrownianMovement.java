package ar.edu.itba.ss.g6.tp.tp3;

import ar.edu.itba.ss.g6.simulation.EventDrivenSimulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;

public class BrownianMovement implements EventDrivenSimulation<WeightedDynamicParticle2D, SimulationFrame<WeightedDynamicParticle2D>> {

    private final double duration;
    private final Set<WeightedDynamicParticle2D> particles;
    double wallLength = 0.5;
    private double currentTime;

    public BrownianMovement(double duration, Set<WeightedDynamicParticle2D> particles){
        this.duration = duration;
        this.particles = particles;
    }

    @Override
    public TimeDrivenSimulation toTimeDrivenSimulation() {
        return new BrownianMovementTimeDrivenSimulation(this);
    }

    @Override
    public SimulationFrame getNextStep() {
        //calcular tiempo evento
        //ver que sea menor a end
        //crear frame
        //actualizar parts
        return null;
    }

    private double getNextEventTime() {
        return 0;
    }
}
