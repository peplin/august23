// Enable the mouse rotation and up and down arrow zooming

float rX,rZ,vX,vZ;
float zoomScale = 1.0;
float zoomScaleTarget = 6.0;
Boolean fingerDown = false;
float primaryFingerId = -1;
float previousFingerX = -1;
float previousFingerY = -1;
float fingerX = -1;
float fingerY = -1;

void rotations(){
  rX+=vX;
  rZ+=vZ;
  vX*=.95;
  vZ*=.95;

  if(mousePressed){
    vX+=(mouseY-pmouseY)*.01;
    vZ+=(mouseX-pmouseX)*.01;
  }

  if(fingerDown) {
      /*System.out.println("Previous Finger X: " + previousFingerX);
      System.out.println("Previous Finger X: " + previousFingerY);
      System.out.println("Finger X: " + fingerX);
      System.out.println("Finger Y: " + fingerY);
      */
      vX += (fingerY - previousFingerY) * .01;
      vZ += (fingerX - previousFingerX) * .01;
  }

  rotateX( radians(-rX) );  
  rotateZ( radians(- rZ) );  
}

void zooms(){
  zoomScale = lerp(zoomScale,zoomScaleTarget,.02); 
  scale(zoomScale);
}

void center(){
  translate(width/2, height/2);  
}

void keyPressed(){
  if(keyCode == UP){
    if(zoomScaleTarget < 50){
      zoomScaleTarget +=.2;
    }
  }
  if(keyCode == DOWN){
    if(zoomScaleTarget > 1){
      zoomScaleTarget -=.2;
    }
  } 
}

// called when a cursor is added to the scene
// Keep track of one primary finger and use that for movement
void addTuioCursor(TuioCursor tcur) {
    if(!fingerDown) { // if we have no others, this the new primary
        primaryFingerId = tcur.getFingerID();
    }
    fingerDown = true;
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
    if(tcur.getFingerID() == primaryFingerId) {
        previousFingerX = fingerX;
        previousFingerY = fingerY;
        fingerX = tcur.getX() * screen.width;
        fingerY = tcur.getY() * screen.height;
    }
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
    TuioCursor[] cursors = tuioClient.getTuioCursors();
    if(cursors.length == 0) { // no other fingers down
        fingerDown = false;
    } else { // new primary finger
        primaryFingerId = cursors[0].getFingerID();
    }
}

// called when an object is added to the scene
void addTuioObject(TuioObject tobj) {}

// called when an object is removed from the scene
void removeTuioObject(TuioObject tobj) {}

// called when an object is moved
void updateTuioObject (TuioObject tobj) {}

void refresh(long timestamp) { 
  redraw();
}

