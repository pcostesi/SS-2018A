package ar.edu.itba.ss.g6.tp.tp5;

import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.force.Force;
import ar.edu.itba.ss.g6.topology.force.GranularForce;
import ar.edu.itba.ss.g6.topology.geometry.Vessel;
import ar.edu.itba.ss.g6.topology.geometry.Wall;
import ar.edu.itba.ss.g6.topology.grid.Grid;
import ar.edu.itba.ss.g6.topology.grid.TheParticleGrid;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GranularSimulation implements TimeDrivenSimulation<TheParticle, GranularSimulationFrame> {
    private final static double G = -9.8;
    private final double W;
    private final double L;
    private final double D;
    private final double deltaT;
    private double fps;

    private Set<TheParticle> particles;
    private Grid<TheParticle> grid;
    private Set<TheParticle> waitingPlacement;
    private final Force force;
    private final Vessel vessel;

    private int count = 0;

    private double timestamp;
    private AtomicInteger flowed = new AtomicInteger();


    private TheParticle warp(@NotNull TheParticle particle) {
        if (particle.getPosition().getY() <= (L * -0.1)) {
            double x;
            double y;
            boolean safeToPlace = false;
            V2d position = new V2d(0, 0);
            V2d resting = new V2d(0, 0);
            int attempts = 0;
            while(!safeToPlace && attempts < 10) {
                attempts++;
                x = Math.random() * W * 0.8 + W * 0.1;
                y = L - Math.random() * 0.25 * L - particle.getRadius() * 2;
                position = new V2d(x, y);
                resting = new V2d(0, 0);
                safeToPlace = particles.parallelStream()
                        .noneMatch(p -> p.overlapsWith(particle) && !p.equals(particle));
            }

            if(attempts >= 10) {
                waitingPlacement.add(particle);
                particles.remove(particle);
                return null;
            }
            particles.add(new TheParticle(particle.getId(), position, resting, resting, resting,
                    particle.getRadius(), particle.getMass()));
            return null;
        }
        else if(count % 30 == 0) {
            V2d position = new V2d(0, -L);
            V2d resting = new V2d(0, 0);
            if (particle.getPosition().getY() > L) {
                return warp(new TheParticle(particle.getId(), position, resting,
                        resting, resting, particle.getRadius(), particle.getMass(),0));
            }
            if (particle.getPosition().getX() - particle.getRadius() < 0) {
                return warp(new TheParticle(particle.getId(), position, resting,
                        resting, resting, particle.getRadius(), particle.getMass(),0));
            }
            if (particle.getPosition().getX() + particle.getRadius() > W) {
                return warp(new TheParticle(particle.getId(), position, resting,
                        resting, resting, particle.getRadius(), particle.getMass(),0));
            }
        }
        particles.add(particle);
        return null;
    }

    public GranularSimulation(double Mu, double Gamma, double deltaT, double width, double height, double aperture, Set<TheParticle> particles, double fps) {
        double radius = particles.stream().mapToDouble(TheParticle::getRadius).max().orElse(0) * 2;
        double side = Math.max(width, height);

        this.deltaT = deltaT;
        this.W = width;
        this.L = height;
        this.D = aperture;
        this.particles = particles;
        this.vessel = new Vessel(height, width, aperture);
        this.grid = new TheParticleGrid((int) (side / radius) , side, radius);
        this.waitingPlacement = new HashSet<>();

        this.fps = fps;

        grid.set(this.particles);
        force = new GranularForce(Mu, Gamma);
        if (W > L || D > W) {
            throw new IllegalArgumentException("L > W > D");
        }
    }

    private V2d getForce(@NotNull TheParticle particle) {
        Collection<TheParticle> neighbors = grid.getNeighbors(particle);
        double p2pForceX = 0;
        double p2pForceY = 0;

        double p2wForceX = 0;
        double p2wForceY = 0;

        for (TheParticle neighbor : neighbors) {
            V2d interaction = force.getForce(particle, neighbor);
            p2pForceX += interaction.getX();
            p2pForceY += interaction.getY();
        }

        for (Wall neighbor : vessel.getWalls()) {
            V2d interaction = force.getForce(particle, neighbor);
            p2wForceX += interaction.getX();
            p2wForceY += interaction.getY();
        }

        return new V2d(p2pForceX + p2wForceX, p2pForceY + p2wForceY);
    }

    private double getXAcceleration(@NotNull TheParticle particle) {
        return getForce(particle).getX() / particle.getMass();
    }

    private double getYAcceleration(@NotNull TheParticle particle) {
        return G + getForce(particle).getY() / particle.getMass();
    }

    private TheParticle move(@NotNull TheParticle particle) {
        double sTs = deltaT;
        double nRx, nRy;
        double nVx, nVy, pVx, pVy;
        double ax = getXAcceleration(particle);
        double ay = getYAcceleration(particle);
        double pax = particle.getPrevAcceleration().getX();
        double pay = particle.getPrevAcceleration().getY();

        // Calculate new position and predicted speed
        nRx = particle.getPosition().getX() + particle.getVelocity().getX() * sTs
         + ( (2.0 / 3.0) * ax  - (1.0 / 6.0) * pax ) * Math.pow(sTs, 2.0);
        pVx = particle.getVelocity().getX() + (3.0 / 2.0) * ax * sTs
         - (1.0 / 2.0) * pax * sTs;

        nRy = particle.getPosition().getY() + particle.getVelocity().getY() * sTs
         + ( (2.0 / 3.0) * ay  - ( 1.0 / 6.0) * pay ) * Math.pow(sTs, 2.0);
        pVy = particle.getVelocity().getY() + (3.0 / 2.0) * ay * sTs
         - (1.0 / 2.0) * pay * sTs;

        // Update particle with new position and predicted speed
        TheParticle predictedParticle = new TheParticle(particle.getId(), nRx, nRy, pVx, pVy, particle.getRadius(), particle.getMass());
        // Calculate t+DT acceleration
        double fAx = getXAcceleration(predictedParticle);
        double fAy = getYAcceleration(predictedParticle);
        nVx = particle.getVelocity().getX() + (1.0/3.0) * fAx * sTs + (5.0/6.0) * ax * sTs - (1.0/6.0) * pax * sTs;
        nVy = particle.getVelocity().getY() + (1.0/3.0) * fAy * sTs + (5.0/6.0) * ay * sTs - (1.0/6.0) * pay * sTs;

        // Update particle with approximated speed
        TheParticle result = new TheParticle(particle.getId(), nRx, nRy, nVx, nVy, fAx, fAy, ax, ay,
            particle.getRadius(), particle.getMass(), particle.getNormalForce());

        // did it flow?
        if (particle.getPosition().getY() > 0 && nRy <= 0) {
            flowed.incrementAndGet();
        }
        return result;
    }

    @Override
    public GranularSimulationFrame getNextStep() {
        if (timestamp == 0) {
            timestamp += deltaT;
            return new GranularSimulationFrame(0, particles, 0);
        }
        if (count % 100 == 0) {
            Set<TheParticle> aux = waitingPlacement;
            waitingPlacement = new HashSet<>();
            aux.parallelStream().forEach(this::warp);
        }
        timestamp += deltaT;
        flowed.set(0);

        Set<TheParticle> state = particles.parallelStream()
            .map(this::move).collect(Collectors.toSet());
        particles.clear();
        state.stream().forEach(this::warp);
        grid.set(particles);
        count++;
        return new GranularSimulationFrame(timestamp, particles, flowed.get());
    }

    @Override
    public double getDeltaT() {
        return deltaT;
    }

    @Override
    public double getFPS() {
        return fps;
    }

    public double getMaxHeight() {
        double heigth = 0;
        Optional<Double> max = particles.stream().map(TheParticle::getPosition).map(V2d::getY).max(Double::compareTo);
        if (max.isPresent()){
            return max.get();
        }
        return 0;
    }

    @Override
    public void resetNormalForce() {
        particles.parallelStream().forEach(TheParticle::resetNormalForce);
    }
}
