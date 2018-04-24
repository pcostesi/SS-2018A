package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Arrays;

public class BeemanForceSimulator implements ForceSimulator {
    private double deltaT;
    private final double G = 6.67384E-20;

    public BeemanForceSimulator(double deltaT) {
        this.deltaT = deltaT;
    }

    @Override
    public TrajectoryData moveParticle(TrajectoryData target, TrajectoryData... system) {
        double sTs = deltaT;
        double sTs2 = Math.pow(sTs, 2);
        double nRx, nRy;
        double nVx, nVy;
        double rx = target.getRx()[0];
        double ry = target.getRy()[0];
        double vx = target.getRx()[1];
        double vy = target.getRy()[1];
        double ax = target.getRx()[2];
        double ay = target.getRy()[2];
        double pax = target.getRx()[3]; // p stands for past
        double pay = target.getRy()[3];

        // Compute new position
        nRx = rx + vx * sTs + 2./3 * ax * sTs2 - 1./6 * pax * sTs2;
        target.getRx()[0] = nRx;

        nRy = ry + vy * sTs + 2./3 * ay * sTs2 - 1./6 * pay * sTs2;
        target.getRx()[0] = nRy;

        // Compute new acceleration
        double nAx = computeEffectiveForceInAxis(Axis.X, target, system) / target.getMass();
        double nAy = computeEffectiveForceInAxis(Axis.Y, target, system) / target.getMass();

        // Compute new speed
        nVx = vx + 1./3 * nAx * sTs + 5./6 * ax * sTs - 1./6 * pax * sTs;
        nVy = vy + 1./3 * nAy * sTs + 5./6 * ay * sTs - 1./6 * pay * sTs;

        double[] rcx = new double[]{nRx, nVx, nAx, ax};
        double[] rcy = new double[]{nRy, nVy, nAy, ay};
        return new TrajectoryData(rcx, rcy, target.getId(), target.getMass(), target.getRadius());
    }

    @Override
    public TrajectoryData[] initParticles(WeightedDynamicParticle2D... system) {
        // initialize structures
        TrajectoryData[] data = new TrajectoryData[system.length];
        TrajectoryData[] prev = new TrajectoryData[system.length];


        for (int particleIdx = 0; particleIdx < system.length; particleIdx++) {
            WeightedDynamicParticle2D p = system[particleIdx];
            double[] rx = new double[] {p.getXCoordinate(), p.getXSpeed(), 0, 0};
            double[] ry = new double[] {p.getYCoordinate(), p.getYSpeed(), 0, 0};
            data[particleIdx] = new TrajectoryData(rx, ry, p.getId(), p.getWeight(), p.getRadius());
        }

        for (int particleIdx = 0; particleIdx < system.length; particleIdx++) {
            WeightedDynamicParticle2D p = system[particleIdx];
            double ax = computeEffectiveForceInAxis(Axis.X, data[particleIdx], data) / p.getWeight();
            double ay = computeEffectiveForceInAxis(Axis.Y, data[particleIdx], data) / p.getWeight();
            double prevX = p.getXCoordinate() - p.getXSpeed() * deltaT - ax * Math.pow(deltaT, 2) / 2;
            double prevY = p.getYCoordinate() - p.getYSpeed() * deltaT - ay * Math.pow(deltaT, 2) / 2;
            double[] rx = new double[] {prevX, 0, ax, 0};
            double[] ry = new double[] {prevY, 0, ay, 0};
            prev[particleIdx] = new TrajectoryData(rx, ry, p.getId(), p.getWeight(), p.getRadius());
        }

        for (int particleIdx = 0; particleIdx < system.length; particleIdx++) {
            WeightedDynamicParticle2D p = system[particleIdx];
            double ax = prev[particleIdx].getRx()[2];
            double ay = prev[particleIdx].getRy()[2];

            double pax = computeEffectiveForceInAxis(Axis.X, prev[particleIdx], prev) / p.getWeight();
            double pay = computeEffectiveForceInAxis(Axis.Y, prev[particleIdx], prev) / p.getWeight();

            double[] rx = new double[] {p.getXCoordinate(), p.getXSpeed(), ax, pax};
            double[] ry = new double[] {p.getYCoordinate(), p.getYSpeed(), ay, pay};
            data[particleIdx] = new TrajectoryData(rx, ry, p.getId(), p.getWeight(), p.getRadius());
        }

        return data;
    }


    @Override
    public double computeForceInAxis(Axis axis, TrajectoryData body1, TrajectoryData body2) {
        if (body1.equals(body2)) {
            return 0;
        }
        double distance = body2.getAxis(axis)[0] - body1.getAxis(axis)[0];
        double eij = distance / Math.abs(distance);
        return G * body1.getMass() * body2.getMass() / Math.pow(distance, 2) * eij;
    }
}
