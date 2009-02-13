import tuio.*;

color currentcolor;
CircleButton circle1, circle2, circle3;
RectButton rect1, rect2;

TuioClient tuioClient;

boolean locked = false;

void setup()
{
  size(200, 200);
  smooth();

  color baseColor = color(102);
  currentcolor = baseColor;

  // Define and create circle button
  color buttoncolor = color(204);
  color highlight = color(153);
  ellipseMode(CENTER);
  circle1 = new CircleButton(30, 100, 100, buttoncolor, highlight);

  // Define and create circle button
  buttoncolor = color(204);
  highlight = color(153); 
  circle2 = new CircleButton(130, 110, 24, buttoncolor, highlight);

  // Define and create circle button
  buttoncolor = color(153);
  highlight = color(102); 
  circle3 = new CircleButton(130, 140, 24, buttoncolor, highlight);

  // Define and create rectangle button
  buttoncolor = color(102);
  highlight = color(51); 
  rect1 = new RectButton(150, 20, 100, buttoncolor, highlight);

  // Define and create rectangle button
  buttoncolor = color(51);
  highlight = color(0); 
  rect2 = new RectButton(90, 20, 50, buttoncolor, highlight);

  tuioClient = new TuioClient(this);
}

void draw() {
  background(currentcolor);
  stroke(255);
  updateButtons();
  circle1.display();
  circle2.display();
  circle3.display();
  rect1.display();
  rect2.display();
}

void updateButtons()
{
  if(locked == false) {
    circle1.update();
    circle2.update();
    circle3.update();
    rect1.update();
    rect2.update();
  } 
  else {
    locked = false;
  }

  TuioCursor[] tuioCursorList = tuioClient.getTuioCursors();
  for(int i = 0; i < tuioCursorList.length; i++) {
    if(circle1.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      currentcolor = circle1.basecolor;
    } 
    else if(circle2.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      currentcolor = circle2.basecolor;
    } 
    else if(circle3.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      currentcolor = circle3.basecolor;
    } 
    else if(rect1.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      currentcolor = rect1.basecolor;
    } 
    else if(rect2.over(tuioCursorList[i].getScreenX(width), tuioCursorList[i].getScreenY(height))) {
      currentcolor = rect2.basecolor;
    }
  }
}


class Button {
  int x, y;
  int size;
  color basecolor, highlightcolor;
  color currentcolor;
  boolean pressed = false;   

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
    } else {
      return false;
    }
  }

  boolean overCircle(int cursorX, int cursorY, int x, int y, int diameter) {
    float disX = x - cursorX;
    float disY = y - cursorY;
    if(sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
      return true;
    } else {
      return false;
    }
  }

}

class CircleButton extends Button { 
  CircleButton(int ix, int iy, int isize, color icolor, color ihighlight) {
    x = ix;
    y = iy;
    size = isize;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
  }

  boolean pressed(int cursorX, int cursorY) {
    if( overCircle(cursorX, cursorY, x, y, size) ) {
      pressed = true;
      return true;
    } else {
      pressed = false;
      return false;
    }
  }

  void display() {
    stroke(255);
    fill(currentcolor);
    ellipse(x, y, size, size);
  }
}

class RectButton extends Button {
  RectButton(int ix, int iy, int isize, color icolor, color ihighlight) 
  {
    x = ix;
    y = iy;
    size = isize;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
  }

  boolean pressed(int cursorX, int cursorY) {
    if( overRect(cursorX, cursorY, x, y, size, size) ) {
      pressed = true;
      return true;
    } 
    else {
      pressed = false;
      return false;
    }
  }

  void display() {
    stroke(255);
    fill(currentcolor);
    rect(x, y, size, size);
  }
}

