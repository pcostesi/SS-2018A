package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.loader.ParticleLoader;
import ar.edu.itba.ss.g6.loader.StaticDataLoader;
import ar.edu.itba.ss.g6.loader.StaticLoaderResult;
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

import javax.annotation.processing.SupportedSourceVersion;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D.COLOR.BLACK;
import static java.util.stream.Collectors.joining;

public class TP3 {

    public static void generatorMode(int numberOfParticles, File outFile, double worldSize, double maxSpeed, double weight, double radius) {
        System.out.println("Generating particles");

        ParticleGenerator generator = new ParticleGenerator(worldSize, maxSpeed, weight, radius);
        Set<WeightedDynamicParticle2D> particles = generator.getParticles(numberOfParticles);
        OvitoXYZExporter<WeightedDynamicParticle2D> exporter = new OvitoXYZExporter<>();
        try {
            exporter.saveFrameToFile(outFile.toPath(), particles, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved.");
    }

    public static List<Set<ColoredWeightedDynamicParticle2D>> loadMSDMode(String filename, double duration, double worldSize) {
        Set<WeightedDynamicParticle2D> particles;
        StaticLoaderResult<WeightedDynamicParticle2D> result;
        result = StaticDataLoader.importFromFile(Paths.get(filename), WeightedDynamicParticle2D::fromValues);
        particles = result.getParticles();

        BrownianMovement simulation = new BrownianMovement(duration, particles);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        System.out.println("Starting time-view of simulation");
        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;

        int i = 0;
        List<Set<ColoredWeightedDynamicParticle2D>> frames = new LinkedList<>();
        while ((simulationFrame = timed.getNextStep()) != null && simulationFrame.getTimestamp() < duration) {
            Set<ColoredWeightedDynamicParticle2D> state = simulationFrame.getState();
            ColoredWeightedDynamicParticle2D origin = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 2), 0 ,0, 0, 0, 0.0001, 0, BLACK);
            ColoredWeightedDynamicParticle2D distal = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 1), worldSize , worldSize,0, 0, 0.0001, 0, BLACK);
            state.add(origin);
            state.add(distal);
            frames.add(state);
        }
        return frames;
    }

    public static void msdMode(double duration, double worldSize) {
        String[] worlds = new String[] {
         "world-1.xyz","world-2.xyz","world-3.xyz","world-4.xyz","world-5.xyz",
         "world-6.xyz","world-7.xyz","world-8.xyz","world-9.xyz","world-10.xyz",
         "world-11.xyz","world-12.xyz","world-13.xyz","world-14.xyz","world-15.xyz",
         "world-16.xyz","world-17.xyz","world-18.xyz","world-19.xyz","world-20.xyz",
        };
        String big = "0";
        String some = "13";

        List<List<Set<ColoredWeightedDynamicParticle2D>>> simulations = Arrays.stream(worlds)
            .map(world -> loadMSDMode(world, duration, worldSize))
            .collect(Collectors.toList());

        List<Double>[] msdBigParticle = simulations.stream()
            .map(simulation -> computeDisplacementForId(simulation, big))
            .toArray(a -> new List[a]);
        List<Double> msdSomeParticle[] = simulations.stream()
            .map(simulation -> computeDisplacementForId(simulation, some))
            .toArray(a -> new List[a]);

        double avgMsdsBigParticleByT[] = IntStream.range(0, msdBigParticle[0].size())
            .mapToDouble(elemIdx -> Arrays.stream(msdBigParticle)
                .mapToDouble(l -> l.get(elemIdx).doubleValue())
                .average()
                .orElse(0))
            .toArray();

        double avgMsdsSomeParticleByT[] = IntStream.range(0, msdSomeParticle[0].size())
            .mapToDouble(elemIdx -> Arrays.stream(msdSomeParticle)
                .mapToDouble(l -> l.get(elemIdx).doubleValue())
                .average()
                .orElse(0))
            .toArray();

        double diffusionForBigParticleOverTime[] = IntStream.range(0, avgMsdsBigParticleByT.length)
            .mapToDouble(idx -> avgMsdsBigParticleByT[idx] / idx)
            .toArray();

        double diffusionForSomeParticleOverTime[] = IntStream.range(0, avgMsdsSomeParticleByT.length)
            .mapToDouble(idx -> avgMsdsSomeParticleByT[idx] / idx)
            .toArray();


        for (int idx = 30 * 10; idx < avgMsdsBigParticleByT.length; idx = idx + 30 * 10) {
            System.out.println("Time T: " + idx / (30.0));
            System.out.println("avg MSD for  BigParticle By T " + avgMsdsBigParticleByT[idx]);
            System.out.println("avg MSD for SomeParticle By T " + avgMsdsSomeParticleByT[idx]);
            System.out.println("diffusion for  BigParticle By T " + diffusionForBigParticleOverTime[idx]);
            System.out.println("diffusion for SomeParticle By T " + diffusionForSomeParticleOverTime[idx]);
        }
    }

    static <T extends WeightedDynamicParticle2D> List<Double> computeDisplacementForId(List<Set<T>> frames, String id) {
        Set<T> firstFrame = frames.get(0);
        T p1 = firstFrame.stream().filter(p -> id.equals(p.getId())).findFirst().get();
        double initialXCoordinate = p1.getXCoordinate();
        double initialYCoordinate = p1.getYCoordinate();


        List msd = frames.stream().map(frame -> {
            T pm = frame.stream().filter(p -> p.getId().equals(id)).findFirst().get();
            double currentXCoordinate = pm.getXCoordinate();
            double currentYCoordinate = pm.getYCoordinate();
            double distanceInX = currentXCoordinate - initialXCoordinate;
            double distanceInY = currentYCoordinate - initialYCoordinate;
            return distanceInX * distanceInX + distanceInY * distanceInY;
        }).collect(Collectors.toList());
        return msd;
    }

    public static void simulatiorMode(double duration, File inputFile, File outputFile, double worldSize) {
        Set<WeightedDynamicParticle2D> particles;
        StaticLoaderResult<WeightedDynamicParticle2D> result;
        result = StaticDataLoader.importFromFile(inputFile.toPath(), WeightedDynamicParticle2D::fromValues);
        particles = result.getParticles();

        BrownianMovement simulation = new BrownianMovement(duration, particles);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        System.out.println("Starting time-view of simulation");
        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;

        int i = 0;
        List<Set<ColoredWeightedDynamicParticle2D>> frames = new LinkedList<>();
        while ((simulationFrame = timed.getNextStep()) != null && simulationFrame.getTimestamp() < duration) {
            Set<ColoredWeightedDynamicParticle2D> state = simulationFrame.getState();
            ColoredWeightedDynamicParticle2D origin = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 2), 0 ,0, 0, 0, 0.0001, 0, BLACK);
            ColoredWeightedDynamicParticle2D distal = new ColoredWeightedDynamicParticle2D(String.valueOf(Integer.MAX_VALUE - 1), worldSize , worldSize,0, 0, 0.0001, 0, BLACK);
            state.add(origin);
            state.add(distal);
            frames.add(state);
        }

        int keyframe = 2 * frames.size() / 3;
        double eps = 0e-4;
        double speeds[] = frames.parallelStream().skip(frames.size() - keyframe)
            .flatMapToDouble(frameParticles -> frameParticles.parallelStream()
                .mapToDouble(particle -> particle.getSpeed()))
            .toArray();

        double distinctSpeeds[] = Arrays.stream(speeds).distinct().sorted().toArray();

        double pdf[] = Arrays.stream(distinctSpeeds)
            .map(speed -> Arrays.stream(speeds).filter(s -> s == speed).count())
            .map(count -> count / distinctSpeeds.length)
            .toArray();

        System.out.printf("speed\tpdf\n");
        for (int idx = 0; idx < distinctSpeeds.length; idx++) {
            System.out.printf("%e\t%e\n", distinctSpeeds[idx], pdf[idx]);
        }

        double speeds1[] = frames.get(0).parallelStream()
         .mapToDouble(particle -> particle.getSpeed())
            .toArray();
        double distinctSpeeds1[] = Arrays.stream(speeds1).distinct().sorted().toArray();
        double pdf1[] = Arrays.stream(distinctSpeeds1)
         .map(speed -> Arrays.stream(speeds1).filter(s -> s == speed).count())
         .map(count -> count / distinctSpeeds.length)
         .toArray();
        System.out.println("for initial state");
        System.out.printf("speed\tpdf\n");
        for (int idx = 0; idx < distinctSpeeds1.length; idx++) {
            System.out.printf("%e\t%e\n", distinctSpeeds1[idx], pdf1[idx]);
        }
        Exporter<ColoredWeightedDynamicParticle2D> ovitoExporter = new OvitoXYZExporter<>();
        try {
            ovitoExporter.saveAnimationToFile(outputFile.toPath(), frames, 1.0 / 30);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ...args) {

        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }


        if (values.isMsd()) {
            msdMode(values.getDuration(), values.getL());
            System.exit(0);
        }

        if (values.isGenerate()) {
            generatorMode(values.getN(), values.getOutFile(), values.getL(), values.getSpeed(), values.getWeight(), values.getRadius());
            System.exit(0);
        }

        simulatiorMode(values.getDuration(), values.getInFile(), values.getOutFile(), values.getL());

    }
}
