package cellIndexMethod;

public interface Particle {

    public double getRadius();

    public double getxPosition();

    public double getyPosition();

    public int getId();

    public Particle updatePosition(double timeDelta, double theta);

    public double getAngle();

    public double getSpeed();

    public double getxSpeed();

    public double getySpeed();
}