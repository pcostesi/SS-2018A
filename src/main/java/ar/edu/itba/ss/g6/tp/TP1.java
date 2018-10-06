package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.Static2DExporter;
import ar.edu.itba.ss.g6.loader.StaticDataLoader;
import ar.edu.itba.ss.g6.loader.StaticLoaderResult;
import ar.edu.itba.ss.g6.topology.grid.Grid;
import ar.edu.itba.ss.g6.topology.grid.MapGrid2D;
import ar.edu.itba.ss.g6.topology.particle.ColoredParticle2D;
import ar.edu.itba.ss.g6.topology.particle.Particle2D;
import ar.edu.itba.ss.g6.topology.particle.Particle2DGenerator;
import ar.edu.itba.ss.g6.topology.particle.ParticleGenerator;
import ar.edu.itba.ss.g6.tp.tp1.Brush;
import ar.edu.itba.ss.g6.tp.tp1.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class TP1 {

    public static void main(String ...args) {
        Set<Particle2D> particles = Set.of();


        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }

        double radius = values.getRadius();
        long side = values.getL();
        int amount = values.getN();
        int buckets = values.getBuckets();
        double searchRadius = values.getSearchRadius();

        if (values.getStaticParticles() == null && values.getDynamicParticles() == null) {
            ParticleGenerator<Particle2D> generator = new Particle2DGenerator(radius, side, amount);
            particles = generator.generate();
            Exporter<Particle2D> exporter = new Static2DExporter<>();
            try {
                exporter.saveFrameToFile("rand.xyz", particles, side);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (values.getStaticParticles() != null) {
            StaticLoaderResult<Particle2D> loaderResult = StaticDataLoader.importFromFile(values.getStaticParticles(), arr ->
                new Particle2D(arr[0], Double.parseDouble(arr[3]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));
            particles = loaderResult.getParticles();
            side = (long) Double.parseDouble(loaderResult.getHeader().substring(1));
        } else {
            System.exit(-1);
        }
        assert values.isPeriodic();
        Grid<Particle2D> grid = new MapGrid2D<>(side, buckets, searchRadius, values.isPeriodic());
        grid.set(particles);

        Optional<Particle2D> target;
        if (values.getHighlight() == null) {
            target = particles.stream().sorted((p1, p2) -> (int) Math.ceil(p1.getYCoordinate() - p2.getYCoordinate())).findFirst();
        } else {
            target = particles.stream()
             .filter(p -> values.getHighlight().equals(p.getId()))
             .findFirst();
        }

        Collection<Particle2D> neighbors = target
            .map(targetParticle -> grid.getNeighbors(targetParticle))
            .orElse(Collections.emptySet());

        System.out.println(particles.size());

        System.out.println(values.isPeriodic());
        System.out.println(neighbors.size());

        Exporter<ColoredParticle2D> exporter = new Static2DExporter<>();
        Brush brush = new Brush(particles, 128, 128, 128);
        brush.paint(neighbors, 255, 0, 0);
        brush.paint(target, 0, 255, 0);
        Set<ColoredParticle2D> painted = brush.all();
        try {
            exporter.saveFrameToFile("abc.xyz", painted,0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
