package ar.edu.itba.ss.particle;

import static ar.edu.itba.ss.data.Data.*;

public class SocialModel {

	public static Pair[] checkWallRight(EscapingParticle p) {
		double eps = p.getX() - W + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(1, 0), new Pair(0, 1));
	}

	public static Pair[] checkWallLeft(EscapingParticle p) {
		double eps = p.getRadius() - p.getX();
		return getContactForce(p.getVelocity(), eps, new Pair(-1, 0), new Pair(0, -1));
	}

	public static Pair[] checkWallBottom(EscapingParticle p) {
		double eps = -(p.getY() - floorLevel) + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(0, -1), new Pair(1, 0));
	}

	public static Pair[] getContactForce(Pair relativeVelocity, double eps, Pair norm, Pair tang) {
		Pair n = norm.clone();
		n.applyFunction(cord -> (-Kn * eps * cord));
		double velTang = Pair.internalProd(relativeVelocity, tang);
		Pair t = new Pair(-Kt * eps * velTang * tang.x, -Kt * eps * velTang * tang.y);
		return new Pair[] {n, t};
	}

	public static Pair getDrivingForce(Double mass, Pair velocity, Pair normal) {
		Pair n = normal.clone();
		n.multiply(desiredVelocity);
		Pair f = Pair.less(n, velocity);
		f.multiply(mass / TAU);
		return f;
	}

	public static Pair getSocialForce(Pair normal, double e) {
		Pair n = normal.clone();
		n.multiply(A * Math.exp(-e/B));
		return n;
	}
}
