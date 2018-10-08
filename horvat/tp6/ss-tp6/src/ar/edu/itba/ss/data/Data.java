package ar.edu.itba.ss.data;

public class Data {
    public static int N = 150;
    //public static final double[] desiredVelocities = {0.8,1.45,2.1,2.75,3.4,4.05,4.7,5.35,6.0};
    public static final double[] desiredVelocities = {4.05};
    public static double desiredVelocity;
    public static String desiredMethod = "MiddleDoor";
    public static final double dt = 1e-4;
    public static final double dt2 = 1.0 / 60;

    public static double W = 20.0;
    public static double L = 20.0;
    public static double D = 1.2;
    public static double RAD_MIN = 0.5;
    public static double RAD_MAX = 0.58;
    public static final double Kn = 1.2e5, Kt = 2.4e5;
    public static final double A = 2000, B = 0.08;

    public static double floorLevel = 4.0;

    final public static double TAU = 0.5;
    public static int id_count = 1;
    public static final double mass = 50;

    public static final int times = 10;

}
