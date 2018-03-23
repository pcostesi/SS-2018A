package ar.edu.itba.ss.g6.topology.grid;

import java.util.stream.Stream;

public interface Cell {
    Stream<? extends Cell> semisphereNeighborhood();
}