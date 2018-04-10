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
    double k = 10e4;
    double Y = 100; // Gama


    public ArmonicSimulation(WeightedDynamicParticle2D particle, double step, IntegrationMethod method) {
        this.armonicParticle = particle;
        this.simulationTimeStep = step;
        this.method = method;
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
        double nVx, nVy;
        double ax = getXAcceleration();
        double ay = getYAcceleration();
        // Do for X
        nRx = ap.getXCoordinate() + ap.getXSpeed() * sTs +
                ( ((double)2 / 3) * ax  - ((double) 1 / 6) * prevXAcceleration ) * Math.pow(sTs, 2);

        // Do for Y
        nRy = ap.getYCoordinate() + ap.getYSpeed() * sTs +
                ( ((double)2 / 3) * ay  - ((double) 1 / 6) * prevYAcceleration ) * Math.pow(sTs, 2);
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
