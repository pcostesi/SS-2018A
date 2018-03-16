import cellIndexMethod.DynamicParticle;
import cellIndexMethod.Particle;
import cellIndexMethod.ParticleFactory;
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
            DynamicParticle.xLimit = l;
            DynamicParticle.yLimit = l;
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
        assert particles != null;
        Stream<String> header = Stream.of(Integer.toString(particles.size()), String.format("t%d", iteration));
        Stream<String> particleStream = particles.stream()
            .map(p -> String.format("%s\t%f\t%f\t%f\t%f", p.getId(), p.getxPosition(), p.getyPosition(), p.getxSpeed(), p.getySpeed()));
        return Stream.concat(header, particleStream);
    }

    public static void main(String args[]) {
        OffLattice lattice = new OffLattice();
        lattice.initialize();

        lattice.runSimulation(lattice.getParticles());
    }

    private List<Particle> getParticles() {
        if(randomGenerateParticles) {
            if(type.equalsIgnoreCase( "dynamic")) {
                ParticleFactory factory = new ParticleFactory();
                factory.setFactory(amount, l, l, maxRadius, (int) System.currentTimeMillis());
                return factory.produceDynamicParticles(speedModule);
            }
        } else {
            return null;
        }
        return null;
    }
}
