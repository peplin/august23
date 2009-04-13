import processing.opengl.*;

public class PulsarSimulation {
    private float jet[];
    private float core[];
    private float jetRotation;
    public PulsarSimulation(PApplet parent) {
        core = loadPoints("core.csv");
        jet = loadPoints("jet.csv");
    }

    public void display() {
        background(0);
        rotateZ(-PI/4);
        rotateY(jetRotation);
        stroke(5,255,255,255); 
        scale(0.3);
        for(int i = 0; i < core.length; i += 3) {
            point(core[i], core[i + 1], core[i + 2]);
        }
        
        stroke(5,5,250,255);
        scale(20,10,10);
        for(int i = 0; i < jet.length; i += 3) {
            point(jet[i], jet[i + 1], jet[i + 2]);
        }
        jetRotation = (jetRotation + .05) % (2 * PI);
    }

    private float[] loadPoints(String path) {
        String[] raw = loadStrings(path);
        float[] points = new float[raw.length * 3];
        for (int i = 0; i < raw.length; i++) {
            String[] thisLine = split(raw[i], ",");
        
            points[i * 3] = new Float(thisLine[0]).floatValue();
            points[i * 3 + 1] = new Float(thisLine[1]).floatValue();
            points[i * 3 + 2] = new Float(thisLine[2]).floatValue();
        }
        println("Loaded: "+raw.length+" points");
        return points;
    }
}
