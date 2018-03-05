package cellIndexMethod;

import java.util.List;

public interface Cell {
    public int getPosition();
    public List<Particle> getParticleList();

    public double getMinX();
    public double getMaxX();
    public double getMinY();
    public double getMaxY();
}
