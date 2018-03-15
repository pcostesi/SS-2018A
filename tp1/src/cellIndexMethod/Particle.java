package cellIndexMethod;

public interface Particle {

    public double getRadius();

    public double getxPosition();

    public double getyPosition();

    public int getId();

    public Particle updatePosition(double timeDelta, double theta);
}