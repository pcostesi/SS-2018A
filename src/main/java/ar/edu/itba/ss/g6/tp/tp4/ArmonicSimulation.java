package ar.edu.itba.ss.g6.tp.tp4;

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
    private double prevRX = 0;
    private double prevRY = 0;

    private double[] particleXCoefficients;
    private double[] particleYCoefficients;
    private boolean particleGPCO5initialized = false;

    private final double[] gearPredictorTable = new double[]
     { 3./20,    251./260,  1.,    11./18,  1./6,   1./60   };

    // Constants
    private final double k = 10e4;
    private final double Y = 100; // Gama
    private final double m = 70;


    public ArmonicSimulation(double step, IntegrationMethod method) {
        double initialSpeed =(-1)*Y/(2*m);
        this.armonicParticle = new WeightedDynamicParticle2D("1", 1, 0, initialSpeed, 0, 0,m);
        this.simulationTimeStep = step;
        this.method = method;
        prevRX = armonicParticle.getXCoordinate() - armonicParticle.getXSpeed() * simulationTimeStep;
        prevRY = armonicParticle.getYCoordinate() - armonicParticle.getYSpeed() * simulationTimeStep;
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
    public ArmonicSimulationFrame getNextStep() {
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
            case ANALYTIC: updateByAnalytic(); break;
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

    private double[] predictAxisWithGPCO5 (double deltaT, double[] r) {
        double t1 = deltaT;
        double t2 = Math.pow(deltaT, 2) / 2;
        double t3 = Math.pow(deltaT, 3) / 6;
        double t4 = Math.pow(deltaT, 4) / 24;
        double t5 = Math.pow(deltaT, 5) / 120;

        return new double[] {
         r[0] + r[1] * t1 + r[2] * t2 + r[3] * t3 + r[4] * t4 + r[5] * t5,  // position
         r[1] + r[2] * t1 + r[3] * t2 + r[4] * t3 + r[5] * t4,            // speed
         r[2] + r[3] * t1 + r[4] * t2 + r[5] * t4,                      // acceleration
         r[3] + r[4] * t1 + r[5] * t2,                                // r3
         r[4] + r[5] * t1,                                          // r4
         r[5],                                                    // r5
        };
    }

    private double[] correctAxisWithGPCO5 (double deltaT, double deltaR2, double[] predicted) {
        double[] rc = {
         predicted[0] + gearPredictorTable[0] * deltaR2,
         predicted[1] + gearPredictorTable[1] * deltaR2 * 1 / Math.pow(deltaT, 1),
         predicted[2] + gearPredictorTable[2] * deltaR2 * 2 / Math.pow(deltaT, 2),
         predicted[3] + gearPredictorTable[3] * deltaR2 * 6 / Math.pow(deltaT, 3),
         predicted[4] + gearPredictorTable[4] * deltaR2 * 24 / Math.pow(deltaT, 4),
         predicted[5] + gearPredictorTable[5] * deltaR2 * 120 / Math.pow(deltaT, 5),
        };
        return rc;
    }

    private double[] initializeGPCO5Axis(double mass, double r0, double r1) {
        double r2 = (-k / mass) * r0 - (Y / mass) * r1;
        double r3 = (-k / mass) * r1 - (Y / mass) * r2;
        double r4 = (-k / mass) * r2 - (Y / mass) * r3;
        double r5 = (-k / mass) * r3 - (Y / mass) * r4;
        return new double[] { r0, r1, r2, r3, r4, r5 };
    }

    private void updateByGPCO5() {
        WeightedDynamicParticle2D ap = armonicParticle;
        if (!particleGPCO5initialized) {
            particleGPCO5initialized = true;
            particleXCoefficients = initializeGPCO5Axis(ap.getWeight(), ap.getXCoordinate(), ap.getXSpeed());
            particleYCoefficients = initializeGPCO5Axis(ap.getWeight(), ap.getYCoordinate(), ap.getYSpeed());
        }

        // predict
        double[] xAxis = predictAxisWithGPCO5(simulationTimeStep, particleXCoefficients);
        double[] yAxis = predictAxisWithGPCO5(simulationTimeStep, particleYCoefficients);

        // eval
        double ax = (-1 * k * xAxis[0] - Y * xAxis[1]) / ap.getWeight();
        double ay = (-1 * k * yAxis[0] - Y * yAxis[1]) / ap.getWeight();
        double deltaR2x = (ax - xAxis[2]) * Math.pow(simulationTimeStep, 2) / 2;
        double deltaR2y = (ay - yAxis[2]) * Math.pow(simulationTimeStep, 2) / 2;

        // correct
        double[] rcx = correctAxisWithGPCO5(simulationTimeStep, deltaR2x, xAxis);
        double[] rcy = correctAxisWithGPCO5(simulationTimeStep, deltaR2y, yAxis);

        particleXCoefficients = rcx;
        particleYCoefficients = rcy;
        armonicParticle = new WeightedDynamicParticle2D("0", rcx[0], rcy[0], rcx[1], rcy[1], ap.getRadius(), ap.getWeight());
    }

    private void updateByVerlet() {
        double sTs = simulationTimeStep;
        WeightedDynamicParticle2D ap = armonicParticle;
        double newSpeedX, newPosX, newSpeedY, newPosY;
        newPosX = 2 * ap.getXCoordinate() - prevRX + (Math.pow(sTs, 2) / ap.getWeight()) * getXForce();
        newPosY = 2 * ap.getYCoordinate() - prevRY + (Math.pow(sTs, 2) / ap.getWeight()) * getYForce();
        newSpeedX = (newPosX - ap.getXCoordinate()) / (2 * sTs);
        newSpeedY = (newPosY - ap.getYCoordinate()) / (2 * sTs);
        prevRX = ap.getXCoordinate();
        prevRY = ap.getYCoordinate();
        armonicParticle = new WeightedDynamicParticle2D( armonicParticle.getId(),
                newPosX, newPosY, newSpeedX, newSpeedY,
                armonicParticle.getRadius(), armonicParticle.getWeight());
        return;
    }

    private void updateByAnalytic() {
        double time = simulationTime + simulationTimeStep;
        double A = 1;
        double initialSpeed =(-1)*Y/(2*m);
        double expTerm = Math.exp(initialSpeed * time);
        double cosineTerm = Math.cos(Math.pow( (k/m) -  (Y*Y) / (4*m*m), 0.5) * time);
        double x = A * expTerm * cosineTerm;
        armonicParticle = new WeightedDynamicParticle2D( armonicParticle.getId(),
                x, armonicParticle.getYCoordinate(), 0, 0,
                armonicParticle.getRadius(), armonicParticle.getWeight());
    }

    public enum IntegrationMethod {
        BEEMAN,
        GPCO5,
        VERLET,
        ANALYTIC
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
