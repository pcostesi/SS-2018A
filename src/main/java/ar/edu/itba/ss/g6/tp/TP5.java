package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.loader.DynamicDataLoader;
import ar.edu.itba.ss.g6.loader.ParticleLoader;
import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.topology.particle.ParticleDyn2DWeigGenerator;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp5.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp5.GranularSimulation;
import ar.edu.itba.ss.g6.tp.tp5.GranularSimulationFrame;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
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
        double minRad = values.getMinRadius();
        double maxRad = values.getMaxRadius();
        Path output = values.getOutFile();

        ParticleDyn2DWeigGenerator generator = new ParticleDyn2DWeigGenerator(weight, w, l, n, minRad, maxRad);

        Set<WeightedDynamicParticle2D> particles = generator.generate();

        Exporter<WeightedDynamicParticle2D> exporter = new OvitoXYZExporter<>();

        try {
            exporter.saveFrameToFile(output, particles, 0);
        } catch (IOException e) {
            System.err.println("Oh shi...");
            System.exit(1);
        }
    }

    private static void simulate(CommandLineOptions values) {
        Simulation<WeightedDynamicParticle2D, GranularSimulationFrame> simulation = granularSimulation(values);
        GranularSimulationFrame frame;
        double stopTime = values.getDuration();

        while ((frame = simulation.getNextStep()) != null && frame.getTimestamp() < stopTime) {
            System.out.println(frame.getTimestamp());
            System.out.println(frame.getState());
        }
    }

    private static Simulation<WeightedDynamicParticle2D, GranularSimulationFrame> granularSimulation(CommandLineOptions values) {
        double width = values.getWidth();
        double height = values.getLenght();
        double aperture = values.getAperture();
        double deltaT = values.getTimeStep();
        Set<WeightedDynamicParticle2D> particles = loadParticles(values);

        Simulation<WeightedDynamicParticle2D, GranularSimulationFrame> simulation = new GranularSimulation(deltaT, width, height, aperture, particles);
        return simulation;
    }

    private static Set<WeightedDynamicParticle2D> loadParticles(CommandLineOptions values) {
        ParticleLoader<WeightedDynamicParticle2D> loader = new DynamicDataLoader<>(WeightedDynamicParticle2D.class);
        List<WeightedDynamicParticle2D> listOfParticles;
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
