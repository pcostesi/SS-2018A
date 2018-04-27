package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class TP2 {
    static CommandLineOptions values;
    public static void main(String ...args) {
        values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);
        // N particulas random
        // v modulo = 0.3
        // tita random
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }
        if (values.isGenerate()) {
            //generatorMode(values.getN(), values.getOutFile(), values.getL(), values.getSpeed(), values.getWeight(), values.getRadius());
        }
    }
}
