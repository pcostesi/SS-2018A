package cellIndexMethod;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {

    private ArrayList<List<Particle>> cells;
    private double blockLength;
    private int cellsPerRow;
    private int totalCells;
    private double Rc;
    private ArrayList<List<Particle>> closeOnes;

    private void initialize(List<Particle> particleList, int N, int L, int M, double Rc) {
        closeOnes = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            closeOnes.add(i,new ArrayList<>());
        }
        this.Rc = Rc;
        blockLength = L / M;
        cellsPerRow = (int) (L / blockLength);
        totalCells = cellsPerRow * cellsPerRow;
        cells = new ArrayList<>(totalCells);
        for (int i = 0; i < totalCells; i++) {
            cells.add(new ArrayList<>());
        }

        for (Particle particle : particleList) {
            locateParticle(particle);
        }
    }

    public List<List<Particle>> getPeriodicNeighbors(List<Particle> particleList, int N, int L, int M, double Rc) {

        initialize(particleList, N, L, M, Rc);
        for (int i = 0; i < totalCells; i++) {
            List<Particle> currentCell = cells.get(i);
            compareWithSelf(currentCell);
            compareWithTopCellPer(currentCell, i);
            compareWithTopRightCellPer(currentCell, i);
            compareWithRightPer(currentCell, i);
            compareWithBotRightPer(currentCell, i);
        }
        return closeOnes;
    }

    public List<List<Particle>> periodicNeighbors(List<Particle> particleList, int N, int L, int M, double Rc) {
        initialize(particleList, N, L, M, Rc);
        for (int i = 0; i < totalCells; i++) {
            List<Particle> currentCell = cells.get(i);
            compareWithSelf(currentCell);
            compareWithTopCell(currentCell, i);
            compareWithTopRightCell(currentCell, i);
            compareWithRight(currentCell, i);
            compareWithBotRight(currentCell, i);
        }
        return closeOnes;
    }


    private void locateParticle(Particle particle) {
        int cellNumber = (int)(particle.getxPosition()/blockLength)
                + (int)(particle.getyPosition()/blockLength)*cellsPerRow;
        cells.get(cellNumber).add(particle);
    }

    private void addIfInRange(Particle particleA, Particle particleB){
        double xDistance = 0;
        double yDistance = 0;
        xDistance = particleB.getxPosition() - particleA.getxPosition();
        yDistance = particleB.getyPosition() - particleA.getyPosition();
        double totalDistance =  Math.sqrt((xDistance * xDistance + yDistance * yDistance))
                - particleB.getRadius() - particleA.getRadius();
        if( totalDistance < Rc ){
            closeOnes.get(particleA.getId()).add(particleB);
            closeOnes.get(particleB.getId()).add(particleA);
        }
    }

    private void checkCells(List<Particle> currentCell, List<Particle> neighborCell) {
        currentCell.forEach( particle -> {
            for (int j = 0; j < neighborCell.size(); j++) {
                addIfInRange(particle, neighborCell.get(j));
            }
        });
    }

    public List<List<Particle>> bruteForce(List<Particle> particleList, int N, int L, int M, double Rc) {
        closeOnes = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            closeOnes.add(i,new ArrayList<>());
        }
        particleList.forEach( particleA -> {
            particleList.forEach( particleB -> {
                addIfInRangeBruteForceEdition(particleA, particleB);
            });
        });
        return closeOnes;
    }

    private void addIfInRangeBruteForceEdition(Particle particleA, Particle particleB){
        if(particleA.getId() == particleB.getId()) {
            return;
        }
        double xDistance = 0;
        double yDistance = 0;
        xDistance = particleB.getxPosition() - particleA.getxPosition();
        yDistance = particleB.getyPosition() - particleA.getyPosition();
        double totalDistance =  Math.sqrt((xDistance * xDistance + yDistance * yDistance))
                - particleB.getRadius() - particleA.getRadius();
        if( totalDistance < Rc ){
            closeOnes.get(particleA.getId()).add(particleB);
        }
    }

    private void compareWithSelf(List<Particle> currentCell) {
        for (int i = 0; i < currentCell.size(); i++) {
            for (int j = i+1; j < currentCell.size(); j++) {
                addIfInRange(currentCell.get(i), currentCell.get(j));
            }
        }
    }

    private void compareWithTopCellPer(List<Particle> currentCell, int i) {
        int neighborIndex = (i + cellsPerRow) % totalCells;
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }

    private void compareWithTopRightCellPer(List<Particle> currentCell, int i) {
        int neighborIndex = (i + cellsPerRow +1) % totalCells;
        if(neighborIndex == 0 && i != 0) {
            neighborIndex = (i + cellsPerRow +1);
        }
        if((i+1) % cellsPerRow == 0){
            neighborIndex -= cellsPerRow;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }

    private void compareWithRightPer(List<Particle> currentCell, int i) {
        int neighborIndex = i+1;
        if((i+1) % cellsPerRow == 0){
            neighborIndex -= cellsPerRow;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);

    }

    private void compareWithBotRightPer(List<Particle> currentCell, int i) {
        int neighborIndex = (i - cellsPerRow);
        //me fui para abajo?
        if(neighborIndex < 0) {
            neighborIndex += cellsPerRow * cellsPerRow;
        }
        neighborIndex++;
        //me fui para la derecha?
        if((i+1) % cellsPerRow == 0){
            neighborIndex -= cellsPerRow;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }

    private void compareWithTopCell(List<Particle> currentCell, int i) {
        int neighborIndex = (i + cellsPerRow);
        if(neighborIndex >= totalCells){
            return;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }

    private void compareWithTopRightCell(List<Particle> currentCell, int i) {
        int neighborIndex = (i + cellsPerRow);
        if( (neighborIndex >= totalCells) || (i+1) % cellsPerRow == 0){
            return;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }

    private void compareWithRight(List<Particle> currentCell, int i) {
        int neighborIndex = i+1;
        if((i+1) % cellsPerRow == 0){
            return;
        }
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);

    }

    private void compareWithBotRight(List<Particle> currentCell, int i) {
        int neighborIndex = (i - cellsPerRow);
        //me fui para abajo?
        if( (neighborIndex < 0) || ((i+1) % cellsPerRow == 0)) {
            return;
        }
        neighborIndex++;
        List<Particle> neighborCell = cells.get(neighborIndex);
        checkCells(currentCell, neighborCell);
    }
}
