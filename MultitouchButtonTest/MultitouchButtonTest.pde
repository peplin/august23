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
boolean[] lock = new boolean[ncont];
boolean[] locka = new boolean[ncont];
boolean[] acton = new boolean[ncont];
CircleButton[] objs = new CircleButton[nobj];
RectButton[] conts  = new RectButton[ncont];
RectButton[] slides = new RectButton[nslide];
RectButton[] invs   = new RectButton[ninv];


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
  String[] img_name = {
    "sputnik.jpg","12382-Planet_Ven.jpg","sun.gif","hst_galaxy.JPG","230240main_Pulsar1_sm.jpg"      };
  for (int i=0 ; i<nobj; i++){
    imgs[i] = loadImage(img_dir+img_name[i]);
   int dx=60;
  int x, x0=100,y0=40, r0=30;
    imgs[i].resize(r0,r0);
    objs[i] = new CircleButton(x0 + i*dx, y0, r0, buttoncolor, highlight,imgs[i]);
  }

 //Action Button positions
  int rectx0 = 540;
  int recty0 = 60;
  int drect = 30;
  int dy = drect+10;
  String[] cont_name = {
    "create","move","zoom","evolve","rotate","learn"      };
  for (int i=0 ; i<ncont; i++){
    conts[i] = new RectButton(rectx0, recty0+ i*dy, drect, drect, buttoncolor, highlight,cont_name[i]);
  }
  String[] slide_name ={
    "zoom","evolve"      }; 
  for (int i=0 ; i<nslide; i++){
    slides[i] = new RectButton(rectx0, recty0+ i*dy, drect, drect, buttoncolor, highlight,slide_name[i]);
  }
  
  // Investigate button positions
  int invxsize = 40, invysize = 25, invx0 = 120, invy0=350, dinvx = invxsize +10;
  String[] inv_name = {
    "create","move","zoom"      };
  for (int i=0 ; i<ninv; i++){
    invs[i] = new RectButton(rectx0, recty0+ i*dy, drect, drect, buttoncolor, highlight,inv_name[i]);
  }

   //Slider positions
  int slidex0 = 30, slidey0=90;
  int slidex1 = 50, slidey1=350;
  
  // Investigate button positions
  int invxsize = 40, invysize = 25, invx0 = 120, invy0=350, dinvx = invxsize +10;

  // Object create Buttons
  color buttoncolor = color(100);
  color highlight = color(100);
  ellipseMode(CENTER);
  PImage image0 = loadImage("../images/sputnik.jpg");
  PImage image1 = loadImage("../images/12382-Planet_Ven.jpg");
  PImage image2 = loadImage("../images/sun.gif");
  PImage image3 = loadImage("../images/hst_galaxy.JPG");
  PImage image4 = loadImage("../images/230240main_Pulsar1_sm.jpg");

  image0.resize(r0,r0);
  image1.resize(r0,r0);
  image2.resize(r0,r0);
  image3.resize(r0,r0);
  image4.resize(r0,r0);
  obj0 = new CircleButton(x0,  y0, r0,     buttoncolor, highlight,image0);
  obj1 = new CircleButton(x0+  dx, y0, r0, buttoncolor, highlight,image1);
  obj2 = new CircleButton(x0+2*dx, y0, r0, buttoncolor, highlight,image2);
  obj3 = new CircleButton(x0+3*dx, y0, r0, buttoncolor, highlight,image3);
  obj4 = new CircleButton(x0+4*dx, y0, r0, buttoncolor, highlight,image4);

  // Action Buttons
  buttoncolor = color(100);
  highlight = color(100); 
  cont0 = new RectButton(rectx0, recty0,      drect, drect, buttoncolor, highlight,"create");
  cont1 = new RectButton(rectx0, recty0+  dy, drect, drect, buttoncolor, highlight,"move");
  cont2 = new RectButton(rectx0, recty0+2*dy, drect, drect, buttoncolor, highlight,"zoom");
  cont3 = new RectButton(rectx0, recty0+3*dy, drect, drect, buttoncolor, highlight,"evolve");
  cont4 = new RectButton(rectx0, recty0+4*dy, drect, drect, buttoncolor, highlight,"rotate");
  cont5 = new RectButton(rectx0, recty0+5*dy, drect, drect, buttoncolor, highlight,"learn");

  // Sliders
  slide0 = new RectButton(slidex0, slidey0, 15, 150, buttoncolor,highlight,"zoom");
  slide1 = new RectButton(slidex1, slidey1, 150, 15, buttoncolor,highlight,"evolve");
  
  // Investigation
  inv0 = new RectButton(invx0, invy0,         invxsize, invysize, buttoncolor, highlight,"read");
  inv1 = new RectButton(invx0+  dinvx, invy0, invxsize, invysize, buttoncolor, highlight,"hear");
  inv2 = new RectButton(invx0+2*dinvx, invy0, invxsize, invysize, buttoncolor, highlight,"watch");

  // Run TUIO Client
  tuioClient = new TuioClient(this);


  for(int i = 0; i < ncont; i++) {
    lock[i] = false;
    locka[i] = false;
    acton[i] = false;
  }
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
  
//  boolean[] objon = new boolean[nobj];
//  for (int i = 0; i< ncont; i++){
//    acton[i] = conts[i].display(conts[i].over(mouseX, mouseY) && mousePressed);
//    conts[i].display(acton[i] || lock[i]);
//    if (acton[i] && !lock[i]) { 
//      lock[i] = true;
//      for (int j = 0; j< ncont; j++){
//        if (i != j) {
//          acton[j] = false; 
//          lock[j] = false;
//        }
//      }
//    }
//
//    if (lock[0]) {
//      for (int j=0 ; j < nobj ; j++ ){
//        objon[j]= objs[j].display(objs[j].over(mouseX, mouseY) && mousePressed);
//        objs[j].display(objon[j] || locka[j]);
//        if (objon[j] && !locka[j]) { 
//          locka[j] = true;
//          for (int k = 0; k< nobj; k++){
//            if (j != k) {
//              objon[k] = false; 
//              locka[k] = false;
//            }
//          }
//        }
//
//
//      }
//    }
//  }
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
    return disp;
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












