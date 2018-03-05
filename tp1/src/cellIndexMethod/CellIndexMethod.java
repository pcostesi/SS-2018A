package cellIndexMethod;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {

    private ArrayList<List<Particle>> cells;
    private double blockLength;
    private int cellsPerRow;

    public void magic(List<Particle> particleList, int N, int L, int M, double Rc) {

        long startTime = System.currentTimeMillis();
        blockLength = L/M;
        cellsPerRow = (int)(L/blockLength);
        int downIsUp = cellsPerRow*(cellsPerRow-1)+1;
        int rightIsLeft = (-cellsPerRow +1);
        int upperRightCornerIsUpperLeft = rightIsLeft+cellsPerRow;


        for(Particle particle : particleList) {
            locateParticle(particle);
        }

        int i;

        //First Row
        for (i = 0; i < blockLength -1 ; i++) {
            for( Particle currentCellParticle: cells.get(i) ){
                for( Particle adjacentCellParticle: cells.get(i+cellsPerRow) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
                for( Particle adjacentCellParticle: cells.get(i+cellsPerRow+1) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
                for( Particle adjacentCellParticle: cells.get(i+1) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
                for( Particle adjacentCellParticle: cells.get(i+downIsUp) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
            }
        }
        for( Particle currentCellParticle: cells.get(++i) ){
            for( Particle adjacentCellParticle: cells.get(i+cellsPerRow) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
            for( Particle adjacentCellParticle: cells.get(i+upperRightCornerIsUpperLeft) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
            for( Particle adjacentCellParticle: cells.get(i+rightIsLeft) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
            for( Particle adjacentCellParticle: cells.get(i+downIsUp+rightIsLeft) ){addIfInRange(currentCellParticle, adjacentCellParticle);}
        }

        //from row 1 to n-1
        //row n
        long duration =  System.currentTimeMillis() - startTime;
    }

    /* Nobody aint got time for that
    private ArrayList<Cell> getCells() {

        ArrayList<Cell> cellList = new ArrayList<>();
        for (int i = 0; i < cellsPerRow*cellsPerRow; i++) {
            double minX = i%cellsPerRow*blockLength;
            double minY = i/cellsPerRow*blockLength;
            Cell cell = new BasicCell(i, new ArrayList<Particle>(),
                    minX,  minX + blockLength,  minY,  minY + blockLength);
            cellList.add(i,cell);
        }
        return cellList;
    }
    */

    private void locateParticle(Particle particle) {
        int cellNumber = (int)(particle.getxPosition()/blockLength)
                + (int)(particle.getyPosition()/blockLength)*cellsPerRow;
        cells.get(cellNumber).add(particle);
    }

    private void addIfInRange(Particle particleA, Particle particleB){

    }

}
