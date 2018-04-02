package ar.edu.itba.ss.g6.tp.tp3;

import ar.edu.itba.ss.g6.simulation.EventDrivenSimulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.DynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.HashSet;
import java.util.Optional;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class BrownianMovement implements EventDrivenSimulation<WeightedDynamicParticle2D, SimulationFrame<WeightedDynamicParticle2D>> {

    private final double duration;
    private Set<WeightedDynamicParticle2D> particles;
    double wallLength = 0.5;
    private double currentTime;
    private boolean isInitialStep = true;
    private WeightedDynamicParticle2D drunkard;
    private WeightedDynamicParticle2D sober;

    //TODO UPDATE SPEED

    public BrownianMovement(double duration, Set<WeightedDynamicParticle2D> particles) {
        this.duration = duration;
        this.particles = particles;
    }

    @Override
    public TimeDrivenSimulation toTimeDrivenSimulation() {
        return new BrownianMovementTimeDrivenSimulation(new BrownianMovement(duration, Collections.unmodifiableSet(particles)));
    }

    @Override
    public SimulationFrame getNextStep() {
        if (isInitialStep) {
            isInitialStep = false;
            return new BrownianMovementSimulationFrame(0, null, particles);
        }
        BrownianMovementSimulationFrame frame = getNextFrame();
        if (frame.getTimestamp() > duration) {
            return null;
        }
        resolveCollition(frame);
        currentTime = frame.getTimestamp();
        particles = frame.getState();
        return frame;
    }

    private BrownianMovementSimulationFrame getNextFrame() {
        Set<WeightedDynamicParticle2D> colliders = new HashSet<>();
        double deltaTime;
        Optional<WeightedDynamicParticle2D> closestToWall =
                particles.stream().min((x, y) -> timeToClosestWall(x) < timeToClosestWall(y) ? 1 : -1);
        double wallTime = timeToClosestWall(closestToWall.get());
        double particlesTime = wallTime;
        sober = null;
        drunkard = null;
        for (WeightedDynamicParticle2D p : particles) {
            for (WeightedDynamicParticle2D pp : particles) {
                if (p.equals(pp)) continue;
                double aux = p.timeToCollision(pp);
                if (aux < particlesTime) {
                    particlesTime = aux;
                    drunkard = pp;
                    sober = p;
                }
            }
        }
        if (wallTime < particlesTime) {
            deltaTime = wallTime;
            colliders.add(closestToWall.get());
        } else {
            deltaTime = particlesTime;
            colliders.add(drunkard);
            colliders.add(sober);
        }
        Set<WeightedDynamicParticle2D> newState = getNewState(deltaTime);
        return new BrownianMovementSimulationFrame(deltaTime + currentTime, colliders, newState);
    }

    private Set<WeightedDynamicParticle2D> getNewState(double deltaTime) {
        return particles.parallelStream()
                .map(p -> moveParticle(p, deltaTime))
                .collect(Collectors.toSet());
    }

    private double timeToClosestWall(DynamicParticle2D particle) {
        double xTime;
        double yTime;
        if (particle.getXSpeed() == 0) {
            xTime = -1;
        } else if (particle.getXSpeed() > 0) {
            xTime = particle.timeToX(wallLength);
        } else {
            xTime = particle.timeToX(0);
        }
        if (particle.getXSpeed() == 0) {
            yTime = -1;
        } else if (particle.getYSpeed() > 0) {
            yTime = particle.timeToY(wallLength);
        } else {
            yTime = particle.timeToY(0);
        }
        if (xTime == -1 || xTime < yTime) {
            return xTime;
        } else {
            return yTime;
        }
    }

    private static WeightedDynamicParticle2D moveParticle(WeightedDynamicParticle2D p, double deltaT) {
        double xpos = p.getXCoordinate() + p.getXSpeed() * deltaT;
        double ypos = p.getYCoordinate() + p.getYSpeed() * deltaT;
        WeightedDynamicParticle2D pNext = new WeightedDynamicParticle2D(p.getId(), xpos,
                ypos, p.getXSpeed(), p.getYSpeed(), p.getRadius(), p.getWeight());
        return pNext;
    }

    private void resolveCollition(BrownianMovementSimulationFrame frame) {
        if (frame.getDelta().size() == 1) {
            frame.getDelta().forEach(x -> {
                WeightedDynamicParticle2D b = updateWallCollisionSpeed(x);
                frame.getDelta().remove(x);
                frame.getDelta().add(b);
                frame.getState().remove(x);
                frame.getState().add(b);
            });
        } else {
            frame.getDelta().clear();
            drunkard = moveParticle(drunkard, frame.getTimestamp() - currentTime);
            sober = moveParticle(sober, frame.getTimestamp() - currentTime);
            double sigma = drunkard.getRadius() + sober.getRadius();
            double DeltaRX = drunkard.getXCoordinate() - sober.getXCoordinate();
            double DeltaRY = drunkard.getYCoordinate() - sober.getYCoordinate();
            double DeltaVX = drunkard.getXSpeed() - sober.getXSpeed();
            double DeltaVY = drunkard.getYSpeed() - sober.getYSpeed();
            double DeltaVDotDeltaR = DeltaVX * DeltaRX + DeltaVY * DeltaRY;
            double J = (2 * drunkard.getWeight() * sober.getWeight() * DeltaVDotDeltaR) /
                    (sigma * (drunkard.getWeight() + sober.getWeight()));
            double JX = J * DeltaRX / sigma;
            double JY = J * DeltaRY / sigma;
            double newDrunkardVX = drunkard.getXSpeed() + JX * drunkard.getWeight();
            double newDrunkardVY = drunkard.getXSpeed() + JY * drunkard.getWeight();
            double newSoberVX = sober.getXSpeed() + JX * sober.getWeight();
            double newSoberVY = sober.getYSpeed() + JY * sober.getWeight();

            drunkard = new WeightedDynamicParticle2D(drunkard.getId(), drunkard.getXCoordinate(),
                    drunkard.getYCoordinate(), newDrunkardVX, newDrunkardVY, drunkard.getRadius(), drunkard.getWeight());
            sober = new WeightedDynamicParticle2D(sober.getId(), sober.getXCoordinate(),
                    sober.getYCoordinate(), newSoberVX, newSoberVY, sober.getRadius(), sober.getWeight());
            frame.getDelta().add(sober);
            frame.getDelta().add(drunkard);
            frame.getState().remove(sober);
            frame.getState().remove(drunkard);
            frame.getState().add(sober);
            frame.getState().add(drunkard);
        }
    }

    private WeightedDynamicParticle2D updateWallCollisionSpeed(WeightedDynamicParticle2D p) {
        double xTime;
        double yTime;
        if (p.getXSpeed() == 0) {
            xTime = -1;
        } else if (p.getXSpeed() > 0) {
            xTime = p.timeToX(wallLength);
        } else {
            xTime = p.timeToX(0);
        }
        if (p.getXSpeed() == 0) {
            yTime = -1;
        } else if (p.getYSpeed() > 0) {
            yTime = p.timeToY(wallLength);
        } else {
            yTime = p.timeToY(0);
        }
        if (xTime == -1 || xTime < yTime) {
            return new WeightedDynamicParticle2D(p.getId(), p.getXCoordinate(),
                    p.getYCoordinate(), (-1) * p.getXSpeed(), p.getYSpeed(), p.getRadius(), p.getWeight());
        } else {
            return new WeightedDynamicParticle2D(p.getId(), p.getXCoordinate(),
                    p.getYCoordinate(), p.getXSpeed(), (-1) * p.getYSpeed(), p.getRadius(), p.getWeight());
        }
    }
}

