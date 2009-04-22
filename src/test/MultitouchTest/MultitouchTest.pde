/**
** Multitouch Motion Test
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
**
** Based on House of Cards sketch by Aaron Koblin
**
** Copyright 2009 Christopher Peplin 
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at 
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and
** limitations under the License. 
*/

import processing.opengl.*;
import tuio.*;
import ca.ubc.cs.wiimote.*;

VBPointCloud cloud;
    TuioClient tuioClient;

void setup(){
    size(1024, 768, OPENGL);
    cloud = new VBPointCloud(this);
    tuioClient = new TuioClient(this);
    cloud.loadFloats(loadPoints("culdesac.csv"));
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
  for (int i = 0; i < raw.length; i++) {
    String[] thisLine = split(raw[i], ",");
    points[i * 3] = new Float(thisLine[0]).floatValue() / 1000;
    points[i * 3 + 1] = new Float(thisLine[1]).floatValue() / 1000;
    points[i * 3 + 2] = new Float(thisLine[2]).floatValue() / 1000;
  }
  println("Loaded: "+raw.length+" points");
  return points;
}


