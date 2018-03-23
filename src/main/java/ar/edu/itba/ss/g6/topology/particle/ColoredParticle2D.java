package ar.edu.itba.ss.g6.topology.particle;

public class ColoredParticle2D extends Particle2D {
    private String color;

    public String getColor() {
        return color;
    }

    public void colorize(String color) {
        this.color = color;
    }

    @Override
    public String[] values() {
        String[] originalValues = super.values();
        String[] newValues = new String[originalValues.length + 1];
        newValues[originalValues.length] = getColor();
        for (int idx = 0; idx < originalValues.length; idx++) {
            newValues[idx] = originalValues[idx];
        }
        return newValues;
    }

    public ColoredParticle2D(String id, double radius, double xCoord, double yCoord, String color) {
        super(id, radius, xCoord, yCoord);
        this.color = color;
    }
}
