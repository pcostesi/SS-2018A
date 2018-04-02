package ar.edu.itba.ss.g6.topology.particle;

public class OverfedDynamicParticle2D extends WeightedDynamicParticle2D {
    public OverfedDynamicParticle2D(String id, double x, double y, double vx, double vy, double radius, double weight) {
        super(id, x, y, vx, vy, radius, weight * 1000);
    }
}
