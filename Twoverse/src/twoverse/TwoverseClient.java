package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import tuio.TuioClient;
import tuio.TuioCursor;
import twoverse.object.Planet;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.util.Camera;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
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

    @Override
    public void setup() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/TwoverseClient.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }

        frameRate(Integer.valueOf(mConfigFile.getProperty("FRAME_RATE")));
        size(Integer.valueOf(mConfigFile.getProperty("WINDOW_WIDTH")),
                Integer.valueOf(mConfigFile.getProperty("WINDOW_HEIGHT")),
                P3D); // TODO try OPENGL on a 32-bit computer
        smooth();

        mCamera = new Camera(this, 0, 0, 1,
                // (float) (width / 2.0),
                // (float) (height / 2.0),
                //(float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                (float) (width / 2.0),
                (float) (height / 2.0),
                0,
                0,
                1,
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
            for (TuioCursor element : tuioCursorList) {
                rect(element.getScreenX(width),
                        element.getScreenY(height),
                        10,
                        10);
            }
        }
    }

    void updateUniverse() {
        lights();
        ArrayList<AppletBodyInterface> bodies =
                mObjectManager.getAllBodiesAsApplets(this);
        for (AppletBodyInterface body : bodies) {
            try {
                body.display();
            } catch (TwoDimensionalException e) {
                sLogger.log(Level.WARNING, "Expected 3D point but was 2D: "
                        + body, e);
            }
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
            mObjectManager.add(new Planet(-1,
                    "Earth",
                    -1,
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
        // TODO to spin around a central point, we need to modify Z as well
        // polar coordinates may be useful
        if(mouseButton == RIGHT) {
            mCamera.moveEye(-1 * radians(mouseY - pmouseY), radians(mouseX
                    - pmouseX), 0);
        } else if(mouseButton == CENTER) {
            mCamera.moveEye(0, 0, (float)(-.01 * (mouseY - pmouseY)));
        }
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
    }
}
