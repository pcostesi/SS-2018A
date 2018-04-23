package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp4.ArmonicSimulation;
import ar.edu.itba.ss.g6.tp.tp4.ArmonicSimulationFrame;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TP4 {
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
            msdMode(values.getDuration(), values.getL());
            System.exit(0);
        }

        if (values.isGenerate()) {
            generatorMode(values.getN(), values.getOutFile(), values.getL(), values.getSpeed(), values.getWeight(), values.getRadius());
            System.exit(0);
        }

        armonicSimulationMode(values.getOutFile(), values.getTimeStep());

    }

    private static void msdMode(double duration, double l) {
    }

    private static void armonicSimulationMode(File outFile, double step) {
        WeightedDynamicParticle2D particle =
                new WeightedDynamicParticle2D("1", 1, 0, 0, 0, 0,70000);

        ArmonicSimulation beemanSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.BEEMAN);
        ArmonicSimulation gpoc5Sim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.GPCO5 );
        ArmonicSimulation verletSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.VERLET);
        ArmonicSimulation analyticSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.ANALYTIC);

        ArmonicSimulationFrame beemanCurrent = beemanSim.getNextStep();
        ArmonicSimulationFrame gpoc5Current = gpoc5Sim.getNextStep();
        ArmonicSimulationFrame verletCurrent = verletSim.getNextStep();
        ArmonicSimulationFrame analyticCurrent = analyticSim.getNextStep();
        try {
        BufferedWriter writerRaw = new BufferedWriter(new FileWriter(values.getOutFile()));
        List<String> out = new ArrayList<String>((int)(100)+1);
        while( beemanCurrent != null ) {
            out.add(String.valueOf(beemanCurrent.getTimestamp()));
            out.add(String.valueOf('\t'));
            beemanCurrent.getState().forEach( p -> {
                out.add(String.valueOf(p.getXCoordinate()));
            });
            out.add(String.valueOf('\t'));
           gpoc5Current.getState().forEach( p -> {
               out.add(String.valueOf(p.getXCoordinate()));
            });
            out.add(String.valueOf('\t'));
            verletCurrent.getState().forEach( p -> {
                out.add(String.valueOf(p.getXCoordinate()));
            });
            out.add(String.valueOf('\t'));
            analyticCurrent.getState().forEach( p -> {
                out.add(String.valueOf(p.getXCoordinate()));
            });
            out.add(String.valueOf('\n'));
            beemanCurrent = beemanSim.getNextStep();
            gpoc5Current = gpoc5Sim.getNextStep();
            verletCurrent = verletSim.getNextStep();
            analyticCurrent = analyticSim.getNextStep();
            writerRaw.write(out.stream().collect(Collectors.joining()));
            out.clear();
        }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generatorMode(int n, File outFile, double l, double speed, double weight, double radius) {
    }
}
