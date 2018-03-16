import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;

public class OffLattice {
    private long m;
    private double eta;
    private double Rc;
    private long cycles;
    private double timeDelta;
    private String staticFilePath;
    private String dynamicFilePath;
    // Generator config
    private boolean randomGenerateParticles;
    private double speedModule;
    private long amount;
    private String type;
    private double maxRadius;

    public void initialize (){
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));

            JSONObject jsonObject = (JSONObject) obj;
            m = (long) jsonObject.get("m");
            eta = (double) jsonObject.get("eta");
            Rc = (double) jsonObject.get("Rc");
            cycles = (long) jsonObject.get("cycles");
            timeDelta = (double) jsonObject.get("timeDelta");
            randomGenerateParticles = (boolean) jsonObject.get("randomGenerateParticles");
            staticFilePath = (String) jsonObject.get("staticFilePath");
            dynamicFilePath = (String) jsonObject.get("dynamicFilePath");
            speedModule = (double) jsonObject.get("speedModule");
            amount = (long) jsonObject.get("amount");
            type = (String) jsonObject.get("type");
            maxRadius = (double) jsonObject.get("maxRadius");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        OffLattice latice = new OffLattice();
        latice.initialize();
    }
}
