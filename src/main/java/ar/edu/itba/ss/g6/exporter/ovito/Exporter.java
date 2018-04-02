package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Exporter<T extends Particle> {

    public void saveAnimationToFile(Path path, List<? extends Collection<T>> timeline, double timeStep) throws IOException {
        Stream<String> stream = exportAnimation(timeline, timeStep);
        Files.write(path, (Iterable<String>)stream::iterator);
    }

    public void saveAnimationToFile(String path, List<? extends Collection<T>> timeline, double timeStep) throws IOException {
        saveAnimationToFile(Paths.get(path), timeline, timeStep);
    }


    public void saveFrameToFile(Path path, Collection<T> particles, double time) throws IOException {
        Stream<String> stream = exportFrame(particles, time);
        Files.write(path, (Iterable<String>)stream::iterator);
    }

    public void saveFrameToFile(String path, Collection<T> particles, double time) throws IOException {
        saveFrameToFile(Paths.get(path), particles, time);
    }

    public Stream<String> exportFrame(Collection<T> particles, double time) {
        Stream<String> header = Stream.of(String.valueOf(particles.size()), String.format("t%10f", time));
        Stream<String> particleStream = particles.stream()
         .map(this::serializeParticle);
        return Stream.concat(header, particleStream);
    }

    public abstract String serializeParticle(T particle);


    public Stream<String> exportAnimation(List<? extends Collection<T>> timeline, double timeStep) {
        return IntStream.range(0, timeline.size())
         .mapToObj(index -> exportFrame(timeline.get(index), index * timeStep))
         .flatMap(Function.identity());
    }
}
