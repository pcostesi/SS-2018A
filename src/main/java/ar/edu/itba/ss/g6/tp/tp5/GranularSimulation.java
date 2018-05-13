package ar.edu.itba.ss.g6.tp.tp5;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;

public class GranularSimulation implements TimeDrivenSimulation<WeightedDynamicParticle2D, GranularSimulationFrame> {
    private final double W;
    private final double L;
    private final double D;
    private final double deltaT;
    private final Set<WeightedDynamicParticle2D> particles;

    private double timestamp;


    public GranularSimulation(double deltaT, double width, double height, double aperture, Set<WeightedDynamicParticle2D> particles) {
        this.deltaT = deltaT;
        this.W = width;
        this.L = height;
        this.D = aperture;
        this.particles = particles;

        if (W > L || D > W) {
            throw new IllegalArgumentException("L > W > D");
        }
    }

    @Override
    public GranularSimulationFrame getNextStep() {
        return new GranularSimulationFrame(++timestamp, null);
    }
}
