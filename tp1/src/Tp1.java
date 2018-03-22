import cellIndexMethod.CellIndexMethod;
import cellIndexMethod.DynamicParticle;
import cellIndexMethod.Particle;
import cellIndexMethod.StaticParticle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Tp1 {

    private int N;
    private int L;
    private int M;
    private double Rc;
    private double T0;
    private double[] radiouses;
    private double[] properties;
    private double[] xPositions;
    private double[] yPositions;
    private boolean periodicCondition;

    // File locations
    private Path outputPath;
    private Path dynamicPath;
    private Path staticPath;
    // Generator config
    private boolean randomGenerateParticles;
    private double maxRadius;

    public void runTp(String[] args) {

        this.initialize();

        if(!randomGenerateParticles){
            parseStaticFile();
            parseDynamicFile();
        } else {
            // TODO Generate parts with generator
        }

        List<Particle> particleList = createParticles();
        CellIndexMethod method = new CellIndexMethod();
        List<List<Particle>> result;


        try {
            if(periodicCondition) {
                result = method.getPeriodicNeighbors(particleList, N, L, M, Rc);
            } else {
                result = method.getNonPeriodicNeighbors(particleList, N, L, M, Rc);
            }

            logToStdout(result);
            streamPoints(result).forEachOrdered(line -> System.out.println(line));
            writeToFile(Paths.get("boundless.data"), result);

            result = method.bruteForce(particleList, N, L, M, Rc);
            streamPoints(result).forEachOrdered(line -> System.out.println(line));
            logToStdout(result);
            writeToFile(Paths.get("boundless-brute-force.data"), result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(Path path, List<List<Particle>> particles) throws IOException {
        Iterable<String> lines = streamPoints(particles)::iterator;
        Files.write(path, lines);
    }

    private Stream<String> streamPoints(List<List<Particle>> particles) {
        return IntStream.range(0, particles.size())
                .mapToObj(idx -> String.format("%d %s", idx, particles.get(idx).stream()
                        .map(p -> Integer.toString(p.getId()))
                        .collect(Collectors.joining(" "))));
    }

    private void logToStdout(List<List<Particle>> result) {
        int index = 0;
        for (List<Particle> list : result) {
            System.out.print("Id: " + index + " x: " + xPositions[index] + " y: " + yPositions[index] + " - ");
            list.forEach(item -> {
                System.out.print("Neighbor: " + item.getId() + " ");
            });
            System.out.println();
            index++;
        }
    }

    private void parseDynamicFile() {

        String line;
        try {
            FileReader fileReader = new FileReader(dynamicPath.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            T0 = Integer.parseInt(line.trim());
            xPositions = new double[N];
            yPositions = new double[N];
            int i = 0, j = 0;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                String[] numbers = line.split(" +");
                xPositions[i++] = Double.parseDouble(numbers[0]);
                yPositions[j++] = Double.parseDouble(numbers[1]);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            dynamicPath + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + dynamicPath + "'");
        }
    }

    private void parseStaticFile() {

        String line;

        try {
            FileReader fileReader = new FileReader(staticPath.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            N = Integer.parseInt(line.trim());
            line = bufferedReader.readLine();
            L = Integer.parseInt(line.trim());
            radiouses = new double[N];
            properties = new double[N];
            int i = 0, j = 0;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                String[] numbers = line.split(" +");
                radiouses[i++] = Double.parseDouble(numbers[0]);
                properties[j++] = Double.parseDouble(numbers[1]);
            }

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            staticPath + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + staticPath + "'");
        }
    }

    // TODO
    private List<Particle> createParticles() {
        List<Particle> particleList = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            particleList.add(new StaticParticle(radiouses[i], xPositions[i], yPositions[i], i));
        }
        return particleList;
    }


    public void initialize() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("tp1config.json"));

            JSONObject jsonObject = (JSONObject) obj;
            M = ((Long) jsonObject.get("m")).intValue();
            L = ((Long) jsonObject.get("l")).intValue();
            Rc = (Double) jsonObject.get("Rc");
            randomGenerateParticles = (Boolean) jsonObject.get("randomGenerateParticles");
            staticPath = Paths.get((String) jsonObject.get("staticPath"));
            dynamicPath = Paths.get((String) jsonObject.get("dynamicPath"));
            outputPath = Paths.get((String) jsonObject.get("outputPath"));
            N = ((Long) jsonObject.get("n")).intValue();
            maxRadius = (Double) jsonObject.get("maxRadius");
            periodicCondition = (Boolean) jsonObject.get("periodicCondition");
            DynamicParticle.xLimit = L;
            DynamicParticle.yLimit = L;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

