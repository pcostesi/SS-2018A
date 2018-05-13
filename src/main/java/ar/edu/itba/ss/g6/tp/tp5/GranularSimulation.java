package ar.edu.itba.ss.g6.tp.tp5;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Set;

public class GranularSimulation implements TimeDrivenSimulation<TheParticle, GranularSimulationFrame> {
    private final static double G = 9.8;
    private final double W;
    private final double L;
    private final double D;
    private final double deltaT;
    private final Set<TheParticle> particles;

    private double timestamp;

    private TheParticle beeman(TheParticle particle) {
        double sTs = deltaT;
        double nRx, nRy;
        double nVx, nVy, pVx, pVy;
        double ax = getXAcceleration(particle);
        double ay = getYAcceleration(particle);
        double pax = particle.getPreviousAccelerationX();
        double pay = particle.getPreviousAccelerationY();

        // Calculate new position and predicted speed
        nRx = particle.getXCoordinate() + particle.getXSpeed() * sTs
         + ( (2.0 / 3.0) * ax  - (1.0 / 6.0) * pax ) * Math.pow(sTs, 2.0);
        pVx = particle.getXSpeed() + (3.0 / 2.0) * ax * sTs
         - (1.0 / 2.0) * pax * sTs;

        nRy = particle.getYCoordinate() + particle.getYSpeed() * sTs
         + ( (2.0 / 3.0) * ay  - ( 1.0 / 6.0) * pay ) * Math.pow(sTs, 2.0);
        pVy = particle.getYSpeed() + (3.0 / 2.0) * ay * sTs
         - (1.0 / 2.0) * pay * sTs;

        // Update particle with new position and predicted speed
        TheParticle predictedParticle = new TheParticle(particle.getId(), nRx, nRy, pVx, pVy, particle.getRadius(), particle.getWeight());
        // Calculate t+DT acceleration
        double fAx = getXAcceleration(predictedParticle);
        double fAy = getYAcceleration(predictedParticle);
        nVx = particle.getXSpeed() + (1.0/3.0) * fAx * sTs + (5.0/6.0) * ax * sTs - (1.0/6.0) * pax * sTs;
        nVy = particle.getYSpeed() + (1.0/3.0) * fAy * sTs + (5.0/6.0) * ay * sTs - (1.0/6.0) * pay * sTs;

        // Update particle with approximated speed
        TheParticle result = null;// = new TheParticle();
        // = new WeightedDynamicParticle2D( armonicParticle.getId(),
        // armonicParticle.getXCoordinate(), armonicParticle.getYCoordinate(), nVx, nVy,
        // armonicParticle.getRadius(), armonicParticle.getWeight());
        // prevXAcceleration = ax;
        // prevYAcceleration = ay;

        return result;

    }

    private double getXAcceleration(TheParticle particle) {
        return 0;
    }

    private double getYAcceleration(TheParticle particle) {
        double forceDueToGravity = G * particle.getWeight();
        return forceDueToGravity;
    }

    public GranularSimulation(double deltaT, double width, double height, double aperture, Set<TheParticle> particles) {
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
