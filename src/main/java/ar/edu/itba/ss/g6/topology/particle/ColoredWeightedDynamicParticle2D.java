package ar.edu.itba.ss.g6.topology.particle;

public class ColoredWeightedDynamicParticle2D extends WeightedDynamicParticle2D {
    public enum COLOR {
        RED,
        GREEN,
        BLUE,
        BLACK,
        WHITE;

        public int getRedValue() {
            return (this.equals(RED) || this.equals(WHITE)) ? 255 : 0;
        }

        public int getGreenValue() {
            return (this.equals(GREEN) || this.equals(WHITE)) ? 255 : 0;
        }

        public int getBlueValue() {
            return (this.equals(BLUE) || this.equals(WHITE)) ? 255 : 0;
        }
    }

    public COLOR getColor() {
        return color;
    }

    private COLOR color;
    public ColoredWeightedDynamicParticle2D(String id, double x, double y, double vx, double vy, double radius, double weight, COLOR color) {
        super(id, x, y, vx, vy, radius, weight);
        this.color = color;
    }

    public ColoredWeightedDynamicParticle2D(WeightedDynamicParticle2D p, COLOR color) {
        super(p.getId(), p.getXCoordinate(), p.getYCoordinate(), p.getXSpeed(), p.getYSpeed(), p.getRadius(), p.getWeight());
        this.color = color;
    }

    @Override
    public String[] values() {
        return new String[] {
            this.getId(),
            String.valueOf(this.getXCoordinate()),
            String.valueOf(this.getYCoordinate()),
            String.valueOf(this.getXSpeed()),
            String.valueOf(this.getYSpeed()),
            String.valueOf(this.getRadius()),
            String.valueOf(this.getWeight()),
            String.valueOf(this.color.getRedValue()),
            String.valueOf(this.color.getGreenValue()),
            String.valueOf(this.color.getBlueValue())
        };
    }
}
