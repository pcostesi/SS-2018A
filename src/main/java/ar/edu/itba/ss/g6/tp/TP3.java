package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp3.BrownianMovement;
import ar.edu.itba.ss.g6.tp.tp3.BrownianMovementTimeDrivenSimulation;
import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp3.ParticleGenerator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D.COLOR.BLACK;
import static java.util.stream.Collectors.joining;

public class TP3 {

    public static void main(String ...args) {


        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }


        double duration = values.getDuration();
        ParticleGenerator generator = new ParticleGenerator();
        Set<WeightedDynamicParticle2D> particles = generator.getParticles(5);
        System.out.println("Using particles: " + particles.stream().map(p -> p.getId()).collect(joining(", ")));

        BrownianMovement simulation = new BrownianMovement(values.getDuration(), particles);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        System.out.println("Starting time-view of simulation");
        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;
        int i = 0;
        List<Set<ColoredWeightedDynamicParticle2D>> frames = new LinkedList<>();
        while ((simulationFrame = timed.getNextStep()) != null && simulationFrame.getTimestamp() < duration) {
            Set<ColoredWeightedDynamicParticle2D> state = simulationFrame.getState();
            ColoredWeightedDynamicParticle2D origin = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 2), 0 ,0, 0, 0, 0.001, 0, BLACK);
            ColoredWeightedDynamicParticle2D distal = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 1), 0.5 ,0.5, 0, 0, 0.001, 0, BLACK);
            state.add(origin);
            state.add(distal);
            frames.add(state);
            System.out.printf("Frame %d\n", ++i);
            System.out.println(state.stream().map(p -> p.toString()).collect(joining("\n")));
        }
        Exporter<ColoredWeightedDynamicParticle2D> ovitoExporter = new OvitoXYZExporter<>();
        try {
            ovitoExporter.saveAnimationToFile(values.getOutFile().toPath(), frames, 1.0 / 30);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
