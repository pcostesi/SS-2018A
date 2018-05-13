package ar.edu.itba.ss.g6.topology.geometry;

import ar.edu.itba.ss.g6.topology.vector.V2d;

import java.util.ArrayList;
import java.util.List;

public class Vessel {


    private final double L;
    private final double W;
    private final double D;
    private final List<Wall> walls;
    private final V2d doorLeft, doorRight;
    private final static double EXT_FACTOR = -0.1; // We extend the walls because mexicans tend to go above and beyond

    // 0,0 Is down to the left
    public Vessel(final double L, final double W, final double D) {
        this.L = L;
        this.W = W;
        this.D = D;
        this.doorLeft = new V2d(W / 2 - D / 2, L);
        this.doorRight = new V2d(W / 2 + D / 2, L);
        this.walls = generateWalls();
    }

    private List<Wall> generateWalls() {
        final List<Wall> walls = new ArrayList<Wall>(5);

        /*

        ^ Y
        |
            Upper
        __________    _ L
        |        |
        |        | Right
        |        |
        |        |
        |        |
        |        |
        |        |
        |        |
        |___  ___| Bottom right  ___> X
        |        |   ____> -L * 1.1
        0
         */

        walls.add(new Wall(new V2d(0, L), new V2d(W, L))); // upper wall
        walls.add(new Wall(new V2d(0, L), new V2d(0, L * EXT_FACTOR))); // left wall
        walls.add(new Wall(new V2d(W, L), new V2d(W, L * EXT_FACTOR))); // right wall
        walls.add(new Wall(new V2d(0, 0), new V2d((W - D) / 2, 0))); // bottom left
        walls.add(new Wall(new V2d((W + D) / 2, 0), new V2d(W, 0))); // bottom right
        return walls;
    }

    public double getWidth() {
        return W;
    }

    public double getHeight() {
        return L;
    }

    public List<Wall> getWalls() {
        return walls;
    }
}
