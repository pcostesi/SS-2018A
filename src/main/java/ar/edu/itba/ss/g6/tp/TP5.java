package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class TP5 {
    static CommandLineOptions values;

    public static void main(String ...args) {

        values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }

        if (values.isMsd()) {
            //msdMode(values.getDuration(), values.getL());
            System.exit(0);
        }

        if (values.isGenerate()) {
            //generatorMode(values.getN(), values.getOutFile(), values.getL(), values.getSpeed(), values.getWeight(), values.getRadius());
            System.exit(0);
        }
    }

    private void granularSimulation() {

    }
}
