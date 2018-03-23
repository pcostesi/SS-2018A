package ar.edu.itba.ss.g6.exporter.ovito;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public abstract class Exporter<T extends Particle> {
    abstract Stream<String> exportFrame(Collection<T> particles, double time);
    abstract Stream<String> exportAnimation(List<Collection<T>> timeline, double timeStep);

    public void saveAnimationToFile(Path path, List<Collection<T>> timeline, double timeStep) throws IOException {
        Stream<String> stream = exportAnimation(timeline, timeStep);
        Files.write(path, (Iterable<String>)stream::iterator);
    }

    public void saveAnimationToFile(String path, List<Collection<T>> timeline, double timeStep) throws IOException {
        saveAnimationToFile(Paths.get(path), timeline, timeStep);
    }


    public void saveFrameToFile(Path path, Collection<T> particles, double time) throws IOException {
        Stream<String> stream = exportFrame(particles, time);
        Files.write(path, (Iterable<String>)stream::iterator);
    }

    public void saveFrameToFile(String path, Collection<T> particles, double time) throws IOException {
        saveFrameToFile(Paths.get(path), particles, time);
    }
}
