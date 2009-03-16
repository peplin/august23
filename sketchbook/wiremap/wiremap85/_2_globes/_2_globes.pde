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
// "b" switches between the globes
// note: all of the calibration functionality should still work



// Variables for Physical Wiremap

  float depth = 32.5;                        // distance from projector Mapline
  float map_length = 21.0;                   // mapline length, inches
  float hyp_unit = 1.0/8.0;                  // units, in inches, along hyp
  float map_unit = 1.0/4.0;                  // units, in inches, along mapline
  int wire = 85;                             // number of wires in contraption

// Variables for the Projector

  int wires_hit = 85;                        // how many wires the projector hits
  float ppi = 48;                              // pixels per inch
  float sliver_ratio = 0.85;                 // ratio each sliver gets hit

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for the Shape to be rendered (a globe)

  float[] globe = new float[3];              // globe x,y,z coords
  float radius = 7;                          // globe radius

// Variables for Calibration

  int r_nudge = 2;                           // how far the slivers nudge right, in pxls
  int width_stretcher = 0;                   // how many pxls the width should stretch

// Variables for slivers

  boolean loaded = false;                    // switch to load info only once
  int dot_height = 5;                       // height of dots in pxs

// Varaibles for key input

  float step = .3;                           // how far the globe moves / button press

// Variables for globe B

  boolean globe_a = true;
  float[] globe_b = new float[3];              // globe x,y,z coords
  boolean b_visible = false;
  float radius_b = 4;                          // globe radius




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
  if (globe_a == true) {
  globe[0] = (mouseY / float(height)) * (map_length + radius * 2) - (map_length / 2 + radius);
  globe[2] = depth + radius - (width - mouseX) / float(width) * (16.0 + radius + 2);
  } else {
  globe_b[0] = (mouseY / float(height)) * (map_length + radius * 2) - (map_length / 2 + radius);
  globe_b[2] = depth + radius - (width - mouseX) / float(width) * (16.0 + radius + 2);
  }
  fill(0);
  rect(0,0,width,height);                                        // generate slivers only for hit wires
  for(int i=round((wire - wires_hit)/2); i<round((wire-wires_hit)/2)+wires_hit; i+=1){
    gen_circle(i);
    if (b_visible == true) {
      gen_circle_b(i);
    }
  }
}

void loader() {
map[0] = 116.0;
map[1] = 49.0;
map[2] = 111.0;
map[3] = 87.0;
map[4] = 47.0;
map[5] = 25.0;
map[6] = 7.0;
map[7] = 32.0;
map[8] = 112.0;
map[9] = 13.0;
map[10] = 18.0;
map[11] = 107.0;
map[12] = 80.0;
map[13] = 84.0;
map[14] = 27.0;
map[15] = 64.0;
map[16] = 75.0;
map[17] = 34.0;
map[18] = 63.0;
map[19] = 54.0 ;
map[20] = 12.0;
map[21] = 47.0;
map[22] = 93.0;
map[23] = 92.0;
map[24] = 71.0;
map[25] = 58.0;
map[26] = 6.0;
map[27] = 77.0;
map[28] = 62.0;
map[29] = 31.0;
map[30] = 83.0;
map[31] = 15.0;
map[32] = 52.0;
map[33] = 83.0;
map[34] = 24.0;
map[35] = 38.0;
map[36] = 31.0;
map[37] = 54.0;
map[38] = 99.0;
map[39] = 45.0;
map[40] = 2.0;
map[41] = 76.0;
map[42] = 68.0;
map[43] = 71.0;
map[44] = 84.0;
map[45] = 4.0;
map[46] = 72.0;
map[47] = 97.0;
map[48] = 28.0;
map[49] = 76.0;
map[50] = 96.0;
map[51] = 49.0;
map[52] = 2.0;
map[53] = 109.0;
map[54] = 93.0;
map[55] = 0.0;
map[56] = 37.0;
map[57] = 65.0;
map[58] = 69.0;
map[59] = 90.0;
map[60] = 35.0;
map[61] = 80.0;
map[62] = 43.0;
map[63] = 62.0;
map[64] = 19.0;
map[65] = 74.0;
map[66] = 20.0;
map[67] = 42.0;
map[68] = 30.0;
map[69] = 69.0;
map[70] = 30.0;
map[71] = 3.0;
map[72] = 31.0;
map[73] = 25.0;
map[74] = 10.0;
map[75] = 76.0;
map[76] = 71.0;
map[77] = 12.0;
map[78] = 80.0;
map[79] = 99.0;
map[80] = 100.0;
map[81] = 2.0;
map[82] = 52.0;
map[83] = 79.0;
map[84] = 3.0;

  globe[0] = 0;                                    
  globe[1] = 4;
  globe[2] = depth - radius;
  globe_b[0] = 0;                                    
  globe_b[1] = 4;
  globe_b[2] = depth - radius_b;

  for(int j=0; j<wire; j++) {                         // calculates x and z coordinates of each wire
    float xmap = (0 - (map_length / 2)) + j*map_unit;
    float hyp = sqrt(sq(xmap) + sq(depth));
    z_by_ind[j] = depth - (depth*map[j]/hyp*hyp_unit);
    x_by_ind[j] = xmap - xmap*map[j]/hyp*hyp_unit;
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
      float left1 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top1 = (height/ppi - y_top_proj) * ppi;
      float wide1 = width / wires_hit * sliver_ratio;
      float tall1 = dot_height;
      rect(left1, top1, wide1, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top2 = (height/ppi - y_bot_proj) * ppi - dot_height;
      float wide2 = width / wires_hit * sliver_ratio;
      float tall2 = dot_height;
      rect(left2, top2, wide2, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0,0,255);
      float left3 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top3 = (height/ppi - y_top_proj) * ppi + dot_height;
      float wide3 = width / wires_hit * sliver_ratio;
      float tall3 = y_height_proj * ppi - (dot_height * 2);
      rect(left3, top3, wide3, tall3);                                                        // draw a rectangle at that intersect
    }
  }
}

void gen_circle_b(int i)
{
  if((x_by_ind[i] >= (globe_b[0] - radius_b)) && (x_by_ind[i] <= (globe_b[0] + radius_b))) {          // if a wire's x is +/- radius inches of globe's
    float local_hyp = sqrt(sq(x_by_ind[i] - globe_b[0]) + sq(z_by_ind[i] - globe_b[2]));    // find the distance
    if(local_hyp <= radius_b) {                                                              // if the distance from globe to wire's x/y is <radius
      float y_abs = sqrt(sq(radius_b) - sq(local_hyp));
      float y_top_coord = globe_b[1] + y_abs;
      float y_bot_coord = globe_b[1] - y_abs;                                             // calculate y's intersect
      float y_top_proj = y_top_coord * depth / z_by_ind[i];                      // compensate for projection morphing IN INCHES
      float y_bot_proj = y_bot_coord * depth / z_by_ind[i];
      float y_height_proj = y_top_proj - y_bot_proj;
      fill(255);                                                                    // draw a rectangle at that intersect

      // rect 1 is top dot for sliver
      float left1 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top1 = (height/ppi - y_top_proj) * ppi;
      float wide1 = width / wires_hit * sliver_ratio;
      float tall1 = dot_height;
      rect(left1, top1, wide1, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top2 = (height/ppi - y_bot_proj) * ppi - dot_height;
      float wide2 = width / wires_hit * sliver_ratio;
      float tall2 = dot_height;
      rect(left2, top2, wide2, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0);
      float left3 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top3 = (height/ppi - y_top_proj) * ppi + dot_height;
      float wide3 = width / wires_hit * sliver_ratio;
      float tall3 = y_height_proj * ppi - (dot_height * 2);
      rect(left3, top3, wide3, tall3);                                                        // draw a rectangle at that intersect
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
        if (b_visible == true) {
        if (globe_a == true) {
          globe_a = false;
        } else {
          globe_a = true;
        }
        }
      
      } else if (key == 'n') {
        if (b_visible == true) {
          globe_a = true;
          b_visible = false;
        } else {
          b_visible = true;
        }
      
      }
      
      
      else if (key == 'o') {
        sliver_ratio = sliver_ratio - .05;
      } else if (key == 'p') {
        sliver_ratio = sliver_ratio + .05;
      } else if (key == 'j') {
        r_nudge = r_nudge + 1;
      } else if (key == 'k') {
        r_nudge = r_nudge - 1;
      }
}
