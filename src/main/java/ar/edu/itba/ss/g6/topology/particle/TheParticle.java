package ar.edu.itba.ss.g6.topology.particle;

public class TheParticle extends WeightedDynamicParticle2D {
    private final double pax;
    private final double pay;

    public TheParticle(String id, double x, double y, double vx, double vy, double radius, double weight) {
        super(id, x, y, vx, vy, radius, weight);
        pax = pay = 0;
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double ax, double ay, double radius, double weight) {
        super(id, x, y, vx, vy, ax, ay, radius, weight);
        pax = pay = 0;
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double ax, double ay, double pax, double pay, double radius, double weight) {
        super(id, x, y, vx, vy, ax, ay, radius, weight);
        this.pax = pax;
        this.pay = pay;
    }

    public double getPreviousAccelerationX() {
        return pax;
    }

    public double getPreviousAccelerationY() {
        return pay;
    }

}
