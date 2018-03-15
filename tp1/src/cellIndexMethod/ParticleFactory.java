package cellIndexMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleFactory {
    private Random randomGenerator = new Random(System.currentTimeMillis());
    private int xLimit, yLimit, maxRadius, amount, speedLimit;
    private List<Particle> particleList = new ArrayList<>();

    public void setFactory(int amount, int xLimit, int yLimit, int maxRadius, int randSeed) {
        if(randSeed != 0) {
            randomGenerator = new Random(randSeed);
        }
        this.xLimit = xLimit;
        this.yLimit = yLimit;
        this.maxRadius = maxRadius;
        this.amount = amount;
    }

        private List<Particle> produceStaticParticles() {
        particleList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double radius = randomGenerator.nextDouble()*maxRadius;
            double xPosition = randomGenerator.nextDouble()*xLimit;
            double yPosition = randomGenerator.nextDouble()*yLimit;
            Particle part = new StaticParticle(radius, xPosition, yPosition, i);
            if (isOk(part)) {
                particleList.add(part);
            }
            else {
                i--;
            }

        }
        return particleList;
    }

    private List<Particle> produceDynamicParticles(double speedModule) {
        particleList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double radius = randomGenerator.nextDouble()*maxRadius;
            double xPosition = randomGenerator.nextDouble()*xLimit;
            double yPosition = randomGenerator.nextDouble()*yLimit;
            double xSpeed = randomGenerator.nextDouble()*speedModule;
            double ySpeed = Math.sqrt(speedModule*speedModule) - (xSpeed*xSpeed);
            Particle part = new DynamicParticle(radius, xPosition, xSpeed, yPosition, ySpeed,  i);
            if (isOk(part)) {
                particleList.add(part);
            }
            else {
                i--;
            }

        }
        return particleList;
    }

    private boolean isOk(Particle newPaRT) {
        double xDistance = 0;
        double yDistance = 0;
        for(Particle p: particleList) {
            xDistance = newPaRT.getxPosition() - p.getxPosition();
            yDistance = newPaRT.getyPosition() - p.getyPosition();
            double totalDistance =  Math.sqrt((xDistance * xDistance + yDistance * yDistance))
                    - newPaRT.getRadius() - p.getRadius();
            if(totalDistance < 0) {
                return false;
            }
        }
        return true;
    }
}
