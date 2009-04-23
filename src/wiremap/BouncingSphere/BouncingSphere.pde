/**
** Bouncing Sphere for Wiremap
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
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

/**
 * This sketch is a brief demo of a sphere bouncing around inside the Wiremap
 * field.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
void setup() {
  size(1024, 768, P3D);

  map = new Wiremap(this, 256, 90, 36, 48, 36.0/9.0, .1875, 2,
  "depth.txt");


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


