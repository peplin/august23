import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;

Wiremap map;
WiremapGlowingSphere glowingSphere;

boolean goingUp = false;
boolean goingLeft = false;
boolean goingBack = false;

float x = 0;
float y = 0;
float z = 10;

void setup() {
  size(1024, 768, P3D);

  map = new Wiremap(this, 256, 90, 36, 48, 36.0/9.0, .1875, 2,
  "/home/peplin/programming/august/sketchbook/wiremap/ManualCalibrator/calibration-round1.txt");


  glowingSphere = new WiremapGlowingSphere(
  map, 500, 300, 20, color(255, 255, 0), 8, 
  color(255, 0, 0)); 
}

void draw() {
  background(0);

  if(goingBack) {
    z += 1;
    if(z >= 35) {
       goingBack = false; 
    }
  } 
  else {
    z -= 1;
    if(z <= 1) {
       goingBack = true; 
    }
  }

  if(goingUp) {
    y -= 10;
    if(y <= 20) {
       goingUp = false; 
    }
  } 
  else {
    y += 10;
    if(y >= height - 40) {
       goingUp = true; 
    }
  }

  if(goingLeft) {
    x -= 10;
    if(x <= 20) {
       goingLeft = false; 
    }
  } 
  else {
    x += 10;
    if(x >= width - 60) {
        goingLeft = true;
    }
  }    
  glowingSphere.setPosition((int)x, (int)y, (int)z);
  glowingSphere.display();
}


