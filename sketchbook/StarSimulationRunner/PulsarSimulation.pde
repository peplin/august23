import processing.opengl.*;
//import wiremap.Wiremap;
//import wiremap.WiremapGlowingSphere;


public class PulsarSimulation {
    private VBPointCloud core, jet;
    private float i;
    public PulsarSimulation(PApplet parent) {
        core = new VBPointCloud(parent);
        jet = new VBPointCloud(parent);
        core.loadFloats(loadPoints("data1.csv"));
        jet.loadFloats(loadPoints("jet.csv"));
        core.pointSize = 1.f;
        jet.pointSize=0.1f; 
    }

    public void display() {
        background(0);
        translate(width/2,height/2,0);
        rotateZ(-PI/4);
        rotateY(i);
        stroke(5,255,255,255); 
        scale(0.3);
        core.draw();
        
        stroke(5,5,250,255);
        scale(20,10,10);
        jet.draw();
        i = (i + .05) % (2 * PI);
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
