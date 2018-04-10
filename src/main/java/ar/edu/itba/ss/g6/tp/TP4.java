package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;

public class TP4 {
    public static void main(String ...args) {

        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        System.out.println("copy pasta");

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

        simulationMode(values.getDuration(), values.getInFile(), values.getOutFile(), values.getL());

    }

    private static void msdMode(double duration, double l) {
    }

    private static void simulationMode(double duration, File inFile, File outFile, double l) {
    }

    private static void generatorMode(int n, File outFile, double l, double speed, double weight, double radius) {
    }
}
