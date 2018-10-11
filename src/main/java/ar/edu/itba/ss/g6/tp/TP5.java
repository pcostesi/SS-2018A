package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.exporter.ovito.Exporter;
import ar.edu.itba.ss.g6.exporter.ovito.OvitoXYZExporter;
import ar.edu.itba.ss.g6.loader.DynamicDataLoader;
import ar.edu.itba.ss.g6.loader.ParticleLoader;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
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
            System.err.println("Can't write 🤷🏻‍♂️");
            System.exit(1);
        }
    }

    private static void simulate(CommandLineOptions values) {
        TimeDrivenSimulation<TheParticle, GranularSimulationFrame> simulation = granularSimulation(values);
        GranularSimulationFrame frame;

        double stopTime = values.getDuration();
        int frameCount = simulation.totalFrameCount(stopTime);

        Path output = values.getOutFile();
        Exporter<TheParticle> exporter = new OvitoXYZExporter<>();

        Set<TheParticle> boundaries = Set.of(new TheParticle("-1", 0, values.getLenght() * -0.1, 0, 0, 0.001, 0),
         new TheParticle("-2", values.getWidth(), values.getLenght(), 0, 0, 0.001, 0));

        double currentFlow = 0;
        double[] totalKE = new double[frameCount];
        double[] flow = new double[frameCount];
        double stabilizedTimestamp = 0;

        try (BufferedWriter out = Files.newBufferedWriter(output, Charset.defaultCharset())) {
            while ((frame = simulation.getNextStep()) != null && frame.getTimestamp() <= stopTime) {
                double ts = frame.getTimestamp();
                currentFlow += frame.getFlowed();
                if (simulation.shouldCaptureFrame(ts)) {
                    int currentFrame = simulation.frameNumber(ts);
                    Set<TheParticle> particles = new HashSet<>();
                    particles.addAll(boundaries);
                    particles.addAll(frame.getState());
                    System.out.printf("%d - %f\n", currentFrame, frame.getTimestamp());
                    exporter.addFrameToFile(out, particles, frame.getTimestamp());

                    totalKE[currentFrame] = frame.getState().parallelStream()
                            .mapToDouble(TheParticle::getKineticEnergy)
                            .sum();
                    flow[currentFrame] = currentFlow;

                    if(values.getAperture() != 0) {
                        if(stabilizedTimestamp == 0 && currentFrame > 10) {
                            double mean = mean(flow, currentFrame);
                            double stDv = standard(flow, currentFrame, mean);
                            if( currentFlow <= (mean + stDv) && currentFlow >= (mean - stDv)){
                                stabilizedTimestamp = frame.getTimestamp();
                            }
                        }
                        if(stabilizedTimestamp != 0 && frame.getTimestamp() > stabilizedTimestamp + 4) {
                            break;
                        }
                    } else {
                        if(stabilizedTimestamp == 0 && currentFrame > 10) {
                            double mean = mean(totalKE, currentFrame);
                            double stDv = standard(totalKE, currentFrame, mean);
                            if( totalKE[currentFrame] <= (mean + stDv) && totalKE[currentFrame] >= (mean - stDv)){
                                stabilizedTimestamp = frame.getTimestamp();
                            }
                        }
                        if(stabilizedTimestamp != 0 && frame.getTimestamp() > stabilizedTimestamp + 4) {
                            break;
                        }
                    }
                    currentFlow = 0;
                }

            }
            System.out.println("Flow stabilized at:" + stabilizedTimestamp);
            System.out.println("Max Particle Height:" + simulation.getMaxHeight());
        } catch (IOException e) {
            System.err.println("Can't write sim 🤷🏻‍♂️");
            System.exit(4);
        }

        writeStatsToFile(values, flow, totalKE);
    }

    private static double mean(double[] m, int position) {
        double sum = 0;
        for (int i = position - 10; i <= position; i++) {
            sum += m[i];
        }
        return sum / position;
    }

    private static double standard(double[] numbers, int position, double average){
        double sd = 0;
        for (int i = position - 10; i <= position; i++)
        {
            sd += Math.pow(numbers[i] - average, 2) / 10;
        }
        return Math.sqrt(sd);
    }


    private static void writeStatsToFile(CommandLineOptions values, double flow[], double totalKE[]) {
        Path output = values.getStatsFile();
        try (BufferedWriter out = Files.newBufferedWriter(output, Charset.defaultCharset())) {
            for (int i = 0; i < Math.min(flow.length, totalKE.length); i++) {
                out.write(String.format("%d\t%e\t%e\n", i, flow[i], totalKE[i]));
            }
        } catch (IOException e) {
            System.err.println("Can't write stats 🤷🏻‍♂️");
            System.exit(8);
        }
    }

    private static TimeDrivenSimulation<TheParticle, GranularSimulationFrame> granularSimulation(CommandLineOptions values) {
        double width = values.getWidth();
        double height = values.getLenght();
        double aperture = values.getAperture();
        double deltaT = values.getTimeStep();
        Set<TheParticle> particles = loadParticles(values);
        double Mu = values.getMu();
        double Gamma = values.getGamma();
        double fps = values.getFps();

        TimeDrivenSimulation<TheParticle, GranularSimulationFrame> simulation = new GranularSimulation(Mu, Gamma, deltaT, width, height, aperture, particles, fps);
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
