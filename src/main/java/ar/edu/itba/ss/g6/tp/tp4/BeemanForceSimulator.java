package ar.edu.itba.ss.g6.tp.tp4;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;

import java.util.Arrays;

public class BeemanForceSimulator implements ForceSimulator {
    private double deltaT;
    private final double G = 6.67191E-20;

    public BeemanForceSimulator(double deltaT) {
        this.deltaT = deltaT;
    }

    @Override
    public TrajectoryData moveParticle(TrajectoryData target, TrajectoryData... system) {
        double sTs = deltaT;
        double nRx, nRy;
        double nVx, nVy, pVx, pVy;
        double rx = target.getRx()[0];
        double ry = target.getRy()[0];
        double vx = target.getRx()[1];
        double vy = target.getRy()[1];
        double ax = target.getRx()[2];
        double ay = target.getRy()[2];
        double pax = target.getRx()[3];
        double pay = target.getRy()[3];

        // Calculate new position and predicted speed
        nRx = rx + vx * sTs
         + ( (2.0 / 3.0) * ax  - (1.0 / 6.0) * pax ) * Math.pow(sTs, 2.0);
        pVx = vx + (3.0 / 2.0) * ax * sTs
         - (1.0 / 2.0) * pax * sTs;

        nRy = ry + vy * sTs
         + ( (2.0 / 3.0) * ay  - ( 1.0 / 6.0) * pay ) * Math.pow(sTs, 2.0);
        pVy = vy + (3.0 / 2.0) * ay * sTs
         - (1.0 / 2.0) * pay * sTs;

        // Calculate t+DT acceleration
        double nAx = computeEffectiveForceInAxis(Axis.X, target, system) / target.getMass();
        double nAy = computeEffectiveForceInAxis(Axis.Y, target, system) / target.getMass();

        // Update speed
        nVx = pVx + (1.0/3.0) * nAx * sTs + (5.0/6.0) * ax * sTs - (1.0/6.0) * pax * sTs;
        nVy = pVy + (1.0/3.0) * nAy * sTs + (5.0/6.0) * ay * sTs - (1.0/6.0) * pay* sTs;

        // Update particle with aproximated speed
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
            prev[particleIdx] = initPreviousParticle(data[particleIdx], data);
        }

        for (int particleIdx = 0; particleIdx < system.length; particleIdx++) {
            data[particleIdx] = initRealParticle(data[particleIdx], prev[particleIdx], prev);
        }
        return data;
    }

    private TrajectoryData initPreviousParticle(TrajectoryData data, TrajectoryData... system) {
        double simulationTimeStep = deltaT;
        double[] rcx = data.getRx();
        double[] rcy = data.getRy();

        double rx = rcx[0];
        double ry = rcy[0];
        double vx = rcx[1];
        double vy = rcy[1];

        double prevRX = rx - vx * simulationTimeStep;
        double prevRY = ry - vy * simulationTimeStep;

        double ax = computeEffectiveForceInAxis(Axis.X, data, system) / data.getMass();
        double ay = computeEffectiveForceInAxis(Axis.Y, data, system) / data.getMass();

        double prevVX = vx - ax * simulationTimeStep;
        double prevVY = vy - ay * simulationTimeStep;

        double[] nrcx = new double[] {prevRX, prevVX, ax, 0};
        double[] nrcy = new double[] {prevRY, prevVY, ax, 0};
        return new TrajectoryData(nrcx, nrcy, data.getId(), data.getMass(), data.getRadius());
    }

    private TrajectoryData initRealParticle(TrajectoryData real, TrajectoryData data, TrajectoryData... system) {
        double[] rcx = real.getRx();
        double[] rcy = real.getRy();

        double pax = computeEffectiveForceInAxis(Axis.X, data, system) / data.getMass();
        double pay = computeEffectiveForceInAxis(Axis.Y, data, system) / data.getMass();

        rcx[3] = pax;
        rcy[3] = pay;
        return real;
    }


    @Override
    public double computeForceInAxis(Axis axis, TrajectoryData body1, TrajectoryData body2) {
        if (body1.equals(body2)) {
            return 0;
        }
        double distance = body1.getAxis(axis)[0] - body2.getAxis(axis)[0];
        return G * body1.getMass() * body2.getMass() / Math.pow(distance, 2);
    }
}
