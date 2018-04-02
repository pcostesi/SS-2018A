package ar.edu.itba.ss.g6.tp.TP3;

import ar.edu.itba.ss.g6.simulation.EventDrivenSimulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.DynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BrownianMovement implements EventDrivenSimulation<WeightedDynamicParticle2D, SimulationFrame<WeightedDynamicParticle2D>> {

    private final double duration;
    private final Set<WeightedDynamicParticle2D> particles;
    double wallLength = 0.5;
    private double currentTime;
    private boolean isInitialStep = true;
    private SimulationFrame nextFrame;

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
        SimulationFrame frame; //TODO new frame
        if(isInitialStep) {
            return null; //TODO RETURN INITIAL
        }
        // calcular tiempo evento
        // ver que sea menor a end
        // crear frame
        // actualizar parts
        return null;
    }

    private void setNextFrame() {
        Set<WeightedDynamicParticle2D> colliders = new HashSet<>();
         //TODO CHECK
        Optional<WeightedDynamicParticle2D> closestToWall =
                particles.stream().min( (x, y) -> timeToClosestWall(x) < timeToClosestWall(y) ? 1 : -1 );
        double wallTime = timeToClosestWall(closestToWall.get());
        double particlesTime = wallTime;
        WeightedDynamicParticle2D sober = null, drunkard = null;
        for(WeightedDynamicParticle2D p: particles) {
            for( WeightedDynamicParticle2D pp: particles) {
                double aux = p.timeToCollision(pp);
                if( aux < particlesTime ) {
                    particlesTime = aux;
                    drunkard = pp;
                    sober = p;
                }
            }
        }
        if(drunkard == null) {
            nextFrame = new SimulationFrame(currentTime + wallTime);
            colliders.add(closestToWall.get());
        } else {
            nextFrame = new SimulationFrame(currentTime + wallTime);
            colliders.add(drunkard);
            colliders.add(sober);
        }
    }

    private double timeToClosestWall(DynamicParticle2D particle) {
        double xTime;
        double yTime;
        if(particle.getXSpeed() == 0) {
            xTime = -1;
        }
        else if(particle.getXSpeed() > 0) {
            xTime = particle.timeToX(wallLength);
        } else {
            xTime = particle.timeToX(0);
        }
        if(particle.getXSpeed() == 0) {
            yTime = -1;
        }
        else if(particle.getYSpeed() > 0) {
            yTime = particle.timeToY(wallLength);
        } else {
            yTime = particle.timeToY(0);
        }
        if(xTime == -1 || xTime < yTime) {
            return xTime;
        }
        else {
            return yTime;
        }
    }
}
