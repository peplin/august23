/**
** TUIO Controller for Multitouch Test
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

float rX,rZ,vX,vZ;
float zoomScale = 1.0;
float zoomScaleTarget = 6.0;
Boolean fingerDown = false;
boolean fingerMoved = true;
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
    if(fingerMoved) {
      vX += (fingerY - previousFingerY) * .01;
      vZ += (fingerX - previousFingerX) * .01;
    }  
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
  
    fingerDown = true;
    fingerX = previousFingerX = tcur.getX() * screen.width;
    fingerY = previousFingerY = tcur.getY() * screen.width;
    }
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
  if(tcur.getFingerID() == primaryFingerId) {
    fingerMoved = true;
    previousFingerX = fingerX;
    previousFingerY = fingerY;
    fingerX = tcur.getX() * screen.width;
    fingerY = tcur.getY() * screen.height;
  }
}

void removeTuioCursor(TuioCursor tcur) {
    fingerDown = false;
    fingerMoved = false;
}

void refresh(long timestamp) { 
  redraw();
}


