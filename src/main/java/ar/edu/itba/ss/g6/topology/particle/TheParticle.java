package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.vector.V2d;

import java.text.DecimalFormat;

public class TheParticle implements Particle {
    private final String id;
    private final double radius;
    private final double mass;
    private final V2d position;
    private final V2d velocity;
    private final V2d acceleration;
    private final V2d prevAcceleration;
    private double totalNormalForce = 0;


    public TheParticle(String id, V2d position, V2d velocity, V2d acceleration, V2d prevAcceleration, double radius, double mass) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.prevAcceleration = prevAcceleration;
        this.radius = radius;
        this.mass = mass;
        this.totalNormalForce = 0;
    }

    public TheParticle(String id, V2d position, V2d velocity, V2d acceleration, V2d prevAcceleration, double radius, double mass, double totalNormalForce) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.prevAcceleration = prevAcceleration;
        this.radius = radius;
        this.mass = mass;
        this.totalNormalForce = totalNormalForce;
    }


    public TheParticle(String id, double x, double y, double vx, double vy, double ax, double ay, double pax, double pay, double radius, double mass) {
        this.id = id;
        this.position = new V2d(x, y);
        this.velocity = new V2d(vx, vy);
        this.acceleration = new V2d(ax, ay);
        this.prevAcceleration = new V2d(pax, pay);
        this.radius = radius;
        this.mass = mass;
        this.totalNormalForce = 0;
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double radius, double mass) {
        this(id, x, y, vx, vy, 0, 0, 0, 0, radius, mass, 0);
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double radius, double mass, double totalNormalForce) {
        this(id, x, y, vx, vy, 0, 0, 0, 0, radius, mass, totalNormalForce);
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double ax, double ay, double pax, double pay, double radius, double mass, double totalNormalForce) {
        this.id = id;
        this.position = new V2d(x, y);
        this.velocity = new V2d(vx, vy);
        this.acceleration = new V2d(ax, ay);
        this.prevAcceleration = new V2d(pax, pay);
        this.radius = radius;
        this.mass = mass;
        this.totalNormalForce = totalNormalForce;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    @Override
    public String[] values() {
        DecimalFormat df = new DecimalFormat("#0.000000");

        return new String[] {
                getId(),
                df.format(position.getX()),
                df.format(position.getY()),
                df.format(velocity.getX()),
                df.format(velocity.getY()),
                df.format(acceleration.getX()),
                df.format(acceleration.getY()),
                df.format(prevAcceleration.getX()),
                df.format(prevAcceleration.getY()),
                df.format(getRadius()),
                df.format(getMass()),
                df.format(this.getNormalForce()/(2 * 3.14159265 * radius * 60))
        };
    }

    public V2d getPosition() {
        return position;
    }

    public V2d getVelocity() {
        return velocity;
    }

    public V2d getAcceleration() {
        return acceleration;
    }

    public V2d getPrevAcceleration() {
        return prevAcceleration;
    }

    @Override
    public boolean overlapsWith(Particle p) {
        if (!(p instanceof TheParticle)) {
            throw new IllegalArgumentException("Wrong class :P");
        }
        TheParticle particle = (TheParticle) p;
        return distanceTo(particle) <= 0;
    }

    public boolean collides(TheParticle particle) {
        return overlapsWith(particle);
    }

    public double distanceTo(TheParticle particle) {
        double radiusDistance = this.getRadius() + particle.getRadius();
        double rawDistance =  position.distance(particle.getPosition());
        return rawDistance - radiusDistance;
    }

    public double distanceTo2(TheParticle particle) {
        double radiusDistance = this.getRadius() + particle.getRadius();
        double rawDistance2 =  position.distance2(particle.getPosition());
        return rawDistance2 - radiusDistance * radiusDistance;
    }

    public double getKineticEnergy() {
        double vx2 = getVelocity().getX() * getVelocity().getX();
        double vy2 = getVelocity().getY() * getVelocity().getY();
        return 0.5 * getMass() * (vx2 + vy2);
    }

    @Override
    public String toString() {
        return "<" + String.join(", ", new String[] {
         getId(),
         String.valueOf(getRadius()),
         String.valueOf(getMass()),
         getPosition().toString(),
         getVelocity().toString(),
         getAcceleration().toString(),
         getPrevAcceleration().toString(),
         Double.toString(this.getNormalForce())
        }) + ">";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TheParticle that = (TheParticle) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void addNormalForce(double normalForce) {
        totalNormalForce += normalForce;
    }

    public void resetNormalForce() {
        totalNormalForce = 0;
    }

    public double getNormalForce() {
        return totalNormalForce;
    }

}
