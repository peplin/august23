import tuio.*;
// mods:
// let display take variables to hide or display

TuioClient tuioClient;
PFont font;
color currentcolor;

CircleButton circle1, circle2, circle3, circle0, circle4;
RectButton   rect0, rect1, rect2, rect3, rect4, rect5;
CircleButton obj0, obj1, obj2, obj3, obj4, obj5;
RectButton   cont0, cont1, cont2, cont3, cont4, cont5,slide0, slide1;
RectButton   inv0, inv1, inv2;

int cont= 99;
int obj = 99;
int inv = 99;
boolean locked = false;
color back0 = color(0);

void setup()
{
  font = loadFont("NimbusSanL-BoldCond-48.vlw"); 

  frameRate(320);
  size(600, 400);
  smooth();

  color baseColor = color(102);
  currentcolor = baseColor;

  //Ojbect button positions
  int dx=60;
  int x, x0=100,y0=40, r0=30;
  
  //Action Button positions
  int rectx0 = 540;
  int recty0 = 60;
  int drect = 30;
  int dy = drect+10;
  
  //Slider positions
  int slidex0 = 30, slidey0=90;
  int slidex1 = 50, slidey1=350;
  
  // Investigate button positions
  int invxsize = 40, invysize = 25, invx0 = 120, invy0=350, dinvx = invxsize +10;

  // Object create Buttons
  color buttoncolor = color(100);
  color highlight = color(100);
  ellipseMode(CENTER);
  PImage image0 = loadImage("/home/august/bzr/images/sputnik.jpg");
  PImage image1 = loadImage("/home/august/bzr/images/12382-Planet_Ven.jpg");
  PImage image2 = loadImage("/home/august/bzr/images/sun.gif");
  PImage image3 = loadImage("/home/august/bzr/images/hst_galaxy.JPG");
  PImage image4 = loadImage("/home/august/bzr/images/230240main_Pulsar1_sm.jpg");

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
}

void draw() {
  background(back0);
  updateButtons();

   TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
  for(int i = 0; i < tuioCursorList.length; i++) {
//    rect(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height), 10, 10);
  rect(tuioCursorList[0].getScreenX(width), tuioCursorList[0].getScreenY(height), 10, 10);
  }
}

void updateButtons()
{

  if(locked == false) {
   
    if (cont == 0) {
      if (obj == 0) { obj0.highlight();  }else{obj0.display();}
      if (obj == 1) { obj1.highlight();  }else{obj1.display();}
      if (obj == 2) { obj2.highlight();  }else{obj2.display();}
      if (obj == 3) { obj3.highlight();  }else{obj3.display();}
      if (obj == 4) { obj4.highlight();  }else{obj4.display();}
      
      cont0.highlight();
    }else{cont0.display();}
    
    if (cont ==1) {cont1.highlight();}else{cont1.display();}

    if (cont == 2){
      cont2.highlight(); 
      slide0.display();
    }else{cont2.display();}

    if ( cont ==3){
      cont3.highlight();
      slide1.display();
    }else{cont3.display();}
    
    if (cont == 4){cont4.highlight();}else{cont4.display();}
    
    if (cont == 5){
      if (inv == 0) { inv0.highlight();  }else{inv0.display();}
      if (inv == 1) { inv1.highlight();  }else{inv1.display();}
      if (inv == 2) { inv2.highlight();  }else{inv2.display();}
      cont5.highlight();
      }else{cont5.display();}

  } 
  else {
    locked = false;
  }

  TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
  for(int i = 0; i < tuioCursorList.length; i++) {

    if(cont0.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      cont = 0;
     } 
    else if(cont1.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
       cont = 1;
    }
    else if(cont2.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      cont = 2;
    } 
    else if(cont3.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      cont = 3;
    }
    else if(cont4.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      cont = 4;
    }
    else if(cont5.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      cont = 5;
    }
    
    if (cont ==0){
      if(obj0.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        obj = 0;
     } 
      else if(obj1.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        obj =1;

      } 
      else if(obj2.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        obj =2;
      } 
      else if(obj3.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        obj =3;
      } 
      else if(obj4.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        obj =4;
      } 
    }
    
    if (cont==5){
      if(inv0.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        inv = 0;
     } 
      else if(inv1.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        inv =1;

      } 
      else if(inv2.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
        inv =2;
      } 
    }
    
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

  void display() {
    noStroke();
    noFill();
    tint(255,100);
    image(myimage,x-size/2,y-size/2);
  }
  void highlight() {
    noStroke();
    noFill();
    tint(255,40000);
    image(myimage,x-size/2,y-size/2);
    tint(255,100);
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

  void display() {
    stroke(180);
    fill(color(130));
    rect(x, y, size, sizey);
    textFont(font,8);
    fill(color(255));
    text(name,x+size/4,y+sizey/2);
  }

  void highlight() {
    stroke(255);
    fill(color(230));
    rect(x, y, size, sizey);
    textFont(font,8);
    fill(color(0));
    text(name,x+size/4,y+sizey/2);
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









