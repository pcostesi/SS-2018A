package ar.edu.itba.abstraction;

public abstract class Algorithm {

  // ParticleList

  public Algorithm() {
    generateBorderParticles(x,y);
  }

  public void loop(){
    basic_loop();
  }

  public abstract void basic_loop();

  public void generateBorderParticles(double x, double y){
    // border particles = Generate 4 Particles (0,0)(x,0)(0,y)(x,y)
  }

  public String getInfo(int loopNumber, double x, double y){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append('\t').append(/*ParticleList length + */4).append('\n');
    stringBuilder.append('\t').append(loopNumber).append('\n');
    /*for (Particle p : particles) {
      stringBuilder.append(p.getInfo())
    }
    for (Particle p : borderParticles) {
      stringBuilder.append(p.getInfo())
    }
    */
    return stringBuilder.toString();

  }
}
