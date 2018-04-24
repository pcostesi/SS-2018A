package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Arrays;

public class GPCo5ForceSimulator implements ForceSimulator {
    private final double[] gearPredictorTable = new double[]
     { 3./20,    251./360,  1.,    11./18,  1./6,   1./60   };
    private double deltaT;
    private final double G = 6.67191E-20;

    public GPCo5ForceSimulator(double deltaT) {
        this.deltaT = deltaT;
    }

    @Override
    public TrajectoryData moveParticle(TrajectoryData target, TrajectoryData... system) {
        return updateByGPCO5(deltaT, target, system);
    }

    @Override
    public TrajectoryData[] initParticles(WeightedDynamicParticle2D... system) {
        // initialize structures
        TrajectoryData[] data = new TrajectoryData[system.length];
        for (int particleIdx = 0; particleIdx < system.length; particleIdx++) {
            double[] rx = new double[TrajectoryData.ORDER + 1];
            double[] ry = new double[TrajectoryData.ORDER + 1];
            WeightedDynamicParticle2D p = system[particleIdx];
            data[particleIdx] = new TrajectoryData(rx, ry, p.getId(), p.getWeight(), p.getRadius());
        }
        // initialize values for both axis
        initializeBothAxis(data, system);
        return data;
    }

    private void initializeBothAxis(TrajectoryData[] data, WeightedDynamicParticle2D[] system) {
        // fill-in values for position and speed
        for (int particleIdx = 0; particleIdx < data.length; particleIdx++) {
            WeightedDynamicParticle2D target = system[particleIdx];
            double[] rx = data[particleIdx].getRx();
            double[] ry = data[particleIdx].getRy();
            rx[0] = target.getXCoordinate();
            ry[0] = target.getYCoordinate();
            rx[1] = target.getXSpeed();
            ry[1] = target.getYSpeed();
        }

        // compute the accelerations and derivatives using the whole system
        for (int der = 2; der < TrajectoryData.ORDER + 1; der++) {
            for (int particleIdx = 0; particleIdx < data.length; particleIdx++) {
                double[] rx = data[particleIdx].getRx();
                double[] ry = data[particleIdx].getRy();
                double mass = data[particleIdx].getMass();
                rx[der] = sumOfDerivativeOfForceInAxis(Axis.X, der, data[particleIdx], data) / mass;
                ry[der] = sumOfDerivativeOfForceInAxis(Axis.Y, der, data[particleIdx], data) / mass;
            }
        }
    }

    @Override
    public double computeForceInAxis(Axis axis, TrajectoryData body1, TrajectoryData body2) {
        if (body1.equals(body2)) {
            return 0;
        }
        double distance = body1.getAxis(axis)[0] - body2.getAxis(axis)[0];
        double eij = distance / Math.abs(distance);
        return G * body1.getMass() * body2.getMass() / Math.pow(distance, 2) * eij;
    }

    private double sumOfDerivativeOfForceInAxis(Axis axis, int order, TrajectoryData target, TrajectoryData[] data) {
        // sum the derivative of the forces between target and the other particles
        return Arrays.stream(data)
         .filter(d -> !target.equals(d))
         .mapToDouble(d -> {
             double ra = target.getAxis(axis)[0];
             double rb = d.getAxis(axis)[0];
             return derivativeOfForce(ra, rb, target.getMass(), d.getMass(), order);
         }).sum();
    }

    private double derivativeOfForce(double ra, double rb, double ma, double mb, int order) {
        double eij = (ra - rb) / Math.abs(ra - rb);
        if (order == 2) {
            return       G * (ma * mb) / Math.pow(ra - rb, 2) * eij;
        } else if (order == 3) {
            return  -2 * G * (ma * mb) / Math.pow(ra - rb, 3) * eij;
        } else if (order == 4) {
            return   6 * G * (ma * mb) / Math.pow(ra - rb, 4) * eij;
        } else if (order == 5) {
            return -24 * G * (ma * mb) / Math.pow(ra - rb, 5) * eij;
        }
        throw new IllegalArgumentException("didn't expect that");
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

    private double evalAxisWithGPCO5(double deltaT, double force, double mass, double[] pred) {
        double r2 = force / mass;
        return (r2 - pred[2]) * Math.pow(deltaT, 2) / 2; //deltaR2
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


    private TrajectoryData updateByGPCO5(double deltaT, TrajectoryData target, TrajectoryData... system) {
        // predict
        double[] xAxis = predictAxisWithGPCO5(deltaT, target.getRx());
        double[] yAxis = predictAxisWithGPCO5(deltaT, target.getRy());

        // eval
        double forceX = computeEffectiveForceInAxis(Axis.X, target, system);
        double forceY = computeEffectiveForceInAxis(Axis.Y, target, system);
        double deltaR2x = evalAxisWithGPCO5(deltaT, forceX, target.getMass(), xAxis);
        double deltaR2y = evalAxisWithGPCO5(deltaT, forceY, target.getMass(), yAxis);

        // correct
        double[] rcx = correctAxisWithGPCO5(deltaT, deltaR2x, xAxis);
        double[] rcy = correctAxisWithGPCO5(deltaT, deltaR2y, yAxis);

        return new TrajectoryData(rcx, rcy, target.getId(), target.getMass(), target.getRadius());
    }
}
