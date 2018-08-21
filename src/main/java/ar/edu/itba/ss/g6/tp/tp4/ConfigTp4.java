package ar.edu.itba.ss.g6.tp.tp4;

import org.codehaus.jackson.map.ObjectMapper;

import javax.json.bind.annotation.JsonbProperty;
import java.io.File;

//TODO CopyPasted from tp3, check what 4 needs
public class ConfigTp4 {

    public int getParticles() {
        return particles;
    }

    public void setParticles(int n) {
        particles = n;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double l) {
        length = l;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    @JsonbProperty("radius")
    private double radius = 0.005;

    @JsonbProperty("speed")
    private double speed = 0.3;

    @JsonbProperty("weight")
    private double weight = 0.0;

    @JsonbProperty("duration")
    private double duration = 60 * 5;

    @JsonbProperty("timeStep")
    private double timeStep = 0.1;

    @JsonbProperty("particles")
    private int particles = 30;

    @JsonbProperty("length")
    private double length = 0.5;

    public static ConfigTp4 loadConfig(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File("runconfigs\\tp3config.json"), ConfigTp4.class);
        } catch(Exception e){
            System.out.println(System.getProperty("user.dir"));
            System.out.println("error loading config");
            System.exit(-1);
            return null;
        }
    }
}