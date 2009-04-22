/**
** Active Color Background (tester for ActiveColorGrabber)
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

import codeanticode.gsvideo.*;

ActiveColorGrabber grabber;

/**
ActiveColorBackground is a test application for the ActiveColorGrabber utility
class. It simply updates the background color of the screen to the average
active color on a video feed.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
void setup() {
  size(320, 240, P3D); 
  grabber = new ActiveColorGrabber(this);

  loadPixels();
}

void draw() {
    background(grabber.getActiveColor());
}
