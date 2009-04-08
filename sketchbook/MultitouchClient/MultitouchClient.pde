import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.opengl.*;
import tuio.TuioClient;
import tuio.TuioCursor;
import twoverse.ObjectManagerClient;
import twoverse.RequestHandlerClient;
import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.object.CelestialBody;
import twoverse.object.Star;
import twoverse.util.Camera;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

/** Constants for Configuration **/
private static final boolean DEBUG = true;
private static final int WINDOW_WIDTH = 1024;
private static final int WINDOW_HEIGHT = 768;
private static final int FRAME_RATE = 30;

/** TUIO & Control Members **/
private TuioController mTuioController;

/** Object & Server Members **/
private ObjectManagerClient mObjectManager;
private RequestHandlerClient mRequestHandler;

/** Camera Properties **/
private Camera mCamera;

private int mParentId = 1;

void setup() {
    frameRate(FRAME_RATE);
    size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);

    mCamera =
            new Camera(this,
                    (float) (width / 2.0),
                    (float) (height / 2.0),
                    (float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                    (float) (width / 2.0),
                    (float) (height / 2.0),
                    0, 1);

    mRequestHandler = new RequestHandlerClient();
    mTuioController = new TuioController(this);

    User user =
            new User(0, "august_mt", null, null, 100);
    //TODO before releasing code, figure out how to hide this
    user.setPlaintextPassword("grocs1966");
    mRequestHandler.createAccount(user);
    mRequestHandler.login(user.getUsername(), "grocs1966");

    mObjectManager = new ObjectManagerClient(mRequestHandler);

    Handler[] handlers = Logger.getLogger("").getHandlers();
    for(int i = 0; i < handlers.length; i++) {
        handlers[i].setLevel(Level.WARNING);
    }

}

void draw() {
    background(0);
    mCamera.setCamera();
    updateUniverse();
    updateInterface();

}

void updateInterface() {

}

void updateUniverse() {
    pushMatrix();
    translate(-width/2, -height/2);
    try {
        CelestialBody parent = mObjectManager.getCelestialBody(mParentId);
        for(int i = 0; i < parent.getChildren().size(); i++) {
            CelestialBody body =
                    (CelestialBody) (mObjectManager.getCelestialBody(
                                parent.getChildren().get(i)));
            try {
                body.getAsApplet(this).display();
            } catch(TwoDimensionalException e) {
                println(e);
            }
        }
    } catch(UnhandledCelestialBodyException e) {
        //println("Caught exception when updating universe: " + e);
    }
    popMatrix();
}


void mousePressed() {
    if(mouseButton == LEFT) {
        mObjectManager.add(new Star(0,
                "Your Star",
                mParentId,
                new Point(width / 2 - mCamera.getCenterX() + mouseX,
                    height/2 - mCamera.getCenterY() + mouseY, 0),
                new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8),
                10,
                10));
    }
}

void mouseDragged() {
    if(mouseButton == RIGHT) {
        mCamera.changeTranslateVelocity(mouseX - pmouseX, mouseY - pmouseY);
    } else if(mouseButton == CENTER) {
        mCamera.zoom((float) (-.01 * (mouseY - pmouseY)));
    }
}

/**
    * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
    * class itself, so we can't pull it out to a MultitouchHandler class
    */
void addTuioCursor(TuioCursor tcur) {
    mTuioController.addTuioCursor(tcur);
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
    mTuioController.updateTuioCursor(tcur);
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
    mTuioController.removeTuioCursor(tcur);
}
