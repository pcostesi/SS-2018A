package ar.edu.itba.ss.g6.topology.particle;

public class WeightedDynamicParticle2D extends DynamicParticle2D implements WeightedParticle {
    double weight;

    public WeightedDynamicParticle2D(String id, double x, double y, double vx, double vy, double radius, double weight) {
        super(id, x, y, vx, vy, radius);
        this.weight = weight;
    }


    @Override
    public String toString() {
        return String.format("WeightedDynamicParticle2D <%5s> (%.3e, %.3e) -> (vx: %.3e, vy: %.3e) rad %.3e weight %.3e",
         getId(), getXCoordinate(), getYCoordinate(), getXSpeed(), getYSpeed(), getRadius(), getWeight());
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
