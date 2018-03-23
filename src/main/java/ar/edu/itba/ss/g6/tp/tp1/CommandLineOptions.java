package ar.edu.itba.ss.g6.tp.tp1;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;

public class CommandLineOptions {

    public int getBuckets() {
        return buckets;
    }

    @Option(name = "-b", aliases = { "--buckets" }, required = true,
     usage = "number of buckets (must be a multiple of the side)")
    private int buckets;

    public String getHighlight() {
        return highlight;
    }

    @Option(name = "-x", aliases = { "--highligh" }, required = false,
     usage = "highlight a particle with id")
    private String highlight;

    @Option(name = "-s", aliases = { "--static" }, required = false,
     usage = "input file for the static particles")
    private File staticParticles;

    @Option(name = "-d", aliases = { "--dynamic" }, required = false,
     usage = "input file for the dynamic particles")
    private File dynamicParticles;

    @Option(name = "-o", aliases = { "--out" }, required = false,
     usage = "input file for the static particles")
    private File outFile;

    @Option(name = "-N", aliases = { "--particles" }, required = false,
     usage = "number of particles")
    private int N;

    @Option(name = "-L", aliases = { "--size" }, required = false,
     usage = "size of the world")
    private int L;

    @Option(name = "-r", aliases = { "--radius" }, required = false,
     usage = "particle radius")
    private double radius = 1;

    public boolean isPeriodic() {
        return periodic;
    }

    @Option(name = "-p", aliases = { "--periodic" }, required = false,
     usage = "periodic")
    private boolean periodic = true;

    public File getDynamicParticles() {
        return dynamicParticles;
    }

    public File getOutFile() {
        return outFile;
    }

    public int getN() {
        return N;
    }

    public int getL() {
        return L;
    }

    public double getRadius() {
        return radius;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    @Option(name = "-S", aliases = { "--search-radius" }, required = true,
     usage = "search radius")
    private int searchRadius;


    private boolean errorFree = false;

    public CommandLineOptions(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);

            if (getStaticParticles() == null
                && getDynamicParticles() == null
                && (getN() == 0 || getL() == 0 || getRadius() == 0)) {
                errorFree = false;
                System.err.println("Must provide at least a particle source");
            }
            errorFree = true;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    /**
     * Returns whether the parameters could be parsed without an
     * error.
     *
     * @return true if no error occurred.
     */
    public boolean isErrorFree() {
        return errorFree;
    }

    /**
     * Returns the source file.
     *
     * @return The source file.
     */
    public File getStaticParticles() {
        return staticParticles;
    }
}
