import cellIndexMethod.CellIndexMethod;
import cellIndexMethod.Particle;
import cellIndexMethod.StaticParticle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void runTp(String [] args)
    {
        String staticInputFileName = args[0];
        String dynamicInputFileName = args[1];
        M = Integer.parseInt(args[2]);
        R = Integer.parseInt(args[3]);

        parseStaticFile(staticInputFileName);
        parseDynamicFile(dynamicInputFileName);

        List<Particle> particleList = createParticles();
        CellIndexMethod method = new CellIndexMethod();
        List<List<Particle>> result = method.magic(particleList, N, L, M, R);
        int index = 0;
        for(List<Particle> list: result){
            System.out.print("Id: " + index + " x: " + xPositions[index] + " y: " + yPositions[index] + " - ");
            list.forEach( item -> {
                System.out.print("Neighbor: " + item.getId() + " ");
            });
            System.out.println();
            index++;
        }

        result = method.bruteForce(particleList, N, L, M, R);
        index = 0;
        for(List<Particle> list: result){
            System.out.print("Id: " + index + " x: " + xPositions[index] + " y: " + yPositions[index] + " - ");
            list.forEach( item -> {
                System.out.print("Neighbor: " + item.getId() + " ");
            });
            System.out.println();
            index++;
        }

    }

    private void parseDynamicFile(String fileName){

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
            int i = 0 , j = 0;
            while((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                String[] numbers = line.split(" +");
                xPositions[i++] = Double.parseDouble(numbers[0]);
                yPositions[j++] = Double.parseDouble(numbers[1]);
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
    }

    private void parseStaticFile(String fileName){

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
            int i = 0 , j = 0;
            while((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                String[] numbers = line.split(" +");
                radiouses[i++] = Double.parseDouble(numbers[0]);
                properties[j++] = Double.parseDouble(numbers[1]);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
    }

    private List<Particle> createParticles(){
        List<Particle> particleList = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            particleList.add(new StaticParticle(radiouses[i], xPositions[i], yPositions[i], i));
        }
        return particleList;
    }
}

