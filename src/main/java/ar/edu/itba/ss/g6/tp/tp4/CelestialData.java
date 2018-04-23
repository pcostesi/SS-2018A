package ar.edu.itba.ss.g6.tp.tp4;

public class CelestialData {

    Ephemeris[] planets = new Ephemeris[0];
    VoyagerData voyager1;
    double deltaT;
    int days;

    public Ephemeris[] getPlanets() {
        return planets;
    }

    public CelestialData setPlanets(Ephemeris[] planets) {
        this.planets = planets;
        return this;
    }

    public VoyagerData getVoyager1() {
        return voyager1;
    }

    public CelestialData setVoyager1(VoyagerData voyager1) {
        this.voyager1 = voyager1;
        return this;
    }

    public double getDeltaT() {
        return deltaT;
    }

    public CelestialData setDeltaT(double deltaT) {
        this.deltaT = deltaT;
        return this;
    }

    public int getDays() {
        return days;
    }

    public CelestialData setDays(int days) {
        this.days = days;
        return this;
    }
}
