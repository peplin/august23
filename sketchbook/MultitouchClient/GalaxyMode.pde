public class GalaxyMode implements MultitouchModeInterface {
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

    Star checkStars(Point cursor) {
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

    void cursorDragged(Point cursor) {

    }

    void disable() {

    }

}
