package ar.edu.itba.ss.g6.topology.force;

import ar.edu.itba.ss.g6.topology.geometry.Wall;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;

public class GranularForce implements Force {

    private final double Mu;
    private final double Gamma;
    private final double KN;

    public GranularForce(double Mu, double Gamma) {
        this.Mu = Mu;
        this.Gamma = Gamma;
        this.KN = 10 * 10 * 10 * 10 * 10; // N/m
    }

    public V2d getForce(final TheParticle particle, final TheParticle otherParticle) {
        if (particle.collides(otherParticle) && !particle.equals(otherParticle)) {

            final double distance = particle.getPosition().distance(otherParticle.getPosition());
            final double totalRadius = particle.getRadius() + otherParticle.getRadius();
            final double E = (totalRadius- distance);
            final V2d normalDirection = particle.getPosition().substract(otherParticle.getPosition()).normalize();
            final V2d deltaVel = otherParticle.getVelocity().substract(particle.getVelocity());
            final V2d normalForce = normalDirection.scale(KN * E - Gamma * E);
            final V2d tangentialDirection = new V2d(-normalDirection.y, normalDirection.x);
            final V2d tangentialForce = tangentialDirection.scale(Mu * normalForce.module() * Math.signum(deltaVel.dot(tangentialDirection)));

            particle.addNormalForce(normalForce.module());
            return tangentialForce.add(normalForce);
        }
        return new V2d(0, 0);
    }


    public V2d getForce(final TheParticle particle, final Wall wall) {
        final V2d intersectionPoint = wall.intersection(particle);
        final double distance = intersectionPoint.distance(particle.getPosition());
        if (distance < particle.getRadius()) {
            final double E = (particle.getRadius() - distance);
            final V2d normalDirection = particle.getPosition().substract(intersectionPoint).normalize();
            final V2d tangentialDirection = new V2d(-normalDirection.getY(), normalDirection.getX());
            final V2d deltaVel = particle.getVelocity(); //e.

            final V2d normalForce = normalDirection.scale(KN * E - Gamma * E);
            final V2d tangentialForce = tangentialDirection.scale(Mu * normalForce.module() * Math.signum(deltaVel.dot(tangentialDirection)));

            return normalForce.add(tangentialForce);
        }
        return new V2d(0, 0);
    }
}
