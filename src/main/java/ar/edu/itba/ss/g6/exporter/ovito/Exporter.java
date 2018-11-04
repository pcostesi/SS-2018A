package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.tp.tp5.CommandLineOptions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Exporter<T extends Particle> {

    public void addFrameToFile(BufferedWriter w, Collection<T> particles, double timeStep, CommandLineOptions values) throws IOException {
        w.write(exportFrame(new HashSet<>(particles), timeStep).collect(Collectors.joining("\n")));
        w.write('\n');
    }

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

    Stream<String> exportFrame(Collection<T> particles, double time) {
        Stream<String> header = Stream.of(String.valueOf(particles.size()), String.format("t%10f", time));
        Stream<String> particleStream = particles.stream()
         .map(this::serializeParticle);
        return Stream.concat(header, particleStream);
    }

    protected abstract String serializeParticle(T particle);

    private boolean withinBounds(TheParticle p, CommandLineOptions values){
        return p.getPosition().getY() < values.getLenght() && p.getPosition().getX() - p.getRadius() > 0 &&
                p.getPosition().getX() + p.getRadius() < values.getWidth();
    }

    Stream<String> exportAnimation(List<? extends Collection<T>> timeline, double timeStep) {
        return IntStream.range(0, timeline.size())
         .mapToObj(index -> exportFrame(timeline.get(index), index * timeStep))
         .flatMap(Function.identity());
    }
}
