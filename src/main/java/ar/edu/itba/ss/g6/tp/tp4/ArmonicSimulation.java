package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

public class ArmonicSimulation implements TimeDrivenSimulation {

    private WeightedDynamicParticle2D armonicParticle;
    private final double simulationTimeLimit = 5;
    private double simulationTimeStep;
    private double simulationTime = 0;
    private IntegrationMethod method;
    private double prevXAcceleration = 0;
    private double prevYAcceleration = 0;

    // Constants
    final double k = 10e4;
    final double Y = 100; // Gama


    public ArmonicSimulation(WeightedDynamicParticle2D particle, double step, IntegrationMethod method) {
        this.armonicParticle = particle;
        this.simulationTimeStep = step;
        this.method = method;
        double prevRX = armonicParticle.getXCoordinate() - armonicParticle.getXSpeed() * simulationTimeStep;
        double prevRY = armonicParticle.getYCoordinate() - armonicParticle.getYSpeed() * simulationTimeStep;
        double prevVX = armonicParticle.getXSpeed() - getXAcceleration() * simulationTimeStep;
        double prevVY = armonicParticle.getYSpeed() - getYAcceleration() * simulationTimeStep;
        WeightedDynamicParticle2D temp = armonicParticle;
        armonicParticle = new WeightedDynamicParticle2D( armonicParticle.getId(),
                prevRX, prevRY, prevVX, prevVY, armonicParticle.getRadius(), armonicParticle.getWeight());
        prevXAcceleration = getXAcceleration();
        prevYAcceleration = getYAcceleration();
        armonicParticle = temp;
    }

    @Override
    public SimulationFrame getNextStep() {
        ArmonicSimulationFrame currentFrame =  new ArmonicSimulationFrame(simulationTime, armonicParticle);
        if( simulationTimeLimit < simulationTime) return null;
        updateArmonicParticle();
        simulationTime = simulationTime + simulationTimeStep;
        return currentFrame;
    }

    private void updateArmonicParticle() {
        switch (method) {
            case BEEMAN: updateByBeeman(); break;
            case GPCO5: updateByGPCO5(); break;
            case VERLET: updateByVerlet(); break;
            default: throw new IllegalArgumentException("cHV0byBlbCBxdWUgbGVl");
        }
    }

    private void updateByBeeman() {
        double sTs = simulationTimeStep;
        WeightedDynamicParticle2D ap = armonicParticle;
        double nRx, nRy;
        double nVx, nVy, pVx, pVy;
        double ax = getXAcceleration();
        double ay = getYAcceleration();
        // Calculate new position and predicted speed
        nRx = ap.getXCoordinate() + ap.getXSpeed() * sTs
                + ( (2.0 / 3.0) * ax  - (1.0 / 6.0) * prevXAcceleration ) * Math.pow(sTs, 2.0);
        pVx = ap.getXSpeed() + (3.0 / 2.0) * ax * sTs
                - (1.0 / 2.0) * prevXAcceleration * sTs;
        nRy = ap.getYCoordinate() + ap.getYSpeed() * sTs
                + ( (2.0 / 3.0) * ay  - ( 1.0 / 6.0) * prevYAcceleration ) * Math.pow(sTs, 2.0);
        pVy = ap.getYSpeed() + (3.0 / 2.0) * ay * sTs
                - (1.0 / 2.0) * prevYAcceleration * sTs;
        // Update armonicParticle with new position and predicted speed
        armonicParticle = new WeightedDynamicParticle2D( ap.getId(), nRx, nRy, pVx, pVy, ap.getRadius(), ap.getWeight());
        // Calculate t+DT acceleration
        double fAx = getXAcceleration();
        double fAy = getYAcceleration();
        nVx = ap.getXSpeed() + (1.0/3.0) * fAx * sTs + (5.0/6.0) * ax * sTs - (1.0/6.0) * prevXAcceleration * sTs;
        nVy = ap.getYSpeed() + (1.0/3.0) * fAy * sTs + (5.0/6.0) * ay * sTs - (1.0/6.0) * prevYAcceleration * sTs;
        // Update particle with aproximated speed
        armonicParticle = new WeightedDynamicParticle2D( armonicParticle.getId(),
                armonicParticle.getXCoordinate(), armonicParticle.getYCoordinate(), nVx, nVy,
                armonicParticle.getRadius(), armonicParticle.getWeight());
        prevXAcceleration = ax;
        prevYAcceleration = ay;
    }

    private void updateByGPCO5() {}

    private void updateByVerlet() {}

    public enum IntegrationMethod {
        BEEMAN,
        GPCO5,
        VERLET
    }

    private double getXForce() {
        return (-1) * k * armonicParticle.getXCoordinate() - Y * armonicParticle.getXSpeed();
    }

    private double getYForce() {
        return (-1) * k * armonicParticle.getYCoordinate() - Y * armonicParticle.getYSpeed();
    }

    private double getXAcceleration() {
        return getXForce() / armonicParticle.getWeight();
    }

    private double getYAcceleration() { return getYForce() / armonicParticle.getWeight(); }
}
