package ar.edu.itba.ss.g6.topology.force;

import ar.edu.itba.ss.g6.topology.geometry.Wall;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.topology.vector.V2d;
import ar.edu.itba.ss.g6.tp.tp5.GranularSimulation;

public class GranularForce implements Force {


    private final double KN;
    private final double KT;

    public GranularForce(double kn, double kt) {
        this.KN = kn;
        this.KT = kt;
    }

    public V2d getForce(final TheParticle particle, final TheParticle otherParticle) {
        if (particle.collides(otherParticle) && !particle.equals(otherParticle)) {
            final double distance = particle.getPosition().distance(otherParticle.getPosition());
            final double totalRadius = particle.getRadius() + otherParticle.getRadius();
            final V2d normalDirection = particle.getPosition().substract(otherParticle.getPosition()).normalize();
            final V2d normalForce = normalDirection.scale(KN * (totalRadius - distance));
            final V2d tangentialDirection = new V2d(-normalDirection.getY(), normalDirection.getX());
            final double deltaVelocity = otherParticle.getVelocity().substract(particle.getVelocity()).dot(tangentialDirection);
            final V2d tangentialForce = tangentialDirection.scale(KT * (totalRadius - distance) * deltaVelocity);
            return tangentialForce.add(normalForce);
        }
        return new V2d(0, 0);

    }

    public V2d getForce(final TheParticle particle, final Wall wall) {
        final V2d intersectionPoint = wall.intersection(particle);
        final double distance = intersectionPoint.distance(particle.getPosition());
        if (distance < particle.getRadius()) {
            final V2d normalDirection = particle.getPosition().substract(intersectionPoint).normalize();
            final V2d normalForce = normalDirection.scale(KN * (particle.getRadius() - distance));
            final V2d tangentialDirection = new V2d(-normalDirection.getY(), normalDirection.getX());
            final double deltaVelocity = particle.getVelocity().dot(tangentialDirection);
            final V2d tangentialForce = tangentialDirection.scale(-KT * (particle.getRadius() - distance) * deltaVelocity);
            return normalForce.add(tangentialForce);
        }
        return new V2d(0, 0);
    }
}
