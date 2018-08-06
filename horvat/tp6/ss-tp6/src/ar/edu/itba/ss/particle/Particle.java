package ar.edu.itba.ss.particle;

public class Particle {
	
	private int id;
	Pair position;
	Pair velocity;
	private double radius;
	private double mass;
	

	Particle(int id, double x, double y, double vx, double vy, double m, double r) {
		this.id = id;
		this.position = new Pair(x, y);
		this.velocity = new Pair(vx, vy);
		this.mass = m;
		this.radius = r;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return position.x;
	}
	
	public double getY(){
		return position.y;
	}
	
	public Pair getPosition() {
		return position;
	}
	
	public Pair getVelocity(){
		return velocity;
	}
	
	public double getMass(){
		return mass;
	}

	public double getRadius() {
		return radius;
	}
	
	public void updatePosition(double x, double y) {
		this.position = new Pair(x, y);
	}
	
	public void updateVelocity(double x, double y) {
		this.velocity = new Pair(x, y);
	}

	
	public static <T extends Particle> boolean areOverlapped(T p, T q){
		return Pair.dist2(p.position, q.position) <= Math.pow(p.getRadius()+q.getRadius(),2);
	}

	public double getSpeed() {
		return velocity.abs();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getInfo() {
		return getId() + " " + getX() + " " + getY() + " " + getRadius() + " " + "255 255 255";
	}

	public void collision(){}
}
