package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;

public class Static2DExporter<T extends Particle2D> extends StaticExporter<T> {
    @Override
    public String serializeParticle(T particle) {
        String [] fields = particle.values();

        return String.join("\t", fields);
    }
}
