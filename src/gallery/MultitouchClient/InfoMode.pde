/**
** Information Mode for Twoverse Client
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
The Info Mode is a more detailed, close up view of a single star. It displays
textual & numerical statistics for the star, as well as a larger view.

Clicking and dragging the mouse in this mode will move the viewpoint
left/right/up/down.

Scrolling the mouse wheel in this mode will NOT change the zoom level.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
public class InfoMode implements ModeInterface {
    protected static final int MASTER_PARENT_ID = 1;
    private Star mSelectedStar = null;
    private PApplet mParent;
    private PFont mFont;

    public InfoMode(PApplet parent) {
        mParent = parent;
        mFont = loadFont("buttonFont.vlw");
    }

    public void display() {
        if(mSelectedStar == null) {
            setMode(0);
        } else {
            pushMatrix();
            translate(-50, -50);
            mParent.noStroke();
            mParent.fill((float) mSelectedStar.getColorR(),
                    (float) mSelectedStar.getColorG(),
                    (float) mSelectedStar.getColorB());
            mParent.ellipse(0, 0, (float) mSelectedStar.getRadius() * 20,
                    (float) mSelectedStar.getRadius() * 20);

            for(int i = 2; i < 100; i++) {
                mParent.fill((float) mSelectedStar.getColorR(),
                        (float) mSelectedStar.getColorG(),
                        (float) mSelectedStar.getColorB(),
                        (float) (255.0 / i/2.0));
                mParent.ellipse(0, 0, (float) mSelectedStar.getRadius() * 20 + i,
                        (float) mSelectedStar.getRadius() * 20 + i);
            }
            displayStats();
            popMatrix();
        }
    } 

    private void displayStats() {
        fill(255, 255, 255);
        textMode(SCREEN);
        textFont(mFont);
        text("Born: " + mSelectedStar.getBirthTime(), 10, height - 100);
        String typeString = "Type: ";
        int endState = mSelectedStar.getState();
        if(endState == 0) {
            typeString += "Black Hole";
        } else if(endState == 1) {
            typeString += "Supernova";
        } else {
            typeString += "Pulsar";
        }
        text(typeString, 10, height - 70);
        text("Frequency: " + mSelectedStar.getFrequency(), 10, height - 40);
        String locationString = "Location: ";
        Point starPosition = mSelectedStar.getPosition();
        locationString += starPosition.getX() + ", " + starPosition.getY();
        text(locationString, 10, height - 10);
    }

    public void setSelectedStar(Star star) {
        mSelectedStar = star;
    }

    public void cursorPressed(Point cursor) {

    }

    public void cursorDragged(Point cursor) {

    }

    public void disable() {
        mSelectedStar = null;
    }

    public boolean canDisable() {
        return true;
    }
}
