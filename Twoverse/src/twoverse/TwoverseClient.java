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
    // TODO pull this out to a camera class
    private float mEyeX = 0; // position of the camera in space
    private float mEyeY = -50;
    private float mEyeZ = 0;
    private float mCenterX = 0; // where the eye is looking (center of screen)
    private float mCenterY = 0;
    private float mCenterZ = 0;
    private float mUpX = 0; // 0.0, 1.0 or -1.0,to determine which axis is up
    private float mUpY = 1;
    private float mUpZ = 0;

    private float mLastMouseX = -1;
    private float mLastMouseY = -1;

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
                P3D); // TODO try OPENGL on a 32-bit computer

        mRequestHandler = new RequestHandlerClient();
        mObjectManager = new ObjectManagerClient(mRequestHandler);
        mTuioClient = new TuioClient(this);

        // initializeButtons();

        // temporary stuff to test client/server connection
        User user =
                new User(0, "xmlrpcfirst", "first@first.org", "1111111111", 100);
        user.setPlaintextPassword("foobar");
        mRequestHandler.createAccount(user);
        mRequestHandler.login(user.getUsername(), "foobar");
    }

    /*
     * private void initializeButtons() { mMainMenu = new ArrayList<Button>();
     * mButtonFont = loadFont("twoverse/data/NimbusSanL-BoldCond-48.vlw"); //
     * Ojbect button positions int dx = 60; int x0 = 100, y0 = 40, r0 = 30; //
     * Action Button positions int rectx0 = 540; int recty0 = 60; int drect =
     * 30; int dy = drect + 10; int slidex1 = 50, slidey1 = 350; // Investigate
     * button positions int invxsize = 40, invysize = 25, invx0 = 120, invy0 =
     * 350, dinvx = invxsize + 10; // Object create Buttons int buttoncolor =
     * color(100); int highlight = color(100); Button button = new
     * RectButton(this, new Point(rectx0, recty0), buttoncolor, highlight,
     * "Create", drect, drect); button.addChild(new ImageButton(this, new
     * Point(x0, y0), buttoncolor, highlight, "Satellite",
     * loadImage("twoverse/images/sputnik.jpg"), r0)); button.addChild(new
     * ImageButton(this, new Point(x0 + dx, y0), buttoncolor, highlight,
     * "Planet", loadImage("twoverse/images/12382-Planet_Ven.jpg"), r0));
     * button.addChild(new ImageButton(this, new Point(x0 + 2 dx, y0),
     * buttoncolor, highlight, "Star", loadImage("twoverse/images/sun.gif"),
     * r0)); button.addChild(new ImageButton(this, new Point(x0 + 3 dx, y0),
     * buttoncolor, highlight, "Galaxy",
     * loadImage("twoverse/images/hst_galaxy.JPG"), r0)); button.addChild(new
     * ImageButton(this, new Point(x0 + 4 dx, y0), buttoncolor, highlight,
     * "Pulsar", loadImage("twoverse/images/230240main_Pulsar1_sm.jpg"), r0));
     * mMainMenu.add(button); mMainMenu.add(new RectButton(this, new
     * Point(rectx0, recty0 + dy), buttoncolor, highlight, "Move", drect,
     * drect)); button = new RectButton(this, new Point(rectx0, recty0 + dy 3),
     * buttoncolor, highlight, "Evolve", drect, drect); button.addChild(new
     * RectButton(this, new Point(slidex1, slidey1), buttoncolor, highlight,
     * "evolve", 150, 15)); mMainMenu.add(button); button = new RectButton(this,
     * new Point(rectx0, recty0 + dy 5), buttoncolor, highlight, "Learn", drect,
     * drect); button.addChild(new RectButton(this, new Point(invx0, invy0),
     * buttoncolor, highlight, "Read", invxsize, invysize)); button.addChild(new
     * RectButton(this, new Point(invx0 + dinvx, invy0), buttoncolor, highlight,
     * "Hear", invxsize, invysize)); button.addChild(new RectButton(this, new
     * Point(invx0 + 2 dinvx, invy0), buttoncolor, highlight, "watch", invxsize,
     * invysize)); mMainMenu.add(button); }
     */

    @Override
    public void draw() {
        background(0);
        /*
         * camera(mEyeX, mEyeY, mEyeZ, mCenterX, mCenterY, mCenterZ, mUpX, mUpY,
         * mUpZ);
         */
        camera(mEyeX, mEyeY,
                //(float) (width / 2.0),
                //(float) (height / 2.0),
                (float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                (float) (width / 2.0),
                (float) (height / 2.0),
                0,
                0,
                1,
                0);
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
        ArrayList<AppletBodyInterface> bodies =
                mObjectManager.getAllBodiesAsApplets(this);
        for(AppletBodyInterface body : bodies) {
            try {
                body.display();
            } catch(TwoDimensionalException e) {
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
        mObjectManager.add(new Planet(-1, "Earth", -1, new Point(mouseX,
                mouseY,
                0), new PhysicsVector3d(1, 2, 3, 4), new PhysicsVector3d(5,
                6,
                7,
                8), 10, 10));
    }

    @Override
    public void mouseDragged() {
        //TODO to spin around a central point, we need to modify Z as well
        // polar coordinates may be useful
        if(mLastMouseX != -1 && mLastMouseY != -1) {
            mEyeX += (mouseX - mLastMouseX);
            mEyeY += (mouseY - mLastMouseY);
        }
        mLastMouseX = mouseX;
        mLastMouseY = mouseY;
    }
    
    @Override
    public void mouseReleased() {
        mLastMouseX = -1;
        mLastMouseY = -1;
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
    }
}
