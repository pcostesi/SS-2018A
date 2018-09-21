package ar.edu.itba.ss.g6.tp.tp4;

public class MinDistanceTrajectory {
    private final double[] bestDistance;
    private final int bestHeight;
    private final double bestSpeed;
    private final double angle;

    public MinDistanceTrajectory(double[] bestDistance, int bestHeight, double bestSpeed, double angle) {
        this.bestDistance = bestDistance;
        this.bestHeight = bestHeight;
        this.bestSpeed = bestSpeed;
        this.angle = angle;
    }

    public double[] getBestDistance() {
        return bestDistance;
    }

    public int getBestHeight() {
        return bestHeight;
    }

    public double getBestSpeed() {
        return bestSpeed;
    }

    public double getAngle() {
        return angle;
    }
}
