package ar.edu.itba.ss.output;

import ar.edu.itba.ss.particle.EscapingParticle;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static ar.edu.itba.ss.data.Data.D;
import static ar.edu.itba.ss.data.Data.RAD_MAX;
import static ar.edu.itba.ss.data.Data.W;

public class Output {

	private String fileName;
	private int c = 0;


	public Output(String fileName) {
		this.fileName = fileName;
		try {
			Files.createDirectories(Paths.get("a/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printState(List<? extends EscapingParticle> particles) {
		List<String> lines = new LinkedList<>();
		lines.add(String.valueOf(particles.size()));
		lines.add(""+c);
		c++;
		for (EscapingParticle p : particles) {
			lines.add(p.getInfo());
		}
		lines.set(0, String.valueOf(Integer.valueOf(lines.get(0)) + borders(lines)));
		writeFile(lines);
	}

	private int borders(List<String> lines){
        double x = 0.0;
        int count = 0;
        while(x <= W){
			if ( ! (((W-D)/2.0 < x) && ( x < (W+D)/2.0))){
				lines.add("-1 "+x+" 4.0 "+RAD_MAX/10.0+" 1 0 0");
				count++;
			}
			x += RAD_MAX/10.0;
		}

		lines.add("-1 0.0 0.0 "+RAD_MAX/10000.0+" 1 0 0");
        lines.add("-1 20.0 0.0 "+RAD_MAX/10000.0+" 1 0 0");
        lines.add("-1 0.0 24.0 "+RAD_MAX/10000.0+" 1 0 0");
        lines.add("-1 20.0 24.0 "+RAD_MAX/10000.0+" 1 0 0");

        return 4+count;
    }

    private void writeFile(List<String> lines) {
		try {
            FileWriter fw = new FileWriter(fileName, true);
            for (String line: lines) {
                fw.write(line + "\n");
            }
            fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
