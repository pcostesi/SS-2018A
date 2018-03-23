package ar.edu.itba.ss.g6.topology.particle;

public interface Particle {
    String getId();
    double getRadius();
    boolean isWithinRadius(double radius, Particle p);
    String[] values();
}
