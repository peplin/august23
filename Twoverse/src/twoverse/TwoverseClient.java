package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import tuio.TuioClient;
import tuio.TuioCursor;
import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.object.CelestialBody;
import twoverse.object.Planet;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.util.Camera;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
// TODO ultimately, pull this out into a regular PDE where you just import the
// Twoverse.jar file - that will make random clients easier
public class TwoverseClient extends PApplet {
    private Properties mConfigFile;
    protected static Logger sLogger =
            Logger.getLogger(TwoverseClient.class.getName());

    /** TUIO & Control Members **/
    private TuioClient mTuioClient;

    /** Object & Server Members **/
    private ObjectManagerClient mObjectManager;
    private RequestHandlerClient mRequestHandler;

    /** Camera Properties **/
    private Camera mCamera;

    private int mParentId = 1;

    @Override
    public void setup() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/TwoverseClient.properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }

        frameRate(Integer.valueOf(mConfigFile.getProperty("FRAME_RATE")));
        size(Integer.valueOf(mConfigFile.getProperty("WINDOW_WIDTH")),
                Integer.valueOf(mConfigFile.getProperty("WINDOW_HEIGHT")),
                P3D);
        smooth();

        mCamera =
                new Camera(this,
                        (float) (width / 2.0),
                        (float) (height),
                        (float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                        0,
                        0,
                        0);

        mRequestHandler = new RequestHandlerClient();
        mObjectManager = new ObjectManagerClient(mRequestHandler);
        mTuioClient = new TuioClient(this);

        // temporary stuff to test client/server connection
        User user =
                new User(0, "xmlrpcfirst", "first@first.org", "1111111111", 100);
        user.setPlaintextPassword("foobar");
        mRequestHandler.createAccount(user);
        mRequestHandler.login(user.getUsername(), "foobar");

        Handler[] handlers = Logger.getLogger("").getHandlers();
        for(int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(Level.WARNING);
        }
    }

    @Override
    public void draw() {
        background(0);
        mCamera.setCamera();
        updateUniverse();

        if(Boolean.valueOf(mConfigFile.getProperty("DEBUG"))
                && Boolean.valueOf(mConfigFile.getProperty("USE_TUIO"))) {
            // Draw each cursor to the screen for debugging
            TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
            for(TuioCursor element : tuioCursorList) {
                rect(element.getScreenX(width),
                        element.getScreenY(height),
                        10,
                        10);
            }
        }
    }

    void updateUniverse() {
        lights();
        try {
            CelestialBody parent = mObjectManager.getCelestialBody(mParentId);

            for(int i : parent.getChildren()) {
                CelestialBody body =
                        (CelestialBody) (mObjectManager.getCelestialBody(i));
                try {
                    body.getAsApplet(this).display();
                } catch(TwoDimensionalException e) {
                    sLogger.log(Level.WARNING, "Expected 3D point but was 2D: "
                            + body, e);
                }
            }
        } catch(UnhandledCelestialBodyException e) {
            sLogger.log(Level.WARNING, "Unexpected celestial body type", e);
        }
    }

    /**
     * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
     * class itself, so we can't pull it out to a MultitouchHandler class
     */
    public void addTuioCursor(TuioCursor tcur) {
    }

    @Override
    public void mousePressed() {
        if(mouseButton == LEFT) {
            mObjectManager.add(new Planet(0,
                    "Earth",
                    mParentId,
                    new Point(modelX(mouseX, mouseY, 0), modelY(mouseX,
                            mouseY,
                            0), 0),
                    new PhysicsVector3d(1, 2, 3, 4),
                    new PhysicsVector3d(5, 6, 7, 8),
                    10,
                    10));
        }
    }

    @Override
    public void mouseDragged() {
        if(mouseButton == RIGHT) {
            mCamera.moveCenter(mouseX - pmouseX, mouseY - pmouseY, 0);
        } else if(mouseButton == CENTER) {
            mCamera.moveEye(0, 0, (float) (-.01 * (mouseY - pmouseY)));
        }
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
    }
}
