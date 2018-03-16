package cellIndexMethod;

public class DynamicParticle implements Particle {
    public static int xLimit;
    public static int yLimit;
    private double radius;
    private double xPosition;
    private double yPosition;
    private int id;
    private double xSpeed;
    private double ySpeed;

    public DynamicParticle(double radius, double xPosition, double xSpeed,  double yPosition, double ySpeed, int id) {
        this.radius = radius;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }


    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getxPosition() {
        return xPosition;
    }

    @Override
    public double getyPosition() {
        return yPosition;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Particle updatePosition(double timeDelta, double theta) {
        double newxPosition = xPosition + xSpeed * timeDelta;
        if(newxPosition >= xLimit) {
            newxPosition = newxPosition % xLimit;
         }
         if(newxPosition < 0) {
            newxPosition += xLimit;
         }
        double newyPosition = yPosition + ySpeed * timeDelta;
        if(newyPosition >= yLimit) {
            newyPosition = newyPosition % yLimit;
        }
        if(newyPosition < 0) {
            newyPosition += yLimit;
        }
        double newxSpeed = getXSpeedForTheta(theta);
        double newySpeed = getYSpeedForTheta(theta);
        return new DynamicParticle(radius, newxPosition, newxSpeed, newyPosition, newySpeed, id);
    }

    @Override
    public double getAngle() {
        return Math.atan2(ySpeed, xSpeed);
    }

    @Override
    public double getSpeed() {
        return Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
    }

    private double getXSpeedForTheta(double theta) {
        return getSpeed() * Math.cos(theta);
    }

    private double getYSpeedForTheta(double theta) {

        return getSpeed() * Math.sin(theta);
    }

    @Override
    public double getxSpeed() {
        return xSpeed;
    }

    @Override
    public double getySpeed() {
        return ySpeed;
    }
}
