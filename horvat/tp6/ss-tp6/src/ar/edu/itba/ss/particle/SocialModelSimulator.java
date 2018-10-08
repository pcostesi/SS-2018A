package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;

import java.util.*;

import static ar.edu.itba.ss.data.Data.*;

public class SocialModelSimulator {

	private List<EscapingParticle> particles;
	private double dt;
	private CellIndexMethod<EscapingParticle> cellIndexMethod;
	private List<EscapingParticle> borderParticles;
	private LinkedList<EscapingParticle> toRemove;

	public SocialModelSimulator(List<EscapingParticle> particles, double dt) {
		this.particles = particles;
		this.dt = dt;
		estimateInitialLastPosition();
		borderParticles = new LinkedList<>();
		borderParticles.add(new EscapingParticle(0, (W - D) / 2.0, floorLevel, 0, 0, 0, 0));
		borderParticles.add(new EscapingParticle(0, (W + D) / 2.0, floorLevel, 0, 0, 0, 0));
		cellIndexMethod = new CellIndexMethod<>(particles, L + floorLevel, 2.2, 1);
		toRemove = new LinkedList<>();
	}

	private void estimateInitialLastPosition() {
		for (EscapingParticle p : particles) {
			p.updateLastPosition(p.getOwnForce(), dt);
		}
	}

	public void loop() {
		Map<EscapingParticle, Pair> forces = new HashMap<EscapingParticle, Pair>();
		Map<EscapingParticle, Set<EscapingParticle>> neighbours = cellIndexMethod.getNeighboursMap();
		for (EscapingParticle p : neighbours.keySet()) {
			p.resetPressure();
			Pair force = p.getOwnForce();
			for (EscapingParticle q : neighbours.get(p)) {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Pair.sum(force, wallForce(p));
			forces.put(p, force);
		}

		time += dt;
		for (EscapingParticle p : neighbours.keySet()) {
			Pair lastPosition = p.getLastPosition();
			updatePosition(p, forces.get(p), dt);
			updateVelocity(p, lastPosition, dt);
		}
		while(!toRemove.isEmpty()){
			EscapingParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
	}

	static double time = 0;

	private Pair wallForce(EscapingParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > floorLevel) {
			Pair[] force = SocialModel.checkWallLeft(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > W && p.position.y > floorLevel) {
			Pair[] force = SocialModel.checkWallRight(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (Math.abs(p.position.y - floorLevel) < p.getRadius()) {
			if (inDoor(p)) {
				for (EscapingParticle borderParticle : borderParticles) {
					Pair[] forceComponents = p.getForce(borderParticle);
					sum.add(Pair.sum(forceComponents[0], forceComponents[1]));
					p.addPressure(forceComponents[0]);
				}
			} else {
				Pair[] force = SocialModel.checkWallBottom(p);
				sum.add(Pair.sum(force[0], force[1]));
				p.addPressure(force[0]);
			}
		}
		return sum;
	}

	private boolean inDoor(EscapingParticle Particle) {
		double x = Particle.getX();
		double midW = W / 2;
		double midD = D / 2;
		return x >= midW - midD && x <= midW + midD;
	}

	private void updatePosition(EscapingParticle p, Pair force, double dt) {
		double rx = 2 * p.position.x - p.getLastPosition().x + force.x * Math.pow(dt, 2) / p.getMass();
		double ry = 2 * p.position.y - p.getLastPosition().y + force.y * Math.pow(dt, 2) / p.getMass();

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(EscapingParticle p, Pair lastPosition, double dt) {
		double vx = (p.position.x - lastPosition.x) / (2 * dt);
		double vy = (p.position.y - lastPosition.y) / (2 * dt);
		p.updateVelocity(vx, vy);
	}
}
