package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle;

public class OvitoXYZExporter<T extends Particle> extends Exporter<T>  {

    @Override
    public String serializeParticle(T particle) {
        return String.join("\t", particle.values());
    }
}
