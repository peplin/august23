import processing.video.*;
import java.util.*;
import tuio.*;

TuioClient tuioClient;
Capture video;


int[] lensArray;  // Height and width of lens
int[] buffer;

/** Configuration Parameters **/
final int LENS_DIAMETER = 200;
final int MAGNIFICATION_FACTOR = 40;

HashMap lenses;
boolean mapLock = false;

void setup() {
  size(640, 480);

  // TUIO setup
  tuioClient  = new TuioClient(this);

  background(0);
  video = new Capture( this, 640, 480, 15 );
  buffer = new int[video.width * video.height];
  lensArray = new int[video.width * video.height];
  lenses = new HashMap();
  initializeLensMatrix();
  
  loadPixels();  // load pixels into memory for manip
}

void initializeLensMatrix() {
  for(int i = 0; i < lensArray.length; i++) {
     lensArray[i] = i;
  } 
}

void updateLensMatrix() {
  mapLock = true;
  Iterator it = lenses.entrySet().iterator();
  while(it.hasNext()) {
    int m, a, b;
    int r = LENS_DIAMETER / 2;
    float s = sqrt(r*r - pow(MAGNIFICATION_FACTOR, 2));
    if(!it.hasNext()) 
      break;
    Lens lens = (Lens) (((Map.Entry)it.next()).getValue());
     
    for (int y = 0; y < video.height; y++) {
      for (int x = 0; x < video.width; x++) {
       
        int distanceToCenterX = x - lens.getX();
        int distanceToCenterY = y - lens.getY();
        if(pow(distanceToCenterX, 2) + pow(distanceToCenterY, 2) >= s*s) {
          // point is outside the circle of the lens, so its pixel should not
          // be modified
          a = x;
          b = y;
        }
        else {
          // point is under the lens, so point it somewhere else
          float z = sqrt(r*r
            - pow(distanceToCenterX, 2) 
            - pow(distanceToCenterY, 2));
          a = int(distanceToCenterX * MAGNIFICATION_FACTOR / z + 0.5);
          b = int(distanceToCenterY * MAGNIFICATION_FACTOR / z + 0.5);
          a += lens.getX();
          b += lens.getY();
        }

        lensArray[x + y * video.width] = a + b * video.width;
      }
    }
  }
  mapLock = false;
}

void captureEvent(Capture c) {
  c.read();
  c.loadPixels();
  arraycopy(c.pixels, buffer);
}


void draw() {
  updateLensMatrix();
  int[] outputBuffer = new int[video.width * video.height];
  for(int i = 0; i < video.width * video.height; i++) {
    outputBuffer[i] = buffer[lensArray[i]];
  }
  arraycopy(outputBuffer, g.pixels);
  updatePixels();
}

class Lens {
  private int mX;
  private int mY;
   public Lens(int x, int y) {
     mX = x;
     mY = y;
   }
   
   public void setX(int x) {
      mX = x; 
   }
   public void setY(int y) {
      mY = y; 
   }
   public int getX() {
      return mX; 
   }
   public int getY() {
      return mY; 
   }
}

// called when a cursor is added to the scene
void addTuioCursor(TuioCursor tcur) {
  //println("add cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
  // println("update cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
  //         +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
  if(!mapLock)
    lenses.put(tcur.getFingerID(), new Lens(tcur.getScreenX(video.width), tcur.getScreenY(video.height)));
 }

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
  if(!mapLock)
  lenses.remove(tcur.getFingerID());
}


