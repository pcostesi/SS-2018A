package ar.edu.itba.ss.g6.tp.tp4;

public class VoyagerData {
    String id;
    double angle;
    double speed;
    double weight;

    public String getId() {
        return id;
    }

    public VoyagerData setId(String id) {
        this.id = id;
        return this;
    }

    public double getAngle() {
        return angle;
    }

    public VoyagerData setAngle(double angle) {
        this.angle = angle;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public VoyagerData setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public VoyagerData setWeight(double weight) {
        this.weight = weight;
        return this;
    }
}
