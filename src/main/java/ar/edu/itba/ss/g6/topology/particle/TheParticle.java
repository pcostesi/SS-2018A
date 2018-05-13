package ar.edu.itba.ss.g6.topology.particle;

import ar.edu.itba.ss.g6.topology.vector.V2d;

public class TheParticle implements Particle {
    private final String id;
    private final double radius;
    private final double mass;
    private final V2d position;
    private final V2d velocity;
    private final V2d acceleration;
    private final V2d prevAcceleration;


    public TheParticle(String id, V2d position, V2d velocity, V2d acceleration, V2d prevAcceleration, double radius, double mass) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.prevAcceleration = prevAcceleration;
        this.radius = radius;
        this.mass = mass;
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double radius, double mass) {
        this(id, x, y, vx, vy, 0, 0, 0, 0, radius, mass);
    }

    public TheParticle(String id, double x, double y, double vx, double vy, double ax, double ay, double pax, double pay, double radius, double mass) {
        this.id = id;
        this.position = new V2d(x, y);
        this.velocity = new V2d(vx, vy);
        this.acceleration = new V2d(ax, ay);
        this.prevAcceleration = new V2d(pax, pay);
        this.radius = radius;
        this.mass = mass;
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
        return new String[] {
         getId(),
         String.valueOf(position.x),
         String.valueOf(position.y),
         String.valueOf(velocity.x),
         String.valueOf(velocity.y),
         String.valueOf(acceleration.x),
         String.valueOf(acceleration.y),
         String.valueOf(prevAcceleration.x),
         String.valueOf(prevAcceleration.y),
         String.valueOf(getRadius()),
         String.valueOf(getMass())
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
        position.distance(particle.getPosition());
        double radiusDistance = this.getRadius() + particle.getRadius();
        double rawDistance =  position.distance(particle.getPosition());
        return rawDistance - radiusDistance;
    }

    public double getKineticEnergy() {
        return 0.5 * getMass() * (Math.pow(getVelocity().getX(), 2) + Math.pow(getVelocity().getY(), 2));
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
         getPrevAcceleration().toString()
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
}
