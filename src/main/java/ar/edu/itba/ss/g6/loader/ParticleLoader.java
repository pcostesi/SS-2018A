package ar.edu.itba.ss.g6.loader;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.topology.particle.Particle;
import jdk.jshell.spi.ExecutionControlProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public interface ParticleLoader<T extends Particle> {
    T fromStringValues(String[] values);

    default List<T> loadFromFile(Path input) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(input, Charset.defaultCharset())) {
            return r.lines()
             .skip(2)
             .map(l -> fromStringValues(l.split("\t")))
             .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
}
