package ar.edu.itba.ss.g6.tp;

import ar.edu.itba.ss.g6.simulation.Simulation;
import ar.edu.itba.ss.g6.simulation.SimulationFrame;
import ar.edu.itba.ss.g6.simulation.TimeDrivenSimulation;
import ar.edu.itba.ss.g6.topology.particle.ColoredWeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.topology.particle.WeightedDynamicParticle2D;
import ar.edu.itba.ss.g6.tp.tp3.BrownianMovement;
import ar.edu.itba.ss.g6.tp.tp3.BrownianMovementTimeDrivenSimulation;
import ar.edu.itba.ss.g6.tp.tp3.CommandLineOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.Collections;
import java.util.Set;

public class TP3 {

    public static int main(String ...args) {


        CommandLineOptions values = new CommandLineOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.exit(1);
        }


        double duration = values.getDuration();
        Set<WeightedDynamicParticle2D> particles = Collections.emptySet();

        BrownianMovement simulation = new BrownianMovement(values.getDuration(), particles);

        TimeDrivenSimulation timed = simulation.toTimeDrivenSimulation();

        SimulationFrame<ColoredWeightedDynamicParticle2D> simulationFrame;
        while ((simulationFrame = timed.getNextStep()) != null && simulationFrame.getTimestamp() < duration) {
            Set<ColoredWeightedDynamicParticle2D> state = simulationFrame.getState();
        }
        return 0;
    }
}
