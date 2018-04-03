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

import static java.util.stream.Collectors.joining;

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
            return new BrownianMovementSimulationFrame(0, Collections.EMPTY_SET, particles);
        }
        BrownianMovementSimulationFrame frame = getNextFrame();
        if (frame.getTimestamp() > duration) {
            return null;
        }
        resolveCollision(frame);
        currentTime = frame.getTimestamp();
        particles = frame.getState();
        return frame;
    }

    private BrownianMovementSimulationFrame getNextFrame() {
        Set<WeightedDynamicParticle2D> colliders = new HashSet<>();
        double deltaTime;
        WeightedDynamicParticle2D closestToWall = null;
        double wallTime = Integer.MAX_VALUE;

        for(WeightedDynamicParticle2D p: particles) {
            double aux = timeToClosestWall(p);
            if(aux < wallTime) {
                wallTime = aux;
                closestToWall = p;
            }
        }

        double particlesTime = wallTime;
        sober = null;
        drunkard = null;
        for (WeightedDynamicParticle2D p1 : particles) {
            for (WeightedDynamicParticle2D p2 : particles) {
                if (p1.equals(p2)) continue;

                double aux = p1.timeToCollision(p2);
                if (aux < particlesTime) {
                    particlesTime = aux;
                    sober = p1;
                    drunkard = p2;
                }
            }
        }
        if (wallTime <= particlesTime) {
            deltaTime = wallTime;
            closestToWall = moveParticle(closestToWall, deltaTime);
            colliders.add(closestToWall);
        } else {
            deltaTime = particlesTime;
            drunkard = moveParticle(drunkard, deltaTime);
            sober = moveParticle(sober, deltaTime);
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

        if (particle.getXSpeed() > 0) {
            xTime = particle.timeToX(wallLength);
        } else if (particle.getXSpeed() < 0) {
            xTime = particle.timeToX(0 + 2 * particle.getRadius());
        } else {
            xTime = Integer.MAX_VALUE;
        }

        if (particle.getYSpeed() > 0) {
            yTime = particle.timeToY(wallLength);
        } else if (particle.getYSpeed() < 0) {
            yTime = particle.timeToY(0 + 2 * particle.getRadius());
        } else {
            yTime = Integer.MAX_VALUE;
        }

        return Double.min(xTime, yTime);
    }

    private static WeightedDynamicParticle2D moveParticle(WeightedDynamicParticle2D p, double deltaT) {
        double xpos = p.getXCoordinate() + p.getXSpeed() * deltaT;
        double ypos = p.getYCoordinate() + p.getYSpeed() * deltaT;

        return new WeightedDynamicParticle2D(p.getId(), xpos,
                ypos, p.getXSpeed(), p.getYSpeed(), p.getRadius(), p.getWeight());
    }

    private void resolveCollision(BrownianMovementSimulationFrame frame) {
        if (frame.getDelta().size() == 1) {
            frame.getDelta().forEach(x -> {
                frame.getDelta().remove(x);
                frame.getState().remove(x);
                WeightedDynamicParticle2D b = updateWallCollisionSpeed(x);
                frame.getDelta().add(b);
                frame.getState().add(b);
            });
        } else {
            frame.getDelta().clear();
            frame.getState().remove(sober);
            frame.getState().remove(drunkard);

            double deltaVX = drunkard.getXSpeed() - sober.getXSpeed();
            double deltaVY = drunkard.getYSpeed() - sober.getYSpeed();
            double deltaX = drunkard.getXCoordinate() - sober.getXCoordinate();
            double deltaY = drunkard.getYCoordinate() - sober.getYCoordinate();
            double auxVR = (deltaX * deltaVX) + (deltaY * deltaVY);
            double j = 2 * drunkard.getWeight() * sober.getWeight() * auxVR;
            j /= (drunkard.getRadius() + sober.getRadius()) * (sober.getWeight() + drunkard.getWeight()) ;
            double jX = j * (drunkard.getXCoordinate() - sober.getXCoordinate()) / (drunkard.getRadius() + sober.getRadius());
            double jY = j * (drunkard.getYCoordinate() - sober.getYCoordinate()) / (drunkard.getRadius() + sober.getRadius());
            double newDrunkardVX = (drunkard.getXSpeed() - (jX / drunkard.getWeight()));
            double newSoberVX = (sober.getXSpeed() + (jX / sober.getWeight()));
            double newDrunkardVY = (drunkard.getYSpeed() - (jY / drunkard.getWeight()));
            double newSoberVY = (sober.getYSpeed() + (jY / sober.getWeight()));

            drunkard = new WeightedDynamicParticle2D(drunkard.getId(), drunkard.getXCoordinate(),
                    drunkard.getYCoordinate(), newDrunkardVX, newDrunkardVY, drunkard.getRadius(), drunkard.getWeight());

            sober = new WeightedDynamicParticle2D(sober.getId(), sober.getXCoordinate(),
                    sober.getYCoordinate(), newSoberVX, newSoberVY, sober.getRadius(), sober.getWeight());
            frame.getDelta().add(sober);
            frame.getDelta().add(drunkard);
            frame.getState().add(sober);
            frame.getState().add(drunkard);
        }
    }

    private WeightedDynamicParticle2D updateWallCollisionSpeed(WeightedDynamicParticle2D p) {
        double xTime;
        double yTime;
        double vx = p.getXSpeed();
        double vy = p.getYSpeed();
        if (p.getXSpeed() < 0) {
            xTime = p.timeToX(0 + 2 * p.getRadius());
        } else if (p.getXSpeed() > 0) {
            xTime = p.timeToX(wallLength);
        } else {
            xTime = Integer.MAX_VALUE;
        }

        if (p.getYSpeed() < 0) {
            yTime = p.timeToY(0 + 2 * p.getRadius());
        } else if (p.getYSpeed() > 0) {
            yTime = p.timeToY(wallLength);
        } else {
            yTime = Integer.MAX_VALUE;
        }

        if (xTime < yTime) {
            vx = -vx;
        } else if (xTime > yTime) {
            vy = -vy;
        } else {
            vx = -vx;
            vy = -vy;
        }
        return new WeightedDynamicParticle2D(p.getId(), p.getXCoordinate(),
         p.getYCoordinate(), vx, vy, p.getRadius(), p.getWeight());
    }
}

