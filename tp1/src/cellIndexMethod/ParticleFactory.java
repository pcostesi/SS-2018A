package cellIndexMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleFactory {
    public enum particleType { staticParticle, dynamicParticle }

    private Random randomGenerator = new Random(System.currentTimeMillis());
    private int xLimit, yLimit, maxRadius, amount;

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

        List<Particle> particleList = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            double radius = randomGenerator.nextDouble()*maxRadius;
            double xPosition = randomGenerator.nextDouble()*xLimit;
            double yPosition = randomGenerator.nextDouble()*yLimit;
            particleList.add(new StaticParticle(radius, xPosition, yPosition, i));
        }
        return particleList;
    }
}
