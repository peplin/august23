// Wiremap CALIBRATOR for GLOBE
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

// March 12, 06

// Instructions - 
// The program loads in Calibration mode, where you have to calibrate the projector to the wiremap.
// "u" makes for less wires hit, while "i" calibrates for more wires hit.
// "j" adds empty space between each wire (if blurring occurs), "k" closes that empty space.
// "q" shrinks the width, "w" stretches it
// "n" nudges the image left, "m" right
// "t" brings the threshold foward, "r" backward


// Variables for Physical Wiremap

  float depth = 32.5;                        // distance from projector Mapline
  float map_length = 21.0;                   // mapline length, inches
  float hyp_unit = 1.0/8.0;                  // units, in inches, along hyp
  float map_unit = 1.0/4.0;                  // units, in inches, along mapline
  int wire = 85;                             // number of wires in contraption

// Variables for the Projector

  int wires_hit = 85;                        // how many wires the projector hits
  float sliver_ratio = .7;                 // ratio each sliver gets hit

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for Calibration

  boolean prestage = true;                      // switch to load the calibrator
  float threshold = 3.5;                     // red / blue wall threshold
  int r_nudge = 0;                           // how far the slivers nudge right, in pxls
  int width_stretcher = 0;                   // how many pxls the width should stretch

// Variables for slivers

  boolean loaded = false;                    // switch to load info only once
  int dot_height = 20;                       // height of dots in pxs



void setup() {
  size(1024, 768);
  background(255);
}

void draw() {
  noStroke();
}

void gen_calib(int i) {
  if(loaded == false) {
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
map[19] = 54.0;
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

    for(int j=0; j<wire; j++) {                         // calculates x and z coordinates of each wire
      float xmap = (0 - (map_length / 2)) + j*map_unit;
      float hyp = sqrt(sq(xmap) + sq(depth));
      z_by_ind[j] = depth*map[j]/hyp*hyp_unit;
      x_by_ind[j] = xmap - xmap*map[j]/hyp*hyp_unit;
    }
    loaded = true;                                    // only assign these values once
  }
  
  if(z_by_ind[i] > threshold) {
    fill(0, 0, 255);
  } else {
    fill(255,0,0);
  }
  rect((i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge, 0 , width / wires_hit * sliver_ratio, height);
}

void keyPressed() {                                           // adjust globe[] accordingly
   if (key == 'u') {
      wires_hit = wires_hit - 1;
    } else if (key == 'i') {
      if (wires_hit < wire) {
        wires_hit = wires_hit + 1;
      }
    } else if (key == 'j') {
      sliver_ratio = sliver_ratio - .05;
    } else if (key == 'k') {
      sliver_ratio = sliver_ratio + .05;
    } else if (key == 't') {
      threshold = threshold + .1;
    } else if (key == 'r') {
      threshold = threshold - .1;
    } else if (key == 'n') {
      r_nudge = r_nudge - 1;
    } else if (key == 'm') {
      r_nudge = r_nudge + 1;
    } else if (key == 'q') {
      width_stretcher = width_stretcher - 1;
    } else if (key == 'w') {
      width_stretcher = width_stretcher + 1;
    }
    fill(0);
    rect(0,0,width,height);  
    for(int i=round((wire - wires_hit)/2); i<round((wire-wires_hit)/2)+wires_hit; i = i+=1){
      gen_calib(i);
    }
    println("*********");
    println("Wires Hit = " + wires_hit);
    println("Sliver Ratio = " + sliver_ratio);
    println("Right Nudge = " + r_nudge);
    println("Width Stretcher = " + width_stretcher);
}

