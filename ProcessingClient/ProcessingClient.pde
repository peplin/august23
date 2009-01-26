import processing.opengl.*;
import tuio.*;
import ca.ubc.cs.wiimote.*;

VBPointCloud cloud;
    TuioClient tuioClient;

void setup(){
    size(1280, 1024, OPENGL);
    cloud = new VBPointCloud(this);
    tuioClient = new TuioClient(this);
    //--------------------------Choose which file to load HERE!-------------------------------------------------------------
    //cloud.loadFloats(loadPoints("culdesac.csv"));
    cloud.loadFloats(loadPoints("/home/peplin/programming/processing/sketchbook/hoc/SceneViewer/data/city.csv"));
}

void draw(){
  background(0);
  
  center();
  rotations();
  zooms();

  stroke(255,50,0,150);
  cloud.draw();
}

float[] loadPoints(String path) {
  String[] raw = loadStrings(path);
  float[] points = new float[raw.length * 3];
  //colors = new float[raw.length*4];
  for (int i = 0; i < raw.length; i++) {
    String[] thisLine = split(raw[i], ",");
    points[i * 3] = new Float(thisLine[0]).floatValue() / 1000;
    points[i * 3 + 1] = new Float(thisLine[1]).floatValue() / 1000;
    points[i * 3 + 2] = new Float(thisLine[2]).floatValue() / 1000;

    //colors[i*4] = new Float(thisLine[3]).floatValue()/3f ;
    //colors[i*4+1] = new Float(thisLine[3]).floatValue()/3f ;
    //colors[i*4+2] = 0f ;
    //colors[i*4+3] = 100f ;

  }
  println("Loaded: "+raw.length+" points");
  return points;
}


