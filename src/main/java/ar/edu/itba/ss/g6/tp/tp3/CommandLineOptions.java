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

}
