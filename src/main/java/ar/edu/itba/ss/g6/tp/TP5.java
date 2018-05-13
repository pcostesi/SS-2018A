package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.loader.DynamicDataLoader;
import ar.edu.itba.ss.g6.loader.ParticleLoader;
import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.topology.particle.ParticleDyn2DWeigGenerator;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;
import ar.edu.itba.ss.g6.tp.tp5.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp5.GranularSimulation;
import ar.edu.itba.ss.g6.tp.tp5.GranularSimulationFrame;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TP5 {
    public static void main(String ...args) {

        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }


        if (values.isGenerate()) {
            generate(values);
            return;
        } else if (values.isSimulate()) {
            simulate(values);
            return;
        }

        System.exit(-1);
    }

    private static void generate(CommandLineOptions values) {

        double weight = values.getParticleMass();
        double w = values.getWidth();
        double l = values.getLenght();
        int n = values.getParticles();
        double minD = values.getMinDiameter();
        double maxD = values.getMaxDiameter();
        Path output = values.getOutFile();

        ParticleDyn2DWeigGenerator generator = new ParticleDyn2DWeigGenerator(weight, w, l, n, minD, maxD);

        Set<TheParticle> particles = generator.generate();

        Exporter<TheParticle> exporter = new OvitoXYZExporter<>();

        try {
            exporter.saveFrameToFile(output, particles, 0);
        } catch (IOException e) {
            System.err.println("Oh shi...");
            System.exit(1);
        }
    }

    private static void simulate(CommandLineOptions values) {
        Simulation<TheParticle, GranularSimulationFrame> simulation = granularSimulation(values);
        GranularSimulationFrame frame;
        double stopTime = values.getDuration();
        Path output = values.getOutFile();
        Exporter<TheParticle> exporter = new OvitoXYZExporter<>();

        int framesCaptured = 0;
        double FPS = values.getFps();
        double deltaT = values.getTimeStep();
        Set<TheParticle> boundaries = Set.of(new TheParticle("-1", 0, values.getLenght() / -10, 0, 0, 0.001, 0),
         new TheParticle("-2", values.getWidth(), values.getLenght(), 0, 0, 0.001, 0));

        try (BufferedWriter out = Files.newBufferedWriter(output, Charset.defaultCharset())) {
            while ((frame = simulation.getNextStep()) != null && frame.getTimestamp() <= stopTime) {
                double ts = frame.getTimestamp();
                if (ts >= framesCaptured * FPS * deltaT && ts < framesCaptured * FPS * deltaT + deltaT) {
                    Set<TheParticle> particles = new HashSet<>();
                    particles.addAll(boundaries);
                    particles.addAll(frame.getState());
                    System.out.printf("%d - %f\n", framesCaptured, frame.getTimestamp());
                    exporter.addFrameToFile(out, particles, frame.getTimestamp());
                    framesCaptured += 1;
                }
            }
        } catch (IOException e) {
            System.err.println("Oh f...");
            System.exit(4);
        }
    }

    private static Simulation<TheParticle, GranularSimulationFrame> granularSimulation(CommandLineOptions values) {
        double width = values.getWidth();
        double height = values.getLenght();
        double aperture = values.getAperture();
        double deltaT = values.getTimeStep();
        Set<TheParticle> particles = loadParticles(values);

        Simulation<TheParticle, GranularSimulationFrame> simulation = new GranularSimulation(deltaT, width, height, aperture, particles);
        return simulation;
    }

    private static Set<TheParticle> loadParticles(CommandLineOptions values) {
        ParticleLoader<TheParticle> loader = new DynamicDataLoader<>(TheParticle.class);
        List<TheParticle> listOfParticles;
        try {
            listOfParticles = loader.loadFromFile(values.getInFile());
            return new HashSet<>(listOfParticles);
        } catch (IOException e) {
            System.err.println("Couldn't load the particles");
            System.exit(2);
        }
        return null;
    }
}
