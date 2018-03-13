package cellIndexMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleFactory {
    public enum particleType { staticParticle, dynamicParticle }

    private Random randomGenerator = new Random(System.currentTimeMillis());
    private int xLimit, yLimit, maxRadius, amount;
    private List<Particle> particleList = new ArrayList<>();

    public List<Particle> produceRoundParticles(int amount, particleType type, int xLimit, int yLimit, int maxRadius) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;
        this.maxRadius = maxRadius;
        this.amount = amount;

        switch(type) {
            case staticParticle: return produceBasicParticles();
        }
        return null;
    }

    private List<Particle> produceBasicParticles() {

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
