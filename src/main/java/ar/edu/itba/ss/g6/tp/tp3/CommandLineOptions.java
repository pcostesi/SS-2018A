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

    @Option(name = "-c", aliases = { "--collision-interval" }, required = false,
            usage = "Outputs collision intervals to files")
    private boolean ci;

    @Option(name = "-s", aliases = { "--rand-seed" }, required = false,
            usage = "sets seed for random")
    private long seed = System.currentTimeMillis();

    @Option(name = "-v", aliases = { "--velocity" }, required = false,
            usage = "Outputs initial and last third velocities")
    private boolean vel = false;

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

    public boolean isMsd() {
        return msd;
    }

    public boolean isCi() {
        return ci;
    }

    public void setCi(boolean ci) {
        this.ci = ci;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean isVel() {
        return vel;
    }

    public void setVel(boolean vel) {
        this.vel = vel;
    }
}
