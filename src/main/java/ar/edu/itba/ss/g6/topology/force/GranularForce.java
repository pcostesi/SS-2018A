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

            final double totalRadius = particle.getRadius() + otherParticle.getRadius();
            final double distance = particle.getPosition().distance(otherParticle.getPosition());

            final double E = (totalRadius - distance);
            final V2d normalDirection = particle.getPosition().substract(otherParticle.getPosition()).normalize();
            final V2d tangentialDirection = new V2d(-normalDirection.getY(), normalDirection.getX());

            final double Ederived = otherParticle.getVelocity().substract(particle.getVelocity()).dot(normalDirection); //e.
            final V2d normalForce = normalDirection.scale((-KN * E - Gamma * Ederived));
            final V2d tangentialForce = tangentialDirection.scale(-Mu * normalForce.module() * Math.signum(Ederived));
            return  tangentialForce.add(normalForce);

//      Otra manera, da igual:    final V2d tangentialForce3 = tangentialDirection.scale( -Mu * normalForce.module() * Math.signum(Ederived));


            //Test
//            final V2d relativeVelocity = otherParticle.getVelocity().substract(particle.getVelocity());
//            final V2d distanceVector = otherParticle.getPosition().substract(particle.getPosition());
//            final double eaea = otherParticle.getRadius() + particle.getRadius() - distanceVector.module();
//            final double eaPrime = distanceVector.dot(relativeVelocity) / distanceVector.module();
//            final V2d res2 = normalDirection.scale(-KN * eaea - Gamma * Math.abs(eaPrime));


            final double distance2 = particle.getPosition().distance(otherParticle.getPosition());
            final double totalRadius2 = particle.getRadius() + otherParticle.getRadius();
            final V2d normalDirection2 = particle.getPosition().substract(otherParticle.getPosition()).normalize();
            final V2d normalForce2 = normalDirection2.scale(KN * (totalRadius2- distance2));
            final V2d tangentialDirection2 = new V2d(-normalDirection2.y, normalDirection2.x);
            final double deltaVelocity = otherParticle.getVelocity().substract(particle.getVelocity()).dot(tangentialDirection2);
            final V2d tangentialForce2 = tangentialDirection2.scale(KT * (totalRadius2 - distance2) * deltaVelocity);

            return tangentialForce2.add(normalForce2);
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
            final double Ederived = particle.getVelocity().dot(tangentialDirection); //e.

            final V2d normalForce = normalDirection.scale(KN * E - (Gamma * Ederived));
            //final V2d tangentialForce = tangentialDirection.scale(-KT * (particle.getRadius() - distance) * Ederived);
            final V2d tangentialForce = tangentialDirection.scale( -Mu * normalForce.module() * Math.signum(Ederived));

            return normalForce.add(tangentialForce);
        }
        return new V2d(0, 0);
    }
}
