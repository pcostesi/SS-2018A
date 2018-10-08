package ar.edu.itba.ss.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputStat {

	private String fileName;
	List<String> stats = new ArrayList<>();
	
	public OutputStat(String file) {
        this.fileName = file;
    }
	
	public void addStat(String stat) {
		stats.add(stat);
	}
	
	public void writeFile() {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			fw.write("[");
			boolean first = true;
			for (String stat : stats){
				if (first){
					first = false;
					fw.write(stat);
				}else {
					fw.write("," + stat);
				}
			}
			fw.write("]");
            fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
