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

// October 10, 06

// This program builds a 3d globe that bounces around in a 3d field.
// Eventually, I hope to program a gravity field that will work in any of the six cardinal directions.

// Variables for Physical Wiremap
// units in MILLIMETERS

  float depth = 2286.00;                     // distance from projector Mapline
  float map_length = 1219.00;                // mapline length
  float depth_unit = 4.7;                   // units, in inches, along hyp
  float map_unit = 4.7;               // units, in inches, along mapline
  int wire = 256;                            // number of wires in contraption

// Variables for the Projector

  int wires_hit = 256;                       // how many wires the projector hits
  float ppmm = 1.14;                                  // pixels per mm

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for the Shape to be rendered (a globe)

  float[] globe = new float[3];              // globe x,y,z coords
  float radius = 200.00;                     // globe radius

// Variables for slivers

  int dot_height = 5;                       // height of dots in pxs

// Varaibles for key input

  float step = 3;                           // how far the globe moves / button press

// Variables for 256

  int string_pix_count = 2;

// Variables for Bouncing
  int x_step = 6;
  int x_direction = 1;
  int y_step = 12;
  int y_direction = 1;
  int z_step = 30;
  int z_direction = 1;

void setup() {
  size(1024, 768);
  background(255);
  loader();
  globe[0]=0;
  globe[1]=600;
  globe[2]=2400;
}

void draw() {
  noStroke();
  frameRate(30);
  fill(0);
  rect(0,0,width,height);
  globe[0] = globe[0] + (x_step * x_direction);
  //globe[1] = 600 - (150 * sq(float(millis())/1000));
  globe[1] = globe[1] + (y_step * y_direction);
  globe[2] = globe[2] + (z_step * z_direction);
  if (globe[0] > 440 || globe[0] < -440) {
    x_direction *= -1;
  }
  if (globe[1] > 600 || globe[1] < 200) {
    y_direction *= -1;
  }
  if (globe[2] > 2800 || globe[2] < 2240) {
    z_direction *= -1;
  }
  
  gen_globe(globe[0], globe[1], globe[2], radius);
}

void gen_globe(float x, float y, float z, float rad) {
  for(int i=round((wire - wires_hit)/2); i<round((wire-wires_hit)/2)+wires_hit; i+=1) {
    if((x_by_ind[i] >= (x - rad)) && (x_by_ind[i] <= (x + rad))) {          // if a wire's x is +/- rad inches of globe's
      float local_hyp = sqrt(sq(x_by_ind[i] - x) + sq(z_by_ind[i] - z));    // find the distance
      if(local_hyp <= rad) {                                                              // if the distance from globe to wire's x/y is <radius
        float y_abs = sqrt(sq(rad) - sq(local_hyp));
        float y_top_coord = y + y_abs;
        float y_bot_coord = y - y_abs;                                             // calculate y's intersect
        float y_top_proj = y_top_coord * depth / z_by_ind[i];                      // compensate for projection morphing IN INCHES
        float y_bot_proj = y_bot_coord * depth / z_by_ind[i];
        float y_height_proj = y_top_proj - y_bot_proj;
        fill(255);                                                                    // draw a rectangle at that intersect

        // rect 1 is top dot for sliver
        float left1 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
        float top1 = (height/ppmm - y_top_proj) * ppmm;
        float wide1 = width / wires_hit;
        float tall1 = dot_height;
        rect(left1, top1, string_pix_count, tall1);                                                        // draw a rectangle at that intersect

        // rect 2 is bottom dot for sliver
        float left2 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
        float top2 = (height/ppmm - y_bot_proj) * ppmm - dot_height;
        float wide2 = width / wires_hit;
        float tall2 = dot_height;
        rect(left2, top2, string_pix_count, tall2);                                                        // draw a rectangle at that intersect

        // rect 3 is filler for sliver
        fill(0,0,255);
        float left3 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
        float top3 = (height/ppmm - y_top_proj) * ppmm + dot_height;
        float wide3 = width / wires_hit;
        float tall3 = y_height_proj * ppmm - (dot_height * 2);
        rect(left3, top3, string_pix_count, tall3);                                                        // draw a rectangle at that intersect
      }
    }
  }
}



void keyPressed() {                                           // adjust globe[] accordingly
      if (key == 'w') {
        globe[1] = globe[1] + step;
      } else if (key == 's') {
        globe[1] = globe[1] - step;
      } else if (key == 'e') {
        radius = radius + 5;
      } else if (key == 'd') {
        radius = radius - 5;
      }
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
