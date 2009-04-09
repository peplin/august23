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
        } catch(UnhandledCelestialBodyException e) {
            println("Caught exception when updating universe: " + e);
        }
        popMatrix();
    }


    public void cursorPressed(Point cursor) {
        checkStars(cursor);
    }

    boolean checkStars(Point cursor) {
        pushMatrix();
        translate(-width/2, -height/2);
        try {
            CelestialBody parent =
                mObjectManager.getCelestialBody(MASTER_PARENT_ID);
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
                        println("clicked on star: " + body.getId());
                        popMatrix();
                        return true;
                    }
                } catch(TwoDimensionalException e) {
                    println(e);
                }
            }
        } catch(UnhandledCelestialBodyException e) {
            println("Caught exception when updating universe: " + e);
        }
        popMatrix();
        return false;
    }

    void cursorDragged(Point cursor) {

    }

    void disable() {

    }

}
