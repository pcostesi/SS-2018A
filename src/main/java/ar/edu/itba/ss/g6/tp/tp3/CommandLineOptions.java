package ar.edu.itba.ss.g6.tp.tp3;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;

public class CommandLineOptions {
    private boolean errorFree;



    @Option(name = "-i", aliases = { "--in" }, required = false,
     usage = "input file for the particles")
    private File inFile;

    @Option(name = "-o", aliases = { "--out" }, required = true,
     usage = "output file for the particles")
    private File outFile;

    @Option(name = "-g", aliases = { "--generate" }, required = false,
     usage = "input file for the static particles")
    private boolean generate;

    @Option(name = "-9", aliases = { "--msd" }, required = false,
     usage = "input file for the static particles")
    private boolean msd;

    @Option(name = "-N", aliases = { "--particles" }, required = false,
     usage = "number of particles")
    private int N = 30;

    @Option(name = "-L", aliases = { "--size" }, required = false,
     usage = "size of the world")
    private double L = 0.5;

    @Option(name = "-r", aliases = { "--radius" }, required = false,
     usage = "particle radius")
    private double radius = 0.005;

    @Option(name = "-s", aliases = { "--speed" }, required = false,
     usage = "particle speed")
    private double speed = 0.1;

    @Option(name = "-w", aliases = { "--weight" }, required = false,
     usage = "weight for the particles")
    private double weight = 0.1;

    @Option(name = "-t", aliases = { "--time" }, required = false,
     usage = "weight for the particles")
    private double duration = 60 * 5;

    @Option(name = "-ts", aliases = { "--time-step" }, required = false,
            usage = "weight for the particles")
    private double timeStep = 0.1;


    public CommandLineOptions(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

            errorFree = true;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }


    public boolean isErrorFree() {
        return errorFree;
    }

    public File getInFile() {
        return inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public boolean isGenerate() {
        return generate;
    }

    public int getN() {
        return N;
    }

    public double getL() {
        return L;
    }

    public double getRadius() {
        return radius;
    }

    public double getSpeed() {
        return speed;
    }

    public double getWeight() {
        return weight;
    }

    public double getDuration() {
        return duration;
    }

    public boolean isMsd() {
        return msd;
    }

    public double getTimeStep() {
        return timeStep;
    }
}
