package ar.edu.itba.ss.g6.topology.particle;

public interface DynamicParticle extends Particle {
    public double getSpeed();
    public <T extends DynamicParticle> double timeToCollision(T other);
    public double timeToX(double xLimit);
    public double timeToY(double yLimit);
}
