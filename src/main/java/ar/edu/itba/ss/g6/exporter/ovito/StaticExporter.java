package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class StaticExporter<T extends Particle> extends Exporter<T> {

    @Override
    public Stream<String> exportFrame(Collection<T> particles, double time) {
        Stream<String> header = Stream.of(String.valueOf(particles.size()), String.format("t%10f", time));
        Stream<String> particleStream = particles.stream()
            .map(this::serializeParticle);
        return Stream.concat(header, particleStream);
    }

    public abstract String serializeParticle(T particle);

    @Override
    public Stream<String> exportAnimation(List<? extends Collection<T>> timeline, double timeStep) {
        return IntStream.range(0, timeline.size())
            .mapToObj(index -> exportFrame(timeline.get(index), index * timeStep))
            .flatMap(Function.identity());
    }
}
