/**
** TUIO Controller for Twoverse Multitouch Client
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

/**
The TuioController is a utility class to handle incoming TUIO input events.

This class is used to abstract input away from mouse+keyboard or TUIO, so that
the same client code can be shared between a desktop or multitouch client.

This class contains some skeleton code for tracking a "primary" finger (ie. the
first finger you see), but it was not used for the August 23, 1966 gallery show
and is not fully tested. Use it as a guideline for your own TUIO programming.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
public class TuioController {
    private PApplet mParent;
    private TuioClient mClient;
    private boolean fingerDown = false;
    private boolean fingerMoved = true;
    private float primaryFingerId = -1;
    private float previousFingerX = -1;
    private float previousFingerY = -1;
    private float fingerX = -1;
    private float fingerY = -1;

    TuioController(PApplet parent) {
        mParent = parent;
        mParent.registerDraw(this);
        mClient = new TuioClient(parent);
    }

    void draw() {
        if(DEBUG) {
            // Draw each cursor to the screen for debugging
            TuioCursor[] tuioCursorList = mClient.getTuioCursors();
            for(int i = 0; i < tuioCursorList.length; i++) {
                ellipse(tuioCursorList[i].getScreenX(width),
                        tuioCursorList[i].getScreenY(height),
                        10,
                        10);
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
            //mParent.cursorPressed(fingerX, fingerY);
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
            //mParent.cursorDragged(fingerX, fingerY);
        }
    }

    // called when a cursor is removed from the scene
    void removeTuioCursor(TuioCursor tcur) {
        fingerDown = false;
        fingerMoved = false;
    }
}
