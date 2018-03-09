import cellIndexMethod.CellIndexMethod;
import cellIndexMethod.Particle;
import cellIndexMethod.StaticParticle;

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
    private int R;
    private double T0;
    private double[] radiouses;
    private double[] properties;
    private double[] xPositions;
    private double[] yPositions;

    public void runTp(String[] args) {
        String staticInputFileName = args[0];
        String dynamicInputFileName = args[1];
        M = Integer.parseInt(args[2]);
        R = Integer.parseInt(args[3]);

        parseStaticFile(staticInputFileName);
        parseDynamicFile(dynamicInputFileName);

        List<Particle> particleList = createParticles();
        CellIndexMethod method = new CellIndexMethod();
        List<List<Particle>> result;

        try {
            result = method.getPeriodicNeighbors(particleList, N, L, M, R);
            logToStdout(result);
            streamPoints(result).forEachOrdered(line -> System.out.println(line));
            writeToFile(Paths.get("boundless.data"), result);

            result = method.bruteForce(particleList, N, L, M, R);
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

    private void parseDynamicFile(String fileName) {

        String line;

        try {
            FileReader fileReader =
             new FileReader(fileName);

            BufferedReader bufferedReader =
             new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            T0 = Integer.parseInt(line.trim());
            xPositions = new double[100];
            yPositions = new double[100];
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
              fileName + "'");
        } catch (IOException ex) {
            System.out.println(
             "Error reading file '"
              + fileName + "'");
        }
    }

    private void parseStaticFile(String fileName) {

        String line;

        try {
            FileReader fileReader =
             new FileReader(fileName);
            BufferedReader bufferedReader =
             new BufferedReader(fileReader);
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
              fileName + "'");
        } catch (IOException ex) {
            System.out.println(
             "Error reading file '"
              + fileName + "'");
        }
    }

    private List<Particle> createParticles() {
        List<Particle> particleList = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            particleList.add(new StaticParticle(radiouses[i], xPositions[i], yPositions[i], i));
        }
        return particleList;
    }
}

