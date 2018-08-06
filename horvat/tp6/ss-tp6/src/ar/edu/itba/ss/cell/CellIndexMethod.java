package ar.edu.itba.ss.cell;

import ar.edu.itba.ss.data.Data;
import ar.edu.itba.ss.particle.Particle;
import ar.edu.itba.ss.particle.Pair;

import java.util.*;

public class CellIndexMethod <T extends Particle> {

	private Set<T>[][] matrix;
	private double cellLength;
	private List<T> particles;
	private double rc;
	private int m;
	private double l;

	public CellIndexMethod(List<T> particle, double l, double cellLength, double rc){
		this(particle, l, (int)Math.ceil(l/cellLength), rc);
	}
	
	private CellIndexMethod(List<T> particles, double l, int m, double rc) {
		this.l = l;
		this.m = m;
		this.cellLength = l / m;
		this.rc = rc;
		this.particles = particles;
		if (cellLength < rc + 2* Data.RAD_MAX) {
			throw new IllegalArgumentException();
		}
		fillMatrix(particles);
	}


	private void fillMatrix(List<T> particles) {
		matrix = new Set[m][m];
		// Fill matrix with empty set
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				matrix[i][j] = new HashSet<>();
			}
		}
		for (T particle : particles) {
			Pair p = particle.getPosition();
			try{
				// The (int) cast the double and remove the data after the point
				matrix[(int) (p.x / cellLength)][(int) (p.y / cellLength)].add(particle);
			}catch(Exception e){
				/*Should never get here,
					Got here when get a pair out of the matrix
				 */
				System.err.println(p.x+" "+p.y+"  id: "+particle.getId());
			}
		}
	}

	public Map<T, Set<T>> getNeighboursMap() {
		Map<T, Set<T>> neighbourhood = new HashMap<>();
		fillMatrix(particles);
		//Fills the map with empty sets
		for (T p : particles) {
			neighbourhood.put(p, new HashSet<>());
		}
		int matrixLength = matrix.length;
		//Loop matrix in x index
		for (int i = 0; i < matrixLength; i++) {
			//Loop matrix in y index
			for (int j = 0; j < matrixLength; j++) {
				//Get the cell i,j
				Set<T> particlesInCell = matrix[i][j];
				for (T p : particlesInCell) {
					int[] dx = { 0, 0, 1, 1, 1 };
					int[] dy = { 0, 1, 1, 0, -1 };
					/*
						0 1 1
						0 X 1
						0 0 1
						I'm in X, and check in 1 and X for neighbours
					 */
					for (int k = 0; k < 5; k++) {
						int xi = i + dx[k];
						int yj = j + dy[k];
						if (xi < matrixLength && yj >= 0 && yj < matrixLength) {
							for (T q : matrix[xi][yj]) {
								checkNeighbourhood(p, q, neighbourhood);
							}
						}
					}
				}
			}
		}
		return neighbourhood;
	}

	private void checkNeighbourhood(T p, T q, Map<T, Set<T>> neighbours) {
		//I'm not my neighbour
		if (p.equals(q)) {
			return;
		}

		//Check distance
		Pair pp = p.getPosition().clone();
		Pair qq = q.getPosition().clone();

		double dist2 = Pair.dist2(pp, qq);
		double r = rc + p.getRadius() + q.getRadius();

		if (dist2 < r * r) {
			neighbours.get(p).add(q);
			neighbours.get(q).add(p);
		}
	}

}
