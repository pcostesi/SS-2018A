package ar.edu.itba.ss.g6.topology.particle;

public interface DynamicParticle extends Particle {
    double getSpeed();
    <T extends DynamicParticle> double timeToCollision(T other);
    double timeToX(double xLimit);
    double timeToY(double yLimit);
    double getAcceleration();
}
