
public class ConnectionMode extends GalaxyMode {
    private static final int MASTER_PARENT_ID = 1;
    private PApplet mParent;
    private ObjectManagerClient mObjectManager;
    private Camera mCamera;
    
    public ConnectionMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
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
        if(!checkStars(cursor)) {
            mObjectManager.add(new Star(0,
                    "Your Star",
                    MASTER_PARENT_ID,
                    new Point(
                        width / 2 - mCamera.getCenterX() + mouseX,
                        height/2 - mCamera.getCenterY() + mouseY, 0),
                    new PhysicsVector3d(1, 2, 3, 4),
                    new PhysicsVector3d(5, 6, 7, 8),
                    10,
                    10));
        }
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

}
