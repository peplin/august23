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
                      // pixels per mm

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for the Shape to be rendered (a globe)

  float[] globe = new float[3];              // globe x,y,z coords
  float radius = 300.00;                     // globe radius

// Variables for slivers

  int dot_height = 5;                       // height of dots in pxs

// Varaibles for key input

  float step = 3;                           // how far the globe moves / button press

// Variables for 256

  int string_pix_count = 2;

// Variables for Glow

  int colorval = 128;
  int divs = 8;

void setup() {
  size(1024, 768);
  background(255);
  loader();
  globe[0]=0;
  globe[1]=400;
  globe[2]=2400;
}

void draw() {
  noStroke();
  frameRate(30);
  fill(0);
  rect(0,0,width,height);
  globe[0] = (mouseX / float(width)) * (map_length + radius * 2) - (map_length / 2 + radius);
  globe[2] = depth + radius - (mouseY) / float(height) * (radius * 2 + 960);
  for(int n=0; n<divs; n++) {
    float subrad = (radius - ((n) * radius / divs));
    colorval = n * 255 / divs;
    gen_globe(globe[0], globe[1], globe[2], subrad, colorval);
    }

}

void gen_globe(float x, float y, float z, float rad, int cv) {
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


        // rect 3 is filler for sliver
        fill(255, cv, 0);
        float left3 = (i-round((wire-wires_hit)/2)) * (width) / wires_hit;
        float top3 = (height/ppmm - y_top_proj) * ppmm;
        float wide3 = width / wires_hit;
        float tall3 = y_height_proj * ppmm;
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
