package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.Particle2D;
import ar.edu.itba.ss.g6.topology.particle.TheParticle;


public class MapGridV2d<T extends TheParticle> extends MapGrid<T, Cell2D> {
    public MapGridV2d(long side, int buckets, double radius, boolean isPeriodic) {
        super(side, buckets, radius, isPeriodic, Cell2D::new);
    }


    public boolean isWithinRadius(double radius, Particle2D p1, Particle2D p2) {
        double distanceInX = p1.getXCoordinate() - p2.getXCoordinate();
        double distanceInY = p1.getYCoordinate() - p2.getYCoordinate();
        double sumOfRadius = p1.getRadius() + p2.getRadius();
        double rawDistance = Math.sqrt(distanceInX * distanceInX + distanceInY * distanceInY);

        return rawDistance - sumOfRadius <= radius;
    }


    @Override
    boolean areWithinDistance(T p1, T p2, double radius) {
        boolean withinDistance = isWithinRadius(radius, p1, p2);
        boolean withinPeriodicDistance = false;
        if (isPeriodic()) {
            long maxDist = this.getSideLength();
            double thisX = p1.getXCoordinate();
            double thisY = p1.getYCoordinate();
            double otherX = p2.getXCoordinate();
            double otherY = p2.getYCoordinate();


            if( thisX - radius < 0 && otherX + radius > maxDist) {
                thisX += maxDist;
            }
            else if(thisX + radius > maxDist && otherX - radius < 0) {
                otherX += maxDist;
            }
            if(thisY - radius < 0 && otherY + radius > maxDist) {
                thisY += maxDist;
            }
            else if(thisY + radius > maxDist && otherY - radius < 0) {
                otherY += maxDist;
            }

            double distanceInX = thisX - otherX;
            double distanceInY = thisY - otherY;
            double sumOfRadius = p1.getRadius() + p2.getRadius();
            double rawDistance = Math.sqrt(distanceInX * distanceInX + distanceInY * distanceInY);

            withinPeriodicDistance = rawDistance - sumOfRadius <= radius;
        }
        return withinDistance || withinPeriodicDistance;
    }
}
