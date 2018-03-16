import cellIndexMethod.DynamicParticle;
import cellIndexMethod.Particle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OffLattice {
    private int m;
    private int l;
    private double eta;
    private double Rc;
    private int cycles;
    private int timeDelta;
    private Path inputPath;
    private Path outputPath;
    // Generator config
    private boolean randomGenerateParticles;
    private double speedModule;
    private int amount;
    private String type;
    private double maxRadius;

    public void initialize (){
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));

            JSONObject jsonObject = (JSONObject) obj;
            m = ((Long) jsonObject.get("m")).intValue();
            l = ((Long) jsonObject.get("l")).intValue();
            eta = (Double) jsonObject.get("eta");
            Rc = (Double) jsonObject.get("Rc");
            cycles = ((Long) jsonObject.get("cycles")).intValue();
            timeDelta = ((Double) jsonObject.get("timeDelta")).intValue();
            randomGenerateParticles = (Boolean) jsonObject.get("randomGenerateParticles");
            inputPath = Paths.get((String) jsonObject.get("inputPath"));
            outputPath = Paths.get((String) jsonObject.get("outputPath"));
            speedModule = (Double) jsonObject.get("speedModule");
            amount = ((Long) jsonObject.get("amount")).intValue();
            type = (String) jsonObject.get("type");
            maxRadius = (Double) jsonObject.get("maxRadius");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Murder runSimulation(List<Particle> particles) {
        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            Murder murderOfCrows = new Murder(particles, amount, l, m, Rc, eta, timeDelta);
            for (int i = 0; i < cycles; i++) {
                writer.write(toXYZFrame(murderOfCrows.getCrows(), i).collect(Collectors.joining("\n")));
                writer.write('\n');
                murderOfCrows = murderOfCrows.step();
            }
            return murderOfCrows;
        } catch (IOException e) {
            System.err.println("Oops");
            e.printStackTrace();
        }
        return null;
    }

    private Stream<String> toXYZFrame(List<Particle> particles, int iteration) {
        Stream<String> header = Stream.of(Integer.toString(particles.size()), String.format("t%d", iteration));
        Stream<String> particleStream = particles.stream()
            .map(p -> String.format("%s\t%f\t%f\t%f\t%f", p.getId(), p.getxPosition(), p.getyPosition(), p.getxSpeed(), p.getySpeed()));
        return Stream.concat(header, particleStream);
    }

    private void logToStdout(List<List<Particle>> result) {
        for (List<Particle> list : result) {
            System.out.println(list.stream()
                .map(p -> Integer.toString(p.getId()))
                .collect(Collectors.joining(" ")));
        }
    }

    public static void main(String args[]) {
        OffLattice lattice = new OffLattice();
        lattice.initialize();

        List<Particle> sampleList = new ArrayList<>();
        sampleList.add(new DynamicParticle(1, 0, 5, 0, 1, 0));
        sampleList.add(new DynamicParticle(1, 1, 5, 1, 1, 1));
        sampleList.add(new DynamicParticle(1, 2, 5, 2, 1, 2));
        sampleList.add(new DynamicParticle(1, 3, 5, 3, 1, 3));
        sampleList.add(new DynamicParticle(1, 1, 5, 1, 1, 4));

        lattice.runSimulation(sampleList);
    }
}
