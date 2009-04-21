// Wiremap Renderer for GLOBE
// by


//              /      \
//       ______/________\______
//      /      \        /      \
//     /________\      /________\______
//    /                \         |     \
//   /   /      \       \_____   |      \
//      /________\______         |      /
//      \        /      \        |_____/
//       \      /________\
//              \          \     /
//               \_____      \ /
//                           / \
//                         /     \

// March 31, 06

// "a" and "z" mess with the depth of the globe.
// The arrow keys move the globe up, down, left, or right.
// "s" and "x" grown and shrink the globe

// note: all of the calibration functionality should still work



// Variables for Physical Wiremap
// units in MILLIMETERS

  float depth = 2286.00;                     // distance from projector Mapline
  float map_length = 1219.00;                // mapline length
  float depth_unit = 4.7;                   // units, in inches, along hyp
  float map_unit = 4.7;               // units, in inches, along mapline
  int wire = 256;                            // number of wires in contraption

// Variables for the Projector

  int wires_hit = 256;                       // how many wires the projector hits
  float ppmm = .84;                         // pixels per mm

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for the Shape to be rendered (a globe)

  float[] globe = new float[3];              // globe x,y,z coords
  float radius = 300.00;                     // globe radius

// Variables for Calibration




// Variables for slivers

  boolean loaded = false;                    // switch to load info only once
  int dot_height = 5;                       // height of dots in pxs

// Varaibles for key input

  float step = 3;                           // how far the globe moves / button press

// Variables for globe B

  boolean globe_a = true;
  float[] globe_b = new float[3];              // globe x,y,z coords
  boolean b_visible = false;
  float radius_b = 4;                          // globe radius

  int threshold = 3000-(192/4);


void setup() {
  size(1024, 768);
  background(255);
}

void draw() {
  noStroke();
  if (loaded == false) {
    loader();
    loaded = true;
  }
  globe[0] = (mouseY / float(height)) * (map_length + radius * 2) - (map_length / 2 + radius);
  globe[2] = depth + radius - (width - mouseX) / float(width) * (radius * 2 + 960);
  fill(0);
  rect(0,0,width,height);
  for(int i = 0; i < wire; i ++){
  if(map[i] > 63 && map[i] < 126) {
    fill(0, 255 ,0);
  } else if(map[i] > 126) {
    fill(255, 0, 0);
  } else {
    fill(0,0,255);
  }
  //fill(255);
  rect(i*4, 0, 2, height);
  //rect(i*4, height - 40, 2, 40);
  }
}

void mousePressed() { 
  noLoop(); 
} 
void loader() {
  String depths[] = loadStrings("../../depth256.txt");
  for(int i = 0; i < 256; i++) {
     map[i] =  Float.valueOf(depths[i]);
  }

  globe[0] = 0;                                    
  globe[1] = radius;
  globe[2] = 0;


  for(int j=0; j<wire; j++) {                         // calculates x and z coordinates of each wire
    float xmap = (0 - (map_length / 2)) + j*map_unit;
    float hyp = sqrt(sq(xmap) + sq(depth));
    z_by_ind[j] = depth - map[j]*5;
    x_by_ind[j] = xmap - xmap*map[j]/hyp*depth_unit;
  }
}

void gen_circle(int i)
{
  if((x_by_ind[i] >= (globe[0] - radius)) && (x_by_ind[i] <= (globe[0] + radius))) {          // if a wire's x is +/- radius inches of globe's
    float local_hyp = sqrt(sq(x_by_ind[i] - globe[0]) + sq(z_by_ind[i] - globe[2]));    // find the distance
    if(local_hyp <= radius) {                                                              // if the distance from globe to wire's x/y is <radius
      float y_abs = sqrt(sq(radius) - sq(local_hyp));
      float y_top_coord = globe[1] + y_abs;
      float y_bot_coord = globe[1] - y_abs;                                             // calculate y's intersect
      float y_top_proj = y_top_coord * depth / z_by_ind[i];                      // compensate for projection morphing IN INCHES
      float y_bot_proj = y_bot_coord * depth / z_by_ind[i];
      float y_height_proj = y_top_proj - y_bot_proj;
      fill(255);                                                                    // draw a rectangle at that intersect

      // rect 1 is top dot for sliver
      float left1 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
      float top1 = (height/ppmm - y_top_proj) * ppmm;
      float wide1 = width / wires_hit;
      float tall1 = dot_height;
      rect(left1, top1, 3, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
      float top2 = (height/ppmm - y_bot_proj) * ppmm - dot_height;
      float wide2 = width / wires_hit;
      float tall2 = dot_height;
      rect(left2, top2, 3, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0,0,255);
      float left3 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
      float top3 = (height/ppmm - y_top_proj) * ppmm + dot_height;
      float wide3 = width / wires_hit;
      float tall3 = y_height_proj * ppmm - (dot_height * 2);
      rect(left3, top3, 3, tall3);                                                        // draw a rectangle at that intersect
    }
  }
}


void keyPressed() {                                           // adjust globe[] accordingly
    if (globe_a == true) {
      if (key == 'w') {
        globe[1] = globe[1] + step;
      } else if (key == 's') {
        globe[1] = globe[1] - step;
      } else if (key == 'e') {
        radius = radius + .1;
      } else if (key == 'd') {
        radius = radius - .1;
      }
    } else {
      if (key == 'w') {
        globe_b[1] = globe_b[1] + step;
      } else if (key == 's') {
        globe_b[1] = globe_b[1] - step;
      } else if (key == 'e') {
        radius_b = radius_b + .1;
      } else if (key == 'd') {
        radius_b = radius_b - .1;
      }
    }
      
      
      if (key == 'b') {
        threshold = threshold +5;
      } else if (key == 'n') {
        threshold = threshold -5;
      }
}
