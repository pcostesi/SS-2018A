package ar.edu.itba.ss.g6.tp.tp2;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.grid.MapGrid2D;
import ar.edu.itba.ss.g6.topology.particle.DynamicParticle2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class OffLatticeSimulation implements TimeDrivenSimulation {
    MapGrid2D map;
    private List<DynamicParticle2D> particles;
    private int simulationDuration;
    private double simulationTimeStep;
    private double simulationCurrentTime;
    private double Nu;

    public OffLatticeSimulation(int duration, double timeStep, List<DynamicParticle2D> particles, double Nu,
                                boolean isPeriodic, double radius, int buckets, long side) {
        this.particles = particles;
        this.simulationTimeStep = timeStep;
        this.simulationDuration = duration;
        this.simulationCurrentTime = 0;
        this.Nu = Nu;
        map = new MapGrid2D(side, buckets, radius, isPeriodic);
    }

    @Override
    public SimulationFrame getNextStep() {
        if(simulationCurrentTime + simulationTimeStep > simulationDuration) {
            return null;
        }
        updateSimulation();
        return new OffLatticeSimulationFrame(particles, simulationCurrentTime);
    }

    private void updateSimulation() {
        map.set(particles);
        List<DynamicParticle2D> newParticles = new ArrayList<>(particles.size());
        particles.forEach( x -> newParticles.add(getUpdatedParticle(x)));
        simulationCurrentTime += simulationTimeStep;
        particles = newParticles;
    }

    private DynamicParticle2D getUpdatedParticle(DynamicParticle2D particle) {
        double deltaTheta = Math.random() * Nu - Nu/2;
        double nXc, nYc, nXs, nYs;
        nXc = particle.getXCoordinate() + particle.getXSpeed() * simulationTimeStep;
        nYc = particle.getYCoordinate() + particle.getYSpeed() * simulationTimeStep;
        double newTheta = getNeighborsTheta(particle) + deltaTheta;
        // TODO Check angles usage
        nXs = Math.cos(newTheta) * particle.getXSpeed();
        nYs = Math.sin(newTheta) * particle.getYSpeed();
        return new DynamicParticle2D(particle.getId(), nXc, nYc,
                nXs, nYs, particle.getRadius());
    }

    private double getNeighborsTheta(DynamicParticle2D particle) {
        // TODO Check this
        Collection<DynamicParticle2D> neighbors =  map.getNeighbors(particle);
        return neighbors.stream().collect(Collectors.averagingDouble( x -> x.getDirection()));
    }

    @Override
    public double getDeltaT() {
        return 0;
    }

    @Override
    public double getFPS() {
        return 0;
    }

    @Override
    public double getMaxHeight() {
        return 0;
    }
}
