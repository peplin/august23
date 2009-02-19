import processing.video.*;
import java.util.*;
import tuio.*;

TuioClient tuioClient;
Capture video;
PGraphics lensEffect;
PImage lensImage;
PImage lensImage2;
PGraphics imgtmp;

int lensDiameter = 150;  // Lens diameter
int magFactor = 45;  // Magnification factor
int[] lensArray = new int[lensDiameter*lensD];  // Height and width of lens

void setup() {
  size( 640, 480 );

  // TUIO setup
  /**noStroke();
   * fill(0);
   * loop();
   * hint(ENABLE_NATIVE_FONTS);
   * font = createFont("Arial", 18);
   * scale_factor = height/table_size;
   * tuioClient  = new TuioClient(this);
   **/

  background(0);
  video = new Capture( this, width, height, 60 );
    loadPixels();  // load pixels into memory for manip
}

void initializeLensMatrix() {
    // Lens algorithm (transformation array)
    int m, a, b;
    int r = lensDiameter / 2;
    float s = sqrt(r*r - magFactor*magFactor);

    for (int y = -r; y < r; y++) {
      for (int x = -r ;x < r; x++) {
        if(x*x + y*y >= s*s) {
          a = x;
          b = y;
        }
        else {
          float z = sqrt(r*r - x*x - y*y);
          a = int(x * magFactor / z + 0.5);
          b = int(y * magFactor / z + 0.5);
        }
        lensArray[(y + r) * (lensDiameter + width) + (x + r)]
                            = (b + r) * (lensDiameter + width) + (a + r);
      }
    }
}

void captureEvent(Capture c) {
    c.read();
    c.loadPixels();
    arraycopy(c.pixels, buffer);
}


void draw() {
    int startingCornerX = 50;
    int startingCornerY = 50;
    for(int x = 0; x < lensDiameter; x++) {
        for(int y = 0; y < lensDiameter; y++) {
            buffer[startingCornerX + x 
                    + (video.width * (y + startingCornerY))] 
                            *= lensArray[x + (lensDiameter * y)];
        }
    }
    arraycopy(buffer, g.pixels);
    updatePixels();


    /**background(255);
     * textFont(font,18*scale_factor);
     * float obj_size = object_size*scale_factor; 
     * float cur_size = cursor_size*scale_factor; 
     * 
     * TuioObject[] tuioObjectList = tuioClient.getTuioObjects();
     * for (int i=0;i<tuioObjectList.length;i++) {
     * TuioObject tobj = tuioObjectList[i];
     * stroke(0);
     * fill(0);
     * pushMatrix();
     * translate(tobj.getScreenX(width),tobj.getScreenY(height));
     * rotate(tobj.getAngle());
     * rect(-obj_size/2,-obj_size/2,obj_size,obj_size);
     * popMatrix();
     * fill(255);
     * text(""+tobj.getFiducialID(), tobj.getScreenX(width), tobj.getScreenY(height));
     * }
     * 
     * TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
     * for (int i=0;i<tuioCursorList.length;i++) {
     * TuioCursor tcur = tuioCursorList[i];
     * TuioPoint[] pointList = tcur.getPath();
     * 
     * if (pointList.length>0) {
     * stroke(0,0,255);
     * TuioPoint start_point = pointList[0];
     * for (int j=0;j<pointList.length;j++) {
     * TuioPoint end_point = pointList[j];
     * line(start_point.getScreenX(width),start_point.getScreenY(height),end_point.getScreenX(width),end_point.getScreenY(height));
     * start_point = end_point;
     * }
     * 
     * stroke(192,192,192);
     * fill(192,192,192);
     * ellipse( tcur.getScreenX(width), tcur.getScreenY(height),cur_size,cur_size);
     * fill(0);
     * text(""+ tcur.getFingerID(),  tcur.getScreenX(width)-5,  tcur.getScreenY(height)+5);
     * }
     * 
     * }
     **/
  }
}

