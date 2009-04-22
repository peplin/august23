/**
** Galaxy Mode for Twoverse Client
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

public class GalaxyMode implements ModeInterface {
    protected static final int MASTER_PARENT_ID = 1;
    protected PApplet mParent;
    protected ObjectManagerClient mObjectManager;
    protected Camera mCamera;
    
    public GalaxyMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        mParent = parent;
        mObjectManager = objectManager;
        mCamera = camera;
    }

    public void display() {
        pushMatrix();
        translate(-width/2, -height/2);
        try {
            CelestialBody parent = mObjectManager.getCelestialBody(MASTER_PARENT_ID);
            for(int i = 0; i < parent.getChildren().size(); i++) {
                Star body =
                        (Star) (mObjectManager.getCelestialBody(
                                    parent.getChildren().get(i)));
                try {
                    body.getAsApplet(mParent).display();
                } catch(TwoDimensionalException e) {
                    println(e);
                }
            }
            smooth();
            ArrayList starLinks = mObjectManager.getAllLinks();
            stroke(255);
            for(int i = 0; i < starLinks.size(); i++) {
                Link link = (Link) starLinks.get(i);
                Star first
                    = (Star)mObjectManager.getCelestialBody(link.getFirstId());
                Star second
                    = (Star)mObjectManager.getCelestialBody(link.getSecondId());
                beginShape(LINES);
                vertex((float) first.getPosition().getX(),
                        (float) first.getPosition().getY());
                vertex((float) second.getPosition().getX(),
                        (float) second.getPosition().getY());
                endShape();
            }
            noSmooth();
        } catch(UnhandledCelestialBodyException e) {
            println("Caught exception when updating universe: " + e);
        }
        popMatrix();
    }


    public void cursorPressed(Point cursor) {
        Star star = checkStars(cursor);
        if(star != null) {
            setMode(1);
            ((InfoMode)getMode()).setSelectedStar(star);
        }
    }

    public Star checkStars(Point cursor) {
        pushMatrix();
        translate(-width/2, -height/2);
        try {
            CelestialBody parent =
                mObjectManager.getCelestialBody(MASTER_PARENT_ID);
            cursor.setX(width/2 - mCamera.getCenterX() + cursor.getX());
            cursor.setY(height/2 - mCamera.getCenterY() + cursor.getY());
            for(int i = 0; i < parent.getChildren().size(); i++) {
                Star body =
                        (Star) (mObjectManager.getCelestialBody(
                                    parent.getChildren().get(i)));
                try {
                    Point bodyPosition = new Point(
                            screenX((float)body.getPosition().getX(), 
                                (float)body.getPosition().getY(), 
                                (float)body.getPosition().getZ()),
                            screenY((float)body.getPosition().getX(), 
                                (float)body.getPosition().getY(),
                                (float)body.getPosition().getZ()),
                            0);
                    if(cursor.getX() <= bodyPosition.getX() + body.getRadius() 
                            && cursor.getX() >= bodyPosition.getX() - body.getRadius()
                            && cursor.getY() <= bodyPosition.getY() + body.getRadius() 
                            && cursor.getY() >= bodyPosition.getY() - body.getRadius()) {
                        popMatrix();
                        return body;
                    }
                } catch(TwoDimensionalException e) {
                    println(e);
                }
            }
        } catch(UnhandledCelestialBodyException e) {
            println("Caught exception when updating universe: " + e);
        }
        popMatrix();
        return null;
    }

    public void cursorDragged(Point cursor) {

    }

    public void disable() {

    }

    public boolean canDisable() {
        return true;
    }
}
