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


    @Override
    public String[] values() {
        return new String[] {
         getId(),
         String.valueOf(getXCoordinate()),
         String.valueOf(getYCoordinate()),
         String.valueOf(getXSpeed()),
         String.valueOf(getYSpeed()),
         String.valueOf(getxAcceleration()),
         String.valueOf(getyAcceleration()),
         String.valueOf(getPreviousAccelerationX()),
         String.valueOf(getPreviousAccelerationY()),
         String.valueOf(getRadius()),
         String.valueOf(getWeight())
        };
    }

    public static TheParticle fromValues(String ...values) {
        return new TheParticle(values[0],
         Double.parseDouble(values[1]),
         Double.parseDouble(values[2]),
         Double.parseDouble(values[3]),
         Double.parseDouble(values[4]),
         Double.parseDouble(values[5]),
         Double.parseDouble(values[6]),
         Double.parseDouble(values[7]),
         Double.parseDouble(values[8]),
         Double.parseDouble(values[9]),
         Double.parseDouble(values[10]));
    }

}
