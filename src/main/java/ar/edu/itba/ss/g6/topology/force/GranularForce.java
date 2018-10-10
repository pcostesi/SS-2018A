package ar.edu.itba.ss.g6.topology.force;

import ar.edu.itba.ss.g6.topology.geometry.Wall;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;

public class GranularForce implements Force {

    private final double Mu;
    private final double Gamma;
    private final double KN;
    private final double KT;

    public GranularForce(double Mu, double Gamma) {
        this.Mu = Mu;
        this.Gamma = Gamma;
        this.KN = 10 * 10 * 10 * 10 * 10; // N/m
        this.KT = 2 * this.KN; // N/m
    }

    public V2d getForce(final TheParticle particle, final TheParticle otherParticle) {
        if (particle.collides(otherParticle) && !particle.equals(otherParticle)) {

            final double distance2 = particle.getPosition().distance(otherParticle.getPosition());
            final double totalRadius2 = particle.getRadius() + otherParticle.getRadius();
            final double E = (totalRadius2- distance2);
            final V2d normalDirection2 = particle.getPosition().substract(otherParticle.getPosition()).normalize();
            final V2d normalForce2 = normalDirection2.scale(KN * E);
            final V2d deltaVel = otherParticle.getVelocity().substract(particle.getVelocity());
            final V2d normalForce22 = normalDirection2.scale(-KN * E - (Gamma * deltaVel.cross(normalDirection2)));
            final V2d tangentialDirection2 = new V2d(-normalDirection2.y, normalDirection2.x);
            // final double deltaVelocity = otherParticle.getVelocity().substract(particle.getVelocity()).dot(tangentialDirection2);
            final V2d tangentialForce2 = tangentialDirection2.scale(KT * E * deltaVel.dot(tangentialDirection2));
            final V2d tangentialForce22 = tangentialDirection2.scale(deltaVel.sign().scale(-Mu * normalForce22.module()).dot(tangentialDirection2));

            return tangentialForce2.add(normalForce2);
            // return tangentialForce22.add(normalForce22);
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

            final V2d normalForce = normalDirection.scale(KN * E - (Gamma * deltaVel.cross(normalDirection)));
            final V2d tangentialForce = tangentialDirection.scale(-KT * E * deltaVel.dot(normalDirection));
            final V2d tangentialForce5 = tangentialDirection.scale(deltaVel.sign().scale(-Mu * normalForce.module()).dot(tangentialDirection));

            return normalForce.add(tangentialForce5);
        }
        return new V2d(0, 0);
    }
}
