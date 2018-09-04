package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.loader.StaticDataLoader;
import ar.edu.itba.ss.g6.loader.StaticLoaderResult;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp3.BrownianMovement;
import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp3.ConfigTp3;
import ar.edu.itba.ss.g6.tp.tp3.ParticleGenerator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D.COLOR.BLACK;

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

        BrownianMovement simulation = new BrownianMovement(duration, particles, 0.5);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        System.out.println("Starting time-view of simulation");
        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;

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
         "world-1.xyz","world-2.xyz","world-3.xyz",
         "world-4.xyz","world-5.xyz","world-6.xyz",
         "world-7.xyz","world-8.xyz","world-9.xyz",

         "world-10.xyz","world-11.xyz","world-12.xyz",
         "world-13.xyz","world-14.xyz","world-15.xyz",
         "world-16.xyz","world-17.xyz","world-18.xyz",

         "world-19.xyz","world-20.xyz",
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

        DoubleSummaryStatistics avgMsdsBigParticleByT[] = IntStream.range(0, msdBigParticle[0].size())
            .mapToObj(elemIdx -> Arrays.stream(msdBigParticle)
                .mapToDouble(l -> l.get(elemIdx).doubleValue())
                .summaryStatistics())
             .toArray(a -> new DoubleSummaryStatistics[a]);

        DoubleSummaryStatistics avgMsdsSomeParticleByT[] = IntStream.range(0, msdSomeParticle[0].size())
            .mapToObj(elemIdx -> Arrays.stream(msdSomeParticle)
                .mapToDouble(l -> l.get(elemIdx).doubleValue())
             .summaryStatistics())
         .toArray(a -> new DoubleSummaryStatistics[a]);


        double diffusionMinForBigParticleOverTime[] = IntStream.range(0, avgMsdsBigParticleByT.length)
         .mapToDouble(idx -> avgMsdsBigParticleByT[idx].getMin() / (idx / 30))
         .toArray();

        double diffusionMinForSomeParticleOverTime[] = IntStream.range(0, avgMsdsSomeParticleByT.length)
         .mapToDouble(idx -> avgMsdsSomeParticleByT[idx].getMin() / (idx / 30))
         .toArray();

        double diffusionMaxForBigParticleOverTime[] = IntStream.range(0, avgMsdsBigParticleByT.length)
         .mapToDouble(idx -> avgMsdsBigParticleByT[idx].getMax() / (idx / 30))
         .toArray();

        double diffusionMaxForSomeParticleOverTime[] = IntStream.range(0, avgMsdsSomeParticleByT.length)
         .mapToDouble(idx -> avgMsdsSomeParticleByT[idx].getMax() / (idx / 30))
         .toArray();


        double diffusionMeanForBigParticleOverTime[] = IntStream.range(0, avgMsdsBigParticleByT.length)
         .mapToDouble(idx -> avgMsdsBigParticleByT[idx].getAverage() / (idx / 30))
         .toArray();

        double diffusionMeanForSomeParticleOverTime[] = IntStream.range(0, avgMsdsSomeParticleByT.length)
         .mapToDouble(idx -> avgMsdsSomeParticleByT[idx].getAverage() / (idx / 30))
         .toArray();



        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("msd-big.dat").normalize())) {
            System.out.println("MSD BIG");
            writerRaw.write("time,\tmin-msd,\tmean-msd,\tmax-msd,\tmin-d,\tmean-d,\tmax-d\n");
            for (int idx = 30; idx < avgMsdsBigParticleByT.length && idx < 30 * 60; idx += 30) {
                writerRaw.write(String.format("%e\t%e\t%e\t%e\t%e\t%e\t%e\n",
                 idx / (30.0),
                     avgMsdsBigParticleByT[idx].getMin(),
                     avgMsdsBigParticleByT[idx].getAverage(),
                     avgMsdsBigParticleByT[idx].getMax(),
                     diffusionMinForBigParticleOverTime[idx],
                 diffusionMeanForBigParticleOverTime[idx],
                 diffusionMaxForBigParticleOverTime[idx]
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("msd-small.dat").normalize())) {
            System.out.println("MSD SMALL");
            writerRaw.write("time,\tmin-msd,\tmean-msd,\tmax-msd,\tmin-d,\tmean-d,\tmax-d\n");
            for (int idx = 30; idx < avgMsdsSomeParticleByT.length && idx < 30 * 60; idx += 30) {
                writerRaw.write(String.format("%e\t%e\t%e\t%e\t%e\t%e\t%e\n",
                 idx / (30.0),
                 avgMsdsSomeParticleByT[idx].getMin(),
                 avgMsdsSomeParticleByT[idx].getAverage(),
                 avgMsdsSomeParticleByT[idx].getMax(),
                 diffusionMinForSomeParticleOverTime[idx],
                 diffusionMeanForSomeParticleOverTime[idx],
                 diffusionMaxForSomeParticleOverTime[idx]
                ));            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void step1() {
        List<Double> collisions = new LinkedList<>();

            try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("collisions.dat").normalize())) {
                System.out.println("Raw collisions");
                for (int world = 0; world < 20; world++) {
                    ParticleGenerator generator = new ParticleGenerator(0.5, 0.1, 0.1, 0.005);
                    Set<WeightedDynamicParticle2D> particles = generator.getParticles(40);
                    BrownianMovement simulation2 = new BrownianMovement(600.0, particles, 0.5);

                    SimulationFrame<WeightedDynamicParticle2D> frame;
                    double prevTime = 0;
                    while ((frame = simulation2.getNextStep()) != null) {
                        double thisTime = frame.getTimestamp();
                        double delta = thisTime - prevTime;
                        collisions.add(delta);
                        prevTime = thisTime;
                    }
                }
                double[] thelist = collisions.stream().mapToDouble(s -> s).toArray();
                for (int idx = 0; idx < thelist.length; idx++) {
                    writerRaw.write(String.format("%f\n", thelist[idx]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(1);
    }

    public static void simulatiorMode(double duration, File inputFile, File outputFile, double worldSize) {
        Set<WeightedDynamicParticle2D> particles;
        StaticLoaderResult<WeightedDynamicParticle2D> result;
        result = StaticDataLoader.importFromFile(inputFile.toPath(), WeightedDynamicParticle2D::fromValues);
        particles = result.getParticles();

        BrownianMovement simulation = new BrownianMovement(duration, particles, 0.5);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        System.out.println("Starting time-view of simulation");
        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;

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
        double speeds[] = frames.parallelStream().skip(frames.size() - keyframe)
            .flatMapToDouble(frameParticles -> frameParticles.parallelStream()
             .mapToDouble(particle -> particle.getSpeed()))
            .filter(d -> d != 0)
            .sorted()
            .toArray();

        double speeds1[] = frames.get(0).parallelStream()
            .mapToDouble(particle -> particle.getSpeed())
         .filter(d -> d != 0)
         .sorted()
            .toArray();

        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("pdf-thirds.dat").normalize())) {
            System.out.println("Raw thirds");
            for (int idx = 0; idx < speeds.length; idx++) {
                writerRaw.write(String.format("%f\n", speeds[idx]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("pdf-1.dat").normalize())) {
            System.out.println("Raw first");
            for (int idx = 0; idx < speeds1.length; idx++) {
                writerRaw.write(String.format("%f\n", speeds1[idx]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Exporter<ColoredWeightedDynamicParticle2D> ovitoExporter = new OvitoXYZExporter<>();
        try {
            ovitoExporter.saveAnimationToFile(outputFile.toPath(), frames, 1.0 / 30);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Average of particle momentum");
        System.out.println(frames.stream()
            .mapToDouble(frame -> frame.stream()
                .mapToDouble(p -> p.getWeight() * p.getSpeed())
                .sum()
            ).average().orElse(0)
        );
    }

    public static void main(String ...args) {

        ConfigTp3 config = ConfigTp3.loadConfig();

        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }

        if (values.isMsd()) {
            msdMode(config.getDuration(), config.getLength());
            System.exit(0);
        }

        if (values.isGenerate()) {
            generatorMode(config.getParticles(), values.getOutFile(), config.getLength(), config.getSpeed(), config.getWeight(), config.getRadius());
            System.exit(0);
        }

        if(values.isCi()){
            simulateAndGetCollisionsIntervals(config, values.getOutFile().getPath());
            System.exit(0);
        }

        if(values.isVel()){
            simulteAndGetSpeeds(config);
            System.exit(0);
        }

        simulatiorMode(config.getDuration(), values.getInFile(), values.getOutFile(), config.getLength());
    }

    static private List<SimulationFrame> getSimulationFromRand(ConfigTp3 config) {
        List<SimulationFrame> frames = new ArrayList<>();
        System.out.println("Simulating!");
        ParticleGenerator generator = new ParticleGenerator(config.getLength(), config.getSpeed(), config.getWeight(), config.getRadius());
        Set<WeightedDynamicParticle2D> particles = generator.getParticles(config.getParticles());
        BrownianMovement simulation = new BrownianMovement(config.getDuration(), particles, config.getLength());

        SimulationFrame<WeightedDynamicParticle2D> frame;
        while ((frame = simulation.getNextStep()) != null) {
            frames.add(frame);
        }
        System.out.println("Simulation finished!");
        Set<WeightedDynamicParticle2D> parts = frames.get(frames.size()-1).getState();
        return frames;
    }

    private static Double[] getCollisionIntervals(List<SimulationFrame> frames){
        int length = frames.size();
        Double[] result = frames.stream().map(SimulationFrame::getTimestamp).toArray(Double[]::new);
        result[0] = frames.get(0).getTimestamp();
        for(int i = length-1; i >1; i--){
            if(result[i] - result[i-1] < 0){
                continue;
            }
            result[i] -= result[i-1];
        }
        return result;
    }

    public Double[] getCollisionTimestamps(List<SimulationFrame> frames){
        return frames.stream().map(SimulationFrame::getTimestamp).toArray(Double[]::new);
    }

    private static void writeCollisionIntervals(String outputPath, Double[] intervals){
        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get(outputPath).normalize())) {
            for (int idx = 0; idx < intervals.length; idx++) {
                writerRaw.write(String.format("%f\n", intervals[idx]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void simulateAndGetCollisionsIntervals(ConfigTp3 config, String outputPath){
        List<SimulationFrame> frames = getSimulationFromRand(config);
        for (int i = 0; i < 4 ; i++) {
            frames.addAll(getSimulationFromRand(config));
        }
        Double[] collisions = getCollisionIntervals(frames);
        writeCollisionIntervals(outputPath, collisions);
    }

    @SuppressWarnings("Duplicates")
    public static void simulteAndGetSpeeds(ConfigTp3 config){
        List<SimulationFrame> frames = getSimulationFromRand(config);
        List<Set<WeightedDynamicParticle2D>> states = new LinkedList<>();
        for (SimulationFrame frame:frames) {
            states.add(frame.getState());
        }
        int keyframe = 2 * states.size() / 3;
        double speeds[] = states.parallelStream().skip(states.size() - keyframe)
                .flatMapToDouble(frameParticles -> frameParticles.parallelStream()
                        .mapToDouble(particle -> particle.getSpeed()))
                .filter(d -> d != 0)
                .sorted()
                .toArray();

        double initialSpeeds[] = states.get(0).parallelStream()
                .mapToDouble(particle -> particle.getSpeed())
                .filter(d -> d != 0)
                .sorted()
                .toArray();

        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("velocitythirds50.dat").normalize())) {
            System.out.println("Raw thirds");
            for (int idx = 0; idx < speeds.length; idx++) {
                writerRaw.write(String.format("%f\n", speeds[idx]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("velocityfirst50.dat").normalize())) {
            System.out.println("Raw first");
            for (int idx = 0; idx < initialSpeeds.length; idx++) {
                writerRaw.write(String.format("%f\n", initialSpeeds[idx]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
