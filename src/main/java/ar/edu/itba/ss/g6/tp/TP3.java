package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class TP3 {

    public static int main(String ...args) {


        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }

        return 0;
    }
}
