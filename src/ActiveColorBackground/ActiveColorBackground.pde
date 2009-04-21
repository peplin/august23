import codeanticode.gsvideo.*;

ActiveColorGrabber grabber;

void setup() {
  size(320, 240, P3D); 
  grabber = new ActiveColorGrabber(this);

  loadPixels();
}

void draw() {
    background(grabber.getActiveColor());
}
