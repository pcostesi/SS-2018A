package ar.edu.itba.ss;

import ar.edu.itba.ss.output.Output;
import ar.edu.itba.ss.output.OutputStat;
import ar.edu.itba.ss.particle.EscapingParticle;
import ar.edu.itba.ss.particle.Particle;
import ar.edu.itba.ss.particle.SocialModelSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static ar.edu.itba.ss.data.Data.*;

public class TP6 {

    private static final Random random = new Random();

	private static double getRandomNumber(double min, double max){
		return random.nextDouble() * (max - min) + min;
	}

	private static EscapingParticle createRandomParticle() {
		double r = getRandomNumber(RAD_MIN, RAD_MAX) / 2.0;
		double x = getRandomNumber(0, W - 2 * r)+r;
		double y = getRandomNumber(0, L - 2 * r) + floorLevel + r;
		return new EscapingParticle(id_count, x, y, 0, 0, mass, r);
	}

	private static List<EscapingParticle> generateParticles(int N) {
		List<EscapingParticle> list = new ArrayList<>();
		boolean overlap;
		id_count = 1;
		while (id_count - 1 < N) {
			overlap = false;
			EscapingParticle newParticle = createRandomParticle();
			for (EscapingParticle otherParticle : list) {
				if (Particle.areOverlapped(otherParticle, newParticle)) {
					overlap = true;
					break;
				}
			}
			if (!overlap) {
				list.add(newParticle);
				id_count++;
			}
		}
		return list;
	}

	private static void systemSimulation(int loop){
		String desiredVelocityStr = String.format(Locale.US,"%.2f",desiredVelocity);
		Output ovitoFile = new Output("ovitoFile-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat peopleFile = new OutputStat("people-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat caudalValueFile = new OutputStat("caudalValue-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat caudalTimeFile = new OutputStat("caudalTime-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		List<EscapingParticle> particles = generateParticles(N);
		SocialModelSimulator socialModelSimulator = new SocialModelSimulator(particles, dt);

		double time = 0.0;
		int totalCaudal = 0;
		Integer totalDiff = 0;
		Integer partialDiff = 0;
		double lastTime = - dt2 - 1.0;
		double maxPressure = 0.0;
		double lastCaudalTime = 0.0;
		int caudalPopulation = (int)(N / 20.0);

		while (totalCaudal < N) {
			if (lastTime + dt2 < time) {
				ovitoFile.printState(particles);
				double mp = particles.stream().mapToDouble(x -> x.getPressure()).max().getAsDouble();
				if (maxPressure < mp) {
					maxPressure = mp;
				}
				lastTime = time;
			}
			socialModelSimulator.loop();
			int diff = getDifPeople(particles);
			totalDiff += diff;
			partialDiff += diff;
			totalCaudal += diff;
			peopleFile.addStat(totalDiff.toString());
			while (partialDiff >= caudalPopulation){
				caudalValueFile.addStat(caudalPopulation / ((time - lastCaudalTime)*D) + "");
				caudalTimeFile.addStat(time+"");
				lastCaudalTime = time;
				partialDiff -= caudalPopulation;
			}
			/*for (int i = 0; i < diff; i++) {
				caudal.addStat(String.valueOf(time));
			}*/
			time += dt;
		}
		System.out.println("Time elapsed: " + time);
		peopleFile.writeFile();
		caudalValueFile.writeFile();
		caudalTimeFile.writeFile();
	}

    public static void main(String[] args){
		for (double dVelocity : desiredVelocities){
			desiredVelocity = dVelocity;
			for (int time = 0; time < times; time++){
				systemSimulation(time);
			}
		}
	}

	private static int getDifPeople(List<EscapingParticle> particles) {
		int diff = 0;
		for (EscapingParticle particle : particles) {
			if (particle.getLastPosition().y > floorLevel && particle.getPosition().y <= floorLevel) {
				diff += 1;
			}
		}
		return diff;
	}

}
