package ar.edu.itba.ss.g6.topology.particle;

public interface Particle {
    String getId();
    double getRadius();
    String[] values();
    boolean overlapsWith(Particle p);
}
