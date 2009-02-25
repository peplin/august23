package twoverse;

import java.util.ArrayList;
import java.util.Timer;

import processing.core.PApplet;
import processing.core.PFont;
import tuio.TuioClient;
import tuio.TuioCursor;
import twoverse.gui.Button;
import twoverse.gui.ImageButton;
import twoverse.gui.RectButton;
import twoverse.object.Galaxy;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class TwoverseClient extends PApplet {
    private final static boolean DEBUG = false;
    private final static boolean USE_TUIO = true;
    private Properties mConfigFile;

    /** TUIO & Control Members **/
    private TuioClient mTuioClient;

    /** Object & Server Members **/
    private ObjectManagerClient mObjectManager;
    private RequestHandlerClient mRequestHandler;

    @Override
    public void setup() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("twoverse/conf/TwoverseClient.properties"));
            Class.forName(DB_CLASS_NAME);
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        frameRate(Integer.valueOf(mConfigFile.getProperty("FRAME_RATE")));
        size(Integer.valueOf(mConfigFile.getProperty("WINDOW_WIDTH")),
            Integer.valueOf(mConfigFile.getProperty("WINDOW_HEIGHT")));

        mRequestHandler = new RequestHandlerClient();
        mObjectManager = new ObjectManagerClient(mRequestHandler);
        mTuioClient = new TuioClient(this);

        //initializeButtons();


        // temporary stuff to test client/server connection
        User user = new User(0, "xmlrpcfirst", "first@first.org", "1111111111",
                100);
        user.setPlaintextPassword("foobar");
        mRequestHandler.createAccount(user);
        mRequestHandler.login(user.getUsername(), "foobar");

        mObjectManager.add(new Galaxy(-1, "theBody", -1, new Point(42, 43, 44),
                new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8), new GalaxyShape(1, "test",
                        "test"), 1000.5, 2000.20));
    }

    /*private void initializeButtons() {
        mMainMenu = new ArrayList<Button>();
        mButtonFont = loadFont("twoverse/data/NimbusSanL-BoldCond-48.vlw");

        // Ojbect button positions
        int dx = 60;
        int x0 = 100, y0 = 40, r0 = 30;

        // Action Button positions
        int rectx0 = 540;
        int recty0 = 60;
        int drect = 30;
        int dy = drect + 10;
        int slidex1 = 50, slidey1 = 350;

        // Investigate button positions
        int invxsize = 40, invysize = 25, invx0 = 120, invy0 = 350, dinvx = invxsize + 10;

        // Object create Buttons

        int buttoncolor = color(100);
        int highlight = color(100);

        Button button = new RectButton(this, new Point(rectx0, recty0),
                buttoncolor, highlight, "Create", drect, drect);
        button.addChild(new ImageButton(this, new Point(x0, y0), buttoncolor,
                highlight, "Satellite",
                loadImage("twoverse/images/sputnik.jpg"), r0));
        button.addChild(new ImageButton(this, new Point(x0 + dx, y0),
                buttoncolor, highlight, "Planet",
                loadImage("twoverse/images/12382-Planet_Ven.jpg"), r0));
        button.addChild(new ImageButton(this, new Point(x0 + 2 * dx, y0),
                buttoncolor, highlight, "Star",
                loadImage("twoverse/images/sun.gif"), r0));
        button.addChild(new ImageButton(this, new Point(x0 + 3 * dx, y0),
                buttoncolor, highlight, "Galaxy",
                loadImage("twoverse/images/hst_galaxy.JPG"), r0));
        button.addChild(new ImageButton(this, new Point(x0 + 4 * dx, y0),
                buttoncolor, highlight, "Pulsar",
                loadImage("twoverse/images/230240main_Pulsar1_sm.jpg"), r0));
        mMainMenu.add(button);

        mMainMenu.add(new RectButton(this, new Point(rectx0, recty0 + dy),
                buttoncolor, highlight, "Move", drect, drect));

        button = new RectButton(this, new Point(rectx0, recty0 + dy * 3),
                buttoncolor, highlight, "Evolve", drect, drect);
        button.addChild(new RectButton(this, new Point(slidex1, slidey1),
                buttoncolor, highlight, "evolve", 150, 15));
        mMainMenu.add(button);

        button = new RectButton(this, new Point(rectx0, recty0 + dy * 5),
                buttoncolor, highlight, "Learn", drect, drect);
        button.addChild(new RectButton(this, new Point(invx0, invy0),
                buttoncolor, highlight, "Read", invxsize, invysize));
        button.addChild(new RectButton(this, new Point(invx0 + dinvx, invy0),
                buttoncolor, highlight, "Hear", invxsize, invysize));
        button.addChild(new RectButton(this,
                new Point(invx0 + 2 * dinvx, invy0), buttoncolor, highlight,
                "watch", invxsize, invysize));
        mMainMenu.add(button);

    }*/

    @Override
    public void draw() {
        background(0);
        updateUniverse();

        if (Boolean.valueOf(mConfigFile.getProperty("DEBUG")) 
                && Boolean.valueOf(mConfigFile.getProperty("USE_TUIO"))) {
            // Draw each cursor to the screen for debugging
            TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
            for (TuioCursor element : tuioCursorList) {
                rect(element.getScreenX(width), element.getScreenY(height), 10,
                        10);
            }
        }
    }

    void updateUniverse() {
        lights();
        ArrayList<AppletCelestialBody> bodies = mObjectManager.getAllBodiesAsApplets(this);
        for(AppletCelestialBody body : bodies) {
            body.display();
        }
    }

    /**
     * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
     * class itself, so we can't pull it out to a MultitouchHandler class
     */
    public void addTuioCursor(TuioCursor tcur) {
        if (tcur.getScreenX(Integer.valueOf(mConfigFile.getProperty("WINDOW_WIDTH")) < GUI_SIDE_MIN_X
                && tcur.getScreenY(Integer.valueOf(mConfigFile.getProperty("WINDOW_HEIGHT")) < GUI_TOP_MIN_Y) {
        }
    }

    @Override
    public void mousePressed() {
        if (mouseX < GUI_SIDE_MIN_X && mouseY < GUI_TOP_MIN_Y) {
        }
    }

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
    }
}
