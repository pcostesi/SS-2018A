import cellIndexMethod.CellIndexMethod;
import cellIndexMethod.DynamicParticle;
import cellIndexMethod.Particle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A murder of crows
 */
public class Murder {
    private List<Particle> murderOfCrows;
    private int N;
    private int M;
    private int L;
    private double Rc;
    private double eta;
    private int T;

    public Murder (List<Particle> particles, int N, int L, int M, double Rc, double eta, int T) {
        this.murderOfCrows = particles;
        this.N = N;
        this.L = L;
        this.M = M;
        this.Rc = Rc;
        this.T = T;
        this.eta = eta;
    }

    public static Murder fromFile(Path path) {
        return null;
    }

    public double getOrder() {
        double speeds = murderOfCrows.stream().mapToDouble(p -> p.getSpeed()).sum();
        return speeds / N;
    }

    public double getDensity() {
        return N / (L * L);
    }

    private double getNoise() {
        Random random = new Random();
        double min = -1 * eta / 2;
        double max = eta / 2;
        double noise = min + random.nextDouble() * (max - min);
        return noise;
    }

    private static double getAngleFromNeighbors(List<Particle> neighbors) {
        return neighbors.stream().mapToDouble(p -> p.getId()).average().orElse(0);
    }

    public List<Particle> getCrows() {
        return this.murderOfCrows;
    }

    public Murder step() {
        CellIndexMethod cim = new CellIndexMethod();
        List<List<Particle>> cells = cim.getPeriodicNeighbors(this.murderOfCrows, N, L, M, Rc);
        List<Double> angles = cells.stream().map(Murder::getAngleFromNeighbors).collect(Collectors.toList());
        List<Particle> newMurder = IntStream.range(0, murderOfCrows.size())
            .mapToObj(i -> {
                Particle crow = murderOfCrows.get(i);
                double angle = angles.get(i) + getNoise();
                return crow.updatePosition(T, angle);
            })
            .collect(Collectors.toList());
        return new Murder(newMurder, N, L, M, Rc, eta, T);
    }
}
