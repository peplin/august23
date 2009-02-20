import tuio.*;
// mods:

TuioClient tuioClient;
PFont font;

boolean locked = false;
int nobj = 5;
int ncont = 6;
int nslide = 2;
int ninv = 3;

PImage[] imgs       = new PImage[nobj];
CircleButton[] objs = new CircleButton[nobj];
RectButton[] conts  = new RectButton[ncont];
RectButton[] slides = new RectButton[nslide];
RectButton[] invs   = new RectButton[ninv];

boolean[] objon = new boolean[nobj];
boolean[] objlock  = new boolean[nobj];
boolean[] conton = new boolean[ncont];
boolean[] contlock = new boolean[ncont];


color back0 = color(0);

void setup()
{
  font = loadFont("NimbusSanL-BoldCond-48.vlw"); 

  frameRate(320);
  size(600, 400);
  smooth();

   // Object create Buttons
  color buttoncolor = color(100);
  color highlight = color(100);
  ellipseMode(CENTER);
  String img_dir = "/home/august/bzr/images/";
  String[] img_name = { "sputnik.jpg","12382-Planet_Ven.jpg","sun.gif","hst_galaxy.JPG","230240main_Pulsar1_sm.jpg"      };
  int[] pobj={100, 40, 30, 60};
  for (int i=0 ; i<nobj; i++){
    imgs[i] = loadImage(img_dir+img_name[i]);
    imgs[i].resize(pobj[3],pobj[3]);
    objs[i] = new CircleButton(pobj[0] + i*pobj[3], pobj[1], pobj[2], buttoncolor, highlight,imgs[i]);
  }

 //Action Button positions
  int[] pcont = {540, 60, 30, 10};
  String[] cont_name = { "create","move","zoom","evolve","rotate","learn" };
  for (int i=0 ; i<ncont; i++){
    conts[i] = new RectButton(pcont[0], pcont[1]+ i*(pcont[2] + pcont[3]) , pcont[2], pcont[2], buttoncolor, highlight,cont_name[i]);
  }
  
   //Slider positions
  int[] pslide = {30, 90, 50, 350, 15, 150};
  String[] slide_name ={ "zoom" , "evolve" }; 
  slides[0] = new RectButton(pslide[0], pslide[1], pslide[4], pslide[5], buttoncolor, highlight,slide_name[0]);
  slides[1] = new RectButton(pslide[2], pslide[3], pslide[5], pslide[4], buttoncolor, highlight,slide_name[1]);
  
  
  // Investigate button positions
  int[] pinv = {120, 350, 40, 25, 10};
  String[] inv_name = { "create","move","zoom"  };
  for (int i=0 ; i<ninv; i++){
    invs[i] = new RectButton(pinv[0] + i*(pinv[3]+pinv[4]), pinv[1], pinv[2], pinv[2], buttoncolor, highlight,inv_name[i]);
  }

   // Object create Buttons
  ellipseMode(CENTER);

   // Run TUIO Client
  tuioClient = new TuioClient(this);

}

void draw() {
  background(back0);
  updateTouch();
  updateMouse();

  TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
  for(int i = 0; i < tuioCursorList.length; i++) {
    //    rect(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height), 10, 10);
    rect(tuioCursorList[0].getScreenX(width), tuioCursorList[0].getScreenY(height), 10, 10);
  }
}




void updateMouse() {

    if(locked == false) {
      } 
  else {
    locked = false;
  }
  
  
  for (int i = 0; i< ncont; i++){
    conton[i] = conts[i].display(conts[i].over(mouseX, mouseY) && mousePressed);
    conts[i].display(conton[i] || contlock[i]);
    if (conton[i] && !contlock[i]) { // if lock not true, but press is true, change lock to true
      contlock[i] = true;
      for (int j = 0; j< ncont; j++){ // set all other contlocks to off
        if (i != j) {
          conton[j] = false; 
          contlock[j] = false;
        }
      }
    }

    
 }
 
 for (int i = 0; i< nobj; i++){
   objon[i]= objs[i]display(objs[i].over(mouseX, mouseY) && mousePressed);
   objlock[i] = objon[i] + contlock[0];
   for (int j = 0; j< nobj; j++){
            if (i != j) {
              objon[j] = false; 
              objlock[j] = false;
            }
          }
 }
   
 
 
 if (contlock[0]) {
      for (int j=0 ; j < nobj ; j++ ){
        objon[j]= objs[j].display(objs[j].over(mouseX, mouseY) && mousePressed);
        objs[j].display(objon[j] || objlock[j]);
        if (objon[j] && !objlock[j]) { 

          objlock[j] = true;
          for (int k = 0; k< nobj; k++){
            if (j != k) {
              objon[k] = false; 
              objlock[k] = false;
            }
          }
        }


      }
    }
 
 
}




// UPDATE WITH TUIO
void updateTouch()
{


  TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
  for(int i = 0; i < tuioCursorList.length; i++) {
  }
  
  
}

/////////////////////////////////////////
class Button {
  int x, y;
  int size;
  int sizey;
  color basecolor, highlightcolor;
  color currentcolor;
  boolean pressed = false;   
  String name;

  void update() 
  {
    if(pressed()) {
      currentcolor = highlightcolor;
    } 
    else {
      currentcolor = basecolor;
    }
  }

  boolean pressed() {
    if(pressed) {
      locked = true;
      return true;
    } 
    return false;
  }

  boolean over(int cursorX, int cursorY) {
    return false;
  }

  boolean overRect(int cursorX, int cursorY, 
  int x, int y, int width, int height) {
    if (cursorX >= x && cursorX <= x + width && 
      cursorY >= y && cursorY <= y + height) {
      return true;
    } 
    else {
      return false;
    }
  }

  boolean overCircle(int cursorX, int cursorY, int x, int y, int diameter) {
    float disX = x - cursorX;
    float disY = y - cursorY;
    if(sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
      return true;
    } 
    else {
      return false;
    }
  }

}

class CircleButton extends Button { 
  PImage myimage;

  CircleButton(int ix, int iy, int isize, color icolor, color ihighlight, PImage iimage) {
    x = ix;
    y = iy;
    size = isize;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
    myimage = iimage;
  }

  boolean over(int cursorX, int cursorY) {
    if( overCircle(cursorX, cursorY, x, y, size) ) {
      pressed = true;
      return true;
    } 
    else {
      pressed = false;
      return false;
    }
  }

void check(boolean disp) {  
    if ( disp ){
      noStroke();
      noFill();
      tint(255,40000);
      image(myimage,x-size/2,y-size/2);
    } 
    else  {
      noStroke();
      noFill();
      tint(255,10);
      image(myimage,x-size/2,y-size/2);     
    }
  //  return disp;
  }



  boolean display(boolean disp) {  
    if ( disp ){
      noStroke();
      noFill();
      tint(255,40000);
      image(myimage,x-size/2,y-size/2);
    } 
    else  {
      noStroke();
      noFill();
      tint(255,10);
      image(myimage,x-size/2,y-size/2);     
    }
    return disp;
  }
}

class RectButton extends Button {
  RectButton(int ix, int iy, int isize, int jsize, color icolor, color ihighlight, String iname) 
  {
    x = ix;
    y = iy;
    size  = isize;
    sizey = jsize;
    name = iname;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
  }

  boolean over(int cursorX, int cursorY) {
    if( overRect(cursorX, cursorY, x, y, size, sizey) ) {
      pressed = true;
      return true;
    } 
    else {
      pressed = false;
      return false;
    }
  }

  boolean display(boolean disp) {
    if (disp ){
      stroke(255);
      fill(color(230));
      rect(x, y, size, sizey);
      textFont(font,8);
      fill(color(0));
      text(name,x+size/4,y+sizey/2);
    }
    else {
      stroke(180);
      fill(color(130));
      rect(x, y, size, sizey);
      textFont(font,8);
      fill(color(255));
      text(name,x+size/4,y+sizey/2);
    }
    return disp;
  }
}

// called when an object is added to the scene
void addTuioObject(TuioObject tobj) {
  // println("add object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
}

// called when an object is removed from the scene
void removeTuioObject(TuioObject tobj) {
  //println("remove object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+")");
}

// called when an object is moved
void updateTuioObject (TuioObject tobj) {
  //println("update object "+tobj.getFiducialID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
  //        +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel());
}

// called when a cursor is added to the scene
void addTuioCursor(TuioCursor tcur) {
  //println("add cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
  // println("update cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
  //         +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {
  //println("remove cursor "+tcur.getFingerID()+" ("+tcur.getSessionID()+")");
}

// called after each message bundle
// representing the end of an image frame
void refresh(long timestamp) { 
  //redraw();
}












