/**
** Connection Mode for Twoverse Client
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

import twoverse.object.Link;

public class ConnectionMode extends GalaxyMode {
    private Link mOpenLink = null;
    public ConnectionMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
    }

    public void display() {
        super.display();
        mCamera.resetScale();
        pushMatrix();
        smooth();
        stroke(255);
        noFill();
        translate(-width/2, -height/2);
        if(mOpenLink != null) {
            try {
                Star first
                    = (Star)mObjectManager.getCelestialBody(mOpenLink.getFirstId());
                beginShape(LINES);
                vertex((float) first.getPosition().getX(),
                        (float) first.getPosition().getY());
                vertex(-mCamera.getCenterX() + mouseX + width/2,
                        -mCamera.getCenterY() + mouseY + height/2);
                endShape();
            } catch(UnhandledCelestialBodyException e) {

            }
        }
        noSmooth();
        popMatrix();
    }

    public void cursorPressed(Point cursor) {
        Star selectedStar = checkStars(cursor);
        if(selectedStar != null) {
            if(mOpenLink != null && selectedStar.getId()
                    != mOpenLink.getFirstId()) {
                mOpenLink.setSecond(selectedStar);
                mObjectManager.add(mOpenLink);
                mOpenLink = null;
            } else {
                mOpenLink = new Link(selectedStar);
            }
        }
    }

    public void disable() {
    }

    public boolean canDisable() {
        return true;
    }
}
