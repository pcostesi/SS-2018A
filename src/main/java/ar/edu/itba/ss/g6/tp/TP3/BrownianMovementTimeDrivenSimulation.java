package ar.edu.itba.ss.g6.tp.TP3;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D.COLOR.RED;
import static ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D.COLOR.WHITE;

public class BrownianMovementTimeDrivenSimulation implements TimeDrivenSimulation<WeightedDynamicParticle2D, SimulationFrame<WeightedDynamicParticle2D>> {
    BrownianMovement movement;
    SimulationFrame<WeightedDynamicParticle2D> currentEventFrame;
    SimulationFrame<WeightedDynamicParticle2D> nextEventFrame;
    static int FPS = 30;
    private double currentTimeStep;

    private static class BrownianMovementSimulationTimedFrame implements SimulationFrame<WeightedDynamicParticle2D> {

        private final double timestamp;
        private final Set<WeightedDynamicParticle2D> particles;

        BrownianMovementSimulationTimedFrame(double timestamp, Set<WeightedDynamicParticle2D> updatedPosition) {
            this.timestamp = timestamp;
            this.particles = updatedPosition;
        }

        @Override
        public double getTimestamp() {
            return timestamp;
        }

        @Override
        public Set<WeightedDynamicParticle2D> getDelta() {
            return particles;
        }

        @Override
        public Set<WeightedDynamicParticle2D> getState() {
            return particles;
        }
    }

    BrownianMovementTimeDrivenSimulation(BrownianMovement movement) {
        this.movement = movement;
    }

    private void reframe() {
        if (currentEventFrame == null) {
            currentEventFrame = movement.getNextStep();
            nextEventFrame = movement.getNextStep();
        }

        if (nextEventFrame != null && currentTimeStep >= nextEventFrame.getTimestamp()) {
            // advance the sim by one step;
            currentEventFrame = nextEventFrame;
            nextEventFrame = movement.getNextStep();
        }
    }

    private static WeightedDynamicParticle2D moveParticle(ColoredWeightedDynamicParticle2D p, double deltaT) {
        double xpos = p.getXCoordinate() + p.getXSpeed() * deltaT;
        double ypos = p.getYCoordinate() + p.getYSpeed() * deltaT;
        WeightedDynamicParticle2D pNext = new ColoredWeightedDynamicParticle2D(p.getId(), xpos,
         ypos, p.getXSpeed(), p.getYSpeed(), p.getRadius(), p.getWeight(), p.getColor());
        return pNext;
    }

    @Override
    public SimulationFrame<WeightedDynamicParticle2D> getNextStep() {
        reframe();
        if (currentEventFrame == null) {
            return null;
        }

        double deltaT = currentTimeStep - currentEventFrame.getTimestamp();

        Set<WeightedDynamicParticle2D> particles = currentEventFrame.getState().parallelStream()
         .map(p -> new ColoredWeightedDynamicParticle2D(p, currentEventFrame.getDelta().contains(p) ? RED : WHITE))
         .map(p -> moveParticle(p, deltaT))
            .collect(Collectors.toSet());

        SimulationFrame<WeightedDynamicParticle2D> frame = new BrownianMovementSimulationTimedFrame(currentTimeStep,
            particles);
        currentTimeStep += (1 / FPS);
        return frame;
    }
}