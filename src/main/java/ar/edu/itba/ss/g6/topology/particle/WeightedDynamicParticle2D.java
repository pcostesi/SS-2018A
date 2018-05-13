package ar.edu.itba.ss.g6.topology.particle;

public class WeightedDynamicParticle2D extends DynamicParticle2D implements WeightedParticle {
    double weight;

    public WeightedDynamicParticle2D(String id, double x, double y, double vx, double vy, double radius, double weight) {
        super(id, x, y, vx, vy, radius);
        this.weight = weight;
    }

    public WeightedDynamicParticle2D(String id,
                                     double x, double y,
                                     double vx, double vy,
                                     double ax, double ay,
                                     double radius, double weight) {
        super(id, x, y, vx, vy, ax, ay, radius);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("WeightedDynamicParticle2D <%5s> (%.3e, %.3e) -> (vx: %.3e, vy: %.3e) rad %.3e weight %.3e",
         getId(), getXCoordinate(), getYCoordinate(), getXSpeed(), getYSpeed(), getRadius(), getWeight());
    }

    @Override
    public String[] values() {
        return new String[] {
         getId(),
         String.valueOf(getXCoordinate()),
         String.valueOf(getYCoordinate()),
         String.valueOf(getXSpeed()),
         String.valueOf(getYSpeed()),
         String.valueOf(getRadius()),
         String.valueOf(getWeight())
        };
    }

    public static WeightedDynamicParticle2D fromValues(String ...values) {
        return new WeightedDynamicParticle2D(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]),
            Double.parseDouble(values[3]), Double.parseDouble(values[4]), Double.parseDouble(values[5]),
             Double.parseDouble(values[6]));
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public double getKineticEnergy() {
        return (0.5) * this.weight * this.getXSpeed() * this.getXSpeed() * this.getYSpeed() * this.getYSpeed();
    }
}
