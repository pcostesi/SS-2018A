package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import ar.edu.itba.ss.g6.tp.tp4.ArmonicSimulation;
import ar.edu.itba.ss.g6.tp.tp4.ArmonicSimulationFrame;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        armonicSimulationMode(values.getOutFile());

    }

    private static void msdMode(double duration, double l) {
    }

    private static void armonicSimulationMode(File outFile) {
        double step = 0.01;
        WeightedDynamicParticle2D particle =
                new WeightedDynamicParticle2D("1", 1, 0, 0, 0, 0,70000);
        StringBuilder builder = new StringBuilder();

        ArmonicSimulation beemanSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.BEEMAN);
        ArmonicSimulation gpoc5Sim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.GPCO5 );
        ArmonicSimulation verletSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.VERLET);
        ArmonicSimulation analyticSim = new ArmonicSimulation(step, ArmonicSimulation.IntegrationMethod.ANALYTIC);

        ArmonicSimulationFrame beemanCurrent = beemanSim.getNextStep();
        /*ArmonicSimulationFrame gpoc5Current = gpoc5Sim.getNextStep();*/
        ArmonicSimulationFrame verletCurrent = verletSim.getNextStep();
        ArmonicSimulationFrame analyticCurrent = analyticSim.getNextStep();

        while( beemanCurrent != null ) {
            builder.append(beemanCurrent.getTimestamp());
            builder.append('\t');
            beemanCurrent.getState().forEach( p -> {
                builder.append(p.getXCoordinate());
            });
            //builder.append('\t');
           /* gpoc5Current.getState().forEach( p -> {
                builder.append(p.getXCoordinate());
            });*/
            builder.append('\t');
            verletCurrent.getState().forEach( p -> {
                builder.append(p.getXCoordinate());
            });
            builder.append('\t');
            analyticCurrent.getState().forEach( p -> {
                builder.append(p.getXCoordinate());
            });

           builder.append('\n');
            beemanCurrent = beemanSim.getNextStep();
            //gpoc5Current = gpoc5Sim.getNextStep();
            verletCurrent = verletSim.getNextStep();
            analyticCurrent = analyticSim.getNextStep();
        }
        try{
            BufferedWriter writerRaw = Files.newBufferedWriter(Paths.get("armonicout.dat").normalize());
            writerRaw.write(builder.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generatorMode(int n, File outFile, double l, double speed, double weight, double radius) {
    }
}
