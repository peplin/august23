//  Fullscreen stuff:

import processing.opengl.*;

static public void main(String args[]) {
  PApplet.main(new String[] { "--present", "calib_96" });
}


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
// units in INCHES

  float depth = 70.0;                     // distance from projector Mapline
  float map_length = 30.0;                // mapline length
  float depth_unit = 0.3125;                   // units, in inches, along hyp
  float map_unit = 0.3125;                     // units, in inches, along mapline
  int wire = 96;                            // number of wires in contraption
  float depth_levels = 64;                     // number of depth levels
  float depth_levels_third = depth_levels / 3; //

// Variables for the Projector

  int wires_hit = 128;                       // how many wires the projector hits
  float ppi = 8.0;                         // pixels per inch

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
  size(screen.width, screen.height, OPENGL);
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
  if(map[i] > depth_levels_third && map[i] < depth_levels_third * 2) {
    fill(255, 0 ,0);
  } else if(map[i] > (depth_levels_third * 2)) {
    fill(0,255,0);
  } else {
    fill(0, 0, 255);
  }
  rect(i * width / wire, 0, 2, height);
  //rect(i*4, height - 40, 2, 40);
  }
}

void mousePressed() { 
  noLoop(); 
} 
void loader() {

  map[0] = 7;
map[1] = 56;
map[2] = 3;
map[3] = 18;
map[4] = 50;
map[5] = 34;
map[6] = 22;
map[7] = 13;
map[8] = 52;
map[9] = 10;
map[10] = 40;
map[11] = 20;
map[12] = 58;
map[13] = 42;
map[14] = 1;
map[15] = 26;
map[16] = 4;
map[17] = 48;
map[18] = 19;
map[19] = 40;
map[20] = 34;
map[21] = 45;
map[22] = 22;
map[23] = 24;
map[24] = 45;
map[25] = 53;
map[26] = 39;
map[27] = 54;
map[28] = 28;
map[29] = 1;
map[30] = 9;
map[31] = 55;
map[32] = 38;
map[33] = 6;
map[34] = 12;
map[35] = 9;
map[36] = 17;
map[37] = 19;
map[38] = 62;
map[39] = 57;
map[40] = 30;
map[41] = 42;
map[42] = 33;
map[43] = 5;
map[44] = 26;
map[45] = 15;
map[46] = 17;
map[47] = 46;
map[48] = 63;
map[49] = 59;
map[50] = 16;
map[51] = 25;
map[52] = 41;
map[53] = 37;
map[54] = 44;
map[55] = 61;
map[56] = 2;
map[57] = 53;
map[58] = 0;
map[59] = 29;
map[60] = 38;
map[61] = 50;
map[62] = 43;
map[63] = 48;
map[64] = 5;
map[65] = 23;
map[66] = 8;
map[67] = 2;
map[68] = 31;
map[69] = 12;
map[70] = 32;
map[71] = 60;
map[72] = 0;
map[73] = 28;
map[74] = 56;
map[75] = 51;
map[76] = 8;
map[77] = 14;
map[78] = 35;
map[79] = 27;
map[80] = 11;
map[81] = 21;
map[82] = 47;
map[83] = 30;
map[84] = 60;
map[85] = 49;
map[86] = 14;
map[87] = 6;
map[88] = 11;
map[89] = 16;
map[90] = 36;
map[91] = 24;
map[92] = 4;
map[93] = 32;
map[94] = 36;
map[95] = 20;

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
      float top1 = (height/ppi - y_top_proj) * ppi;
      float wide1 = width / wires_hit;
      float tall1 = dot_height;
      rect(left1, top1, 3, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
      float top2 = (height/ppi - y_bot_proj) * ppi - dot_height;
      float wide2 = width / wires_hit;
      float tall2 = dot_height;
      rect(left2, top2, 3, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0,0,255);
      float left3 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
      float top3 = (height/ppi - y_top_proj) * ppi + dot_height;
      float wide3 = width / wires_hit;
      float tall3 = y_height_proj * ppi - (dot_height * 2);
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
