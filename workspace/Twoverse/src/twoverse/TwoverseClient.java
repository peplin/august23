package twoverse;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import tuio.TuioClient;
import tuio.TuioCursor;
import tuio.TuioObject;
import twoverse.gui.Button;
import twoverse.gui.ImageButton;
import twoverse.gui.RectButton;
import twoverse.object.Galaxy;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;

public class TwoverseClient extends PApplet {
	public final static boolean DEBUG = true;
	/** TUIO & Control Members **/
	private TuioClient mTuioClient;

	/** GUI Members **/
	private PFont mButtonFont;
	private ArrayList<Button> mMainMenu;

	private enum Mode {
		NONE, CREATE, MOVE, ROTATE, ZOOM, EVOLVE, LEARN
	}

	private Mode mCurrentMode;

	/** Object & Server Members **/
	private ObjectManagerClient mObjectManager;
	private RequestHandlerClient mRequestHandler;

	/** Other **/
	int cont = 99; // TODO consider making local
	int obj = 99; // TODO consider making local
	int inv = 99; // TODO consider making local
	int mBackgroundColor = color(0); // TODO consider making local

	public void setup() {

		frameRate(30);
		size(800, 600, OPENGL);
		mCurrentMode = Mode.NONE;

		mObjectManager = new ObjectManagerClient();
		mRequestHandler = new RequestHandlerClient(mObjectManager);

		initializeButtons();

		// Run TUIO Client
		mTuioClient = new TuioClient(this);
	}

	private void initializeButtons() {
		// TODO pull all of these settings out to conf file
		// make the names meaningful
		mButtonFont = loadFont("twoverse/data/NimbusSanL-BoldCond-48.vlw");

		int baseColor = color(102);

		// Ojbect button positions
		int dx = 60;
		int x, x0 = 100, y0 = 40, r0 = 30;

		// Action Button positions
		int rectx0 = 540;
		int recty0 = 60;
		int drect = 30;
		int dy = drect + 10;

		// Slider positions
		int slidex0 = 30, slidey0 = 90;
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

		button = new RectButton(this, new Point(rectx0, recty0 + dy * 2),
				buttoncolor, highlight, "Zoom", drect, drect);
		button.addChild(new RectButton(this, new Point(slidex0, slidey0),
				buttoncolor, highlight, "zoom", 15, 150));
		mMainMenu.add(button);

		button = new RectButton(this, new Point(rectx0, recty0 + dy * 3),
				buttoncolor, highlight, "Evolve", drect, drect);
		button.addChild(new RectButton(this, new Point(slidex1, slidey1),
				buttoncolor, highlight, "evolve", 150, 15));
		mMainMenu.add(button);

		mMainMenu.add(new RectButton(this, new Point(rectx0, recty0 + dy * 4),
				buttoncolor, highlight, "Rotate", drect, drect));

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

	}

	public void draw() {
		background(mBackgroundColor);
		updateButtons();
		updateUniverse();

		if (DEBUG) {
			// Draw each cursor to the screen for debugging
			TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
			for (int i = 0; i < tuioCursorList.length; i++) {
				rect(tuioCursorList[i].getScreenX(width), tuioCursorList[i]
						.getScreenY(height), 10, 10);
			}
		}
	}

	void updateUniverse() {
		ArrayList<Galaxy> galaxies = mObjectManager.getGalaxies();
		for (int i = 0; i < galaxies.size(); i++) {
			rect((int) (galaxies.get(i).getPosition().getX()), (int) (galaxies
					.get(i).getPosition().getY()), 10, 10);
		}
	}

	void updateButtons() {
		// TODO this should go first
		TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
		for (int i = 0; i < tuioCursorList.length; i++) {
			for (int j = 0; j < mMainMenu.size(); j++) {
				// TODO how does button set mode?
				mCurrentMode = mMainMenu.get(i).update(tuioCursorList[i].getScreenX(width),
						tuioCursorList[i].getScreenY(height));
			}
		}

		//TODO if the user isn't pressing a button, they are manipulating in
		// the sandbox - we need to know what mode we're in so we can 
		// perform the correct behavior
		switch (mCurrentMode) {
		case CREATE:

		case ROTATE:
		case MOVE:
		case ZOOM:

		case EVOLVE:
		case NONE:
		case LEARN:
		}
	}

	/**
	 * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
	 * class itself, so we can't pull it out to a MultitouchHandler class
	 */

	void addTuioObject(TuioObject tobj) {
		// println("add object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
	}

	void removeTuioObject(TuioObject tobj) {
		// println("remove object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+")");
	}

	void updateTuioObject(TuioObject tobj) {
		// println("update object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
		// +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel());
	}

	void addTuioCursor(TuioCursor tcur) {
		// println("add cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+
		// ") " +tcur.getX()+" "+tcur.getY());
	}

	void updateTuioCursor(TuioCursor tcur) {
		// println("update cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+
		// ") " +tcur.getX()+" "+tcur.getY()
		// +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
	}

	void removeTuioCursor(TuioCursor tcur) {
		// println("remove cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+")");
	}

	void refresh(long timestamp) {
		// redraw();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
	}
}
