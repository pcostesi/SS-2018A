package ar.edu.itba.ss.g6.topology.grid;

import ar.edu.itba.ss.g6.topology.particle.TheParticle;

import java.util.Collection;


public class MapGridV2d<T extends TheParticle> extends MapGrid<T, CellV2d> {
    public MapGridV2d(double side, int buckets, double radius, boolean isPeriodic) {
        super(side, buckets, radius, isPeriodic, CellV2d::new);
    }


    public boolean isWithinRadius(double radius, TheParticle p1, TheParticle p2) {
        return p1.distanceTo2(p2) <= radius * radius;
    }


    @Override
    boolean areWithinDistance(T p1, T p2, double radius) {
        boolean withinDistance = isWithinRadius(radius, p1, p2);
        boolean withinPeriodicDistance = false;
        if (isPeriodic()) {
            double maxDist = this.getSideLength();
            double thisX = p1.getPosition().getX();
            double thisY = p1.getPosition().getY();
            double otherX = p2.getPosition().getX();
            double otherY = p2.getPosition().getY();


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

    @Override
    public Collection<T> getWouldBeNeighbors(T particle) {
        return null;
    }
}
