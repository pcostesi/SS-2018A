package ar.edu.itba.ss.g6.loader;

import ar.edu.itba.ss.g6.topology.particle.Particle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Files.*;

public class StaticDataLoader {

    public static <T extends Particle> StaticLoaderResult<T> importFromFile(Path path, ParticleLoader<T> loader) {
        try (BufferedReader reader = newBufferedReader(path)) {
            String count = reader.readLine();
            String header = reader.readLine();
            return new StaticLoaderResult<>(Integer.parseInt(count, 10), header, reader.lines()
             .map(line -> loader.fromStringValues(line.split("\t+")))
             .limit(Integer.parseInt(count, 10))
             .collect(Collectors.toSet()));

        } catch (IOException err) {
            return new StaticLoaderResult<>(0, null, Set.of());
        }
    }


    public static <T extends Particle> StaticLoaderResult<T> importFromFile(File path, ParticleLoader<T> loader) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String count = reader.readLine();
            String header = reader.readLine();
            return new StaticLoaderResult<>(Integer.parseInt(count, 10), header, reader.lines()
             .map(line -> loader.fromStringValues(line.split("\t+")))
             .limit(Integer.parseInt(count, 10))
             .collect(Collectors.toSet()));

        } catch (IOException err) {
            return new StaticLoaderResult<>(0, null, Set.of());
        }
    }
}
