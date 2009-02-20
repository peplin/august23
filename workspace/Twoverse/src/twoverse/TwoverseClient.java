package twoverse;

import java.util.ArrayList;
import java.util.Random;

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
	private final static boolean DEBUG = false;
	private final static boolean USE_TUIO = true;
	private final int GUI_SIDE_MIN_X = screen.width - 100;
	private final int GUI_TOP_MIN_Y = screen.height - 100;
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

        public Random randomNumber = new Random(1);

	/** Other **/
	int cont = 99; // TODO consider making local
	int obj = 99; // TODO consider making local
	int inv = 99; // TODO consider making local
	int mBackgroundColor = color(0); // TODO consider making local


	/** Rendering sphere  **/
	PImage bg;
	PImage[] texmap = new PImage[40];

	int sDetail = 30;  //Sphere detail setting
	float rotationX = 0;
	float rotationY = 0;
	float velocityX = 0;
	float velocityY = 0;
	float globeRadius;
	float pushBack = 0;

	float[] cx,cz,sphereX,sphereY,sphereZ;
	float sinLUT[];
	float cosLUT[];
	float SINCOS_PRECISION = 0.5f;
	int SINCOS_LENGTH = int(360.0 / SINCOS_PRECISION);
	

	public void setup() {

		frameRate(30);
		size(800, 600);
		mCurrentMode = Mode.NONE;

		mObjectManager = new ObjectManagerClient();
		mRequestHandler = new RequestHandlerClient(mObjectManager);

		initializeButtons();

		/** load image textures **/
		String imgs_dir = "twoverse/images/";
		String[] imgs_map ={"sun.gif","earthmap1k.jpg","mercurymap.jpg","saturnmap.jpg","moonbump1k.jpg","sputnik.jpg","deimosbump.jpg","jupiter2_1k.jpg","moonmap1k.jpg","sun.gif","earthbump1k.jpg","jupitermap.jpg","neptunemap.jpg","sunmap.jpg","earthcloudmap.jpg","mars_1k_color.jpg","phobosbump.jpg","uranusmap.jpg","earthlights1k.jpg","marsmap1k.jpg","plutomap1k.jpg","venusmap.jpg"  };
		  for ( int i = 0; i < 40; i++) {
		    texmap[i] = loadImage(imgs_dir+imgs_map[i]);
		  }

		initializeSphere(sDetail);

		// Run TUIO Client
		mTuioClient = new TuioClient(this);
	}

	private void initializeButtons() {
		// TODO pull all of these settings out to conf file
		// make the names meaningful
		mMainMenu = new ArrayList<Button>();
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

	}

	public void draw() {
		background(mBackgroundColor);
                updateButtons();
		updateUniverse();

		if (DEBUG && USE_TUIO) {
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
                    renderGlobe((int) (galaxies.get(i).getPosition().getX()), (int) (galaxies
                        .get(i).getPosition().getY()), texmap[randomNumber.next() % 39]);
		}
	}

	void updateButtons() {
		//TODO must display at least once, so we don't have to hold down
		// two parts - update() and display() based on wht you learned from
		// update()
		for (int i = 0; i < mMainMenu.size(); i++) {
			mMainMenu.get(i).update(new Point(-1, -1));
		}
		if (USE_TUIO) {
			TuioCursor[] tuioCursorList = mTuioClient.getTuioCursors();
			for (int i = 0; i < tuioCursorList.length; i++) {
				for (int j = 0; j < mMainMenu.size(); j++) {
					mMainMenu.get(j).update(
							new Point(tuioCursorList[i].getScreenX(width),
									tuioCursorList[i].getScreenY(height)));
				}
			}
		} else {
			for (int i = 0; i < mMainMenu.size(); i++) {
				mMainMenu.get(i).update(new Point(mouseX, mouseY));
			}
		}
	}

void renderGlobe(int x, int y, float rr, PImage mytexmap ) {
  pushMatrix();
  translate(width/2.0+x, height/2.0-y, pushBack);
  pushMatrix();
  noFill();
  stroke(255,200);
  strokeWeight(2);
  smooth();
  popMatrix();
  lights();    
  pushMatrix();
  rotateX( radians(-rotationX) );  
  rotateY( radians(270 - rotationY) );
  fill(200);
  noStroke();
  textureMode(IMAGE);  
  texturedSphere(rr, mytexmap);
  popMatrix();  
  popMatrix();
  rotationX += velocityX;
  rotationY += velocityY;
  velocityX *= 0.95;
  velocityY *= 0.95;

  // Implements mouse control (interaction will be inverse when sphere is  upside down)
  if(mousePressed){
    velocityX += (mouseY-pmouseY) * 0.01;
    velocityY -= (mouseX-pmouseX) * 0.01;
  }
}

void initializeSphere(int res) {
  sinLUT = new float[SINCOS_LENGTH];
  cosLUT = new float[SINCOS_LENGTH];

  for (int i = 0; i < SINCOS_LENGTH; i++) {
    sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
    cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
  }

  float delta = (float)SINCOS_LENGTH/res;
  float[] cx = new float[res];
  float[] cz = new float[res];

  // Calc unit circle in XZ plane
  for (int i = 0; i < res; i++) {
    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
  }

  // Computing vertexlist vertexlist starts at south pole
  int vertCount = res * (res-1) + 2;
  int currVert = 0;

  // Re-init arrays to store vertices
  sphereX = new float[vertCount];
  sphereY = new float[vertCount];
  sphereZ = new float[vertCount];
  float angle_step = (SINCOS_LENGTH*0.5f)/res;
  float angle = angle_step;

  // Step along Y axis
  for (int i = 1; i < res; i++) {
    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
    for (int j = 0; j < res; j++) {
      sphereX[currVert] = cx[j] * curradius;
      sphereY[currVert] = currY;
      sphereZ[currVert++] = cz[j] * curradius;
    }
    angle += angle_step;
  }
  sDetail = res;
}

// Generic routine to draw textured sphere
void texturedSphere(float r, PImage t) {
  int v1,v11,v2;
//  r = (r + 240 ) * 0.33;
  r = (r + 40 ) * 0.33;
  beginShape(TRIANGLE_STRIP);
  texture(t);
  float iu=(float)(t.width-1)/(sDetail);
  float iv=(float)(t.height-1)/(sDetail);
  float u=0,v=iv;
  for (int i = 0; i < sDetail; i++) {
    vertex(0, -r, 0,u,0);
    vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
    u+=iu;
  }
  vertex(0, -r, 0,u,0);
  vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
  endShape();   

  // Middle rings
  int voff = 0;
  for(int i = 2; i < sDetail; i++) {
    v1=v11=voff;
    voff += sDetail;
    v2=voff;
    u=0;
    beginShape(TRIANGLE_STRIP);
    texture(t);
    for (int j = 0; j < sDetail; j++) {
      vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
      vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
      u+=iu;
    }

    // Close each ring
    v1=v11;
    v2=voff;
    vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
    endShape();
    v+=iv;
  }
  u=0;

  // Add the northern cap
  beginShape(TRIANGLE_STRIP);
  texture(t);
  for (int i = 0; i < sDetail; i++) {
    v2 = voff + i;
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
    vertex(0, r, 0,u,v+iv);    
    u+=iu;
  }
  vertex(0, r, 0,u, v+iv);
  vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
  endShape();

}



	/**
	 * TUIO Callbacks: Unfortunately, these must be implemented in the PApplet
	 * class itself, so we can't pull it out to a MultitouchHandler class
	 */

	//TODO remove this...
	private static int galaxy_counter = 0;
	public void addTuioCursor(TuioCursor tcur) {
		System.out.println("foo");
		if (tcur.getScreenX(800) < GUI_SIDE_MIN_X
				&& tcur.getScreenY(600) < GUI_TOP_MIN_Y) {
			System.out.println("foo1");
			mObjectManager.add(new Galaxy(galaxy_counter++, -1, "theBody", null, null, -1,
					new Point(tcur.getScreenX(800), tcur.getScreenY(600), 0), new PhysicsVector3d(1, 2, 3, 4),
					new PhysicsVector3d(5, 6, 7, 8), new GalaxyShape(1, "test",
							"test"), 1000.5, 2000.20));
		}
	}
	
	public void mousePressed() {
		if (mouseX < GUI_SIDE_MIN_X
				&& mouseY < GUI_TOP_MIN_Y) {
			mObjectManager.add(new Galaxy(galaxy_counter++, -1, "theBody", null, null, -1,
					new Point(mouseX, mouseY, 0), new PhysicsVector3d(1, 2, 3, 4),
					new PhysicsVector3d(5, 6, 7, 8), new GalaxyShape(1, "test",
							"test"), 1000.5, 2000.20));
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "twoverse.TwoverseClient" });
	}
}
