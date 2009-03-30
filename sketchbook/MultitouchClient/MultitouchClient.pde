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
import twoverse.object.Planet;
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
private TuioClient mTuioClient;

/** Object & Server Members **/
private ObjectManagerClient mObjectManager;
private RequestHandlerClient mRequestHandler;

/** Camera Properties **/
private Camera mCamera;

private int mParentId = 1;

void setup() {
    frameRate(FRAME_RATE);
    size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
    smooth();

    mCamera =
            new Camera(this,
                    (float) (width / 2.0),
                    (float) (height),
                    (float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                    0,
                    0,
                    0, 1);

    mRequestHandler = new RequestHandlerClient();
    mTuioClient = new TuioClient(this);

    User user =
            new User(0, "multitouch", "august23@umich.edu", NULL, 100);
    mRequestHandler.createAccount(user);
    mRequestHandler.login(user.getUsername(), "txy1k2");

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

    if(DEBUG) {
        // Draw each cursor to the screen for debugging
        TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
        for(int i = 0; i < tuioCursorList.length; i++) {
            rect(tuioCursorList[i].getScreenX(width),
                    tuioCursorList[i].getScreenY(height),
                    10,
                    10);
        }
    }
}

void updateUniverse() {
    lights();
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
        println("Caught exception when updating universe: " + e);
    }
}

/**
    * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
    * class itself, so we can't pull it out to a MultitouchHandler class
    */
void addTuioCursor(TuioCursor tcur) {
}

void mousePressed() {
    if(mouseButton == LEFT) {
        mObjectManager.add(new Star(0,
                "Your Star",
                mParentId,
                new Point(modelX(mouseX, mouseY, 0),
                        modelY(mouseX, mouseY, 0),
                        0),
                new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8),
                10,
                10));
    }
}

void mouseDragged() {
    if(mouseButton == RIGHT) {
        mCamera.moveCenter(mouseX - pmouseX, mouseY - pmouseY, 0);
    } else if(mouseButton == CENTER) {
        mCamera.zoom((float) (-.01 * (mouseY - pmouseY)));
    }
}
