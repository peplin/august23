import processing.core.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class _1_02_calib extends PApplet {// Wiremap Renderer for GLOBE
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

  float depth = 2870.00f;                     // distance from projector Mapline
  float map_length = 1280.00f;                // mapline length
  float depth_unit = 5.00f;                   // units, in inches, along hyp
  float map_unit = 5.00f;                     // units, in inches, along mapline
  int wire = 256;                            // number of wires in contraption

// Variables for the Projector

  int wires_hit = 256;                       // how many wires the projector hits
  float ppmm = 0.80f;                         // pixels per mm

// Variables for to build the Map

  float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
  float[] x_by_ind = new float[wire];        // x coordinate for each wire
  float[] z_by_ind = new float[wire];        // z coordinate for each wire

// Variables for the Shape to be rendered (a globe)

  float[] globe = new float[3];              // globe x,y,z coords
  float radius = 300.00f;                     // globe radius

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


public void setup() {
  size(1024, 768);
  background(255);
}

public void draw() {
  noStroke();
  if (loaded == false) {
    loader();
    loaded = true;
  }
  globe[0] = (mouseY / PApplet.toFloat(height)) * (map_length + radius * 2) - (map_length / 2 + radius);
  globe[2] = depth + radius - (width - mouseX) / PApplet.toFloat(width) * (radius * 2 + 960);
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
  rect(i*4, 0, 2, height);
  //rect(i*4, height - 40, 2, 40);
  }
}

public void mousePressed() { 
  noLoop(); 
} 
public void loader() {

map[0] = 90;
map[1] = 59;
map[2] = 118;
map[3] = 129;
map[4] = 173;
map[5] = 33;
map[6] = 56;
map[7] = 79;
map[8] = 53;
map[9] = 38;
map[10] = 28;
map[11] = 147;
map[12] = 117;
map[13] = 70;
map[14] = 105;
map[15] = 186;
map[16] = 179;
map[17] = 88;
map[18] = 34;
map[19] = 75;
map[20] = 107;
map[21] = 182;
map[22] = 185;
map[23] = 47;
map[24] = 70;
map[25] = 186;
map[26] = 123;
map[27] = 169;
map[28] = 12;
map[29] = 140;
map[30] = 164;
map[31] = 82;
map[32] = 72;
map[33] = 148;
map[34] = 78;
map[35] = 16;
map[36] = 122;
map[37] = 54;
map[38] = 7;
map[39] = 113;
map[40] = 148;
map[41] = 178;
map[42] = 40;
map[43] = 6;
map[44] = 19;
map[45] = 141;
map[46] = 162;
map[47] = 141;
map[48] = 107;
map[49] = 22;
map[50] = 174;
map[51] = 88;
map[52] = 18;
map[53] = 149;
map[54] = 43;
map[55] = 40;
map[56] = 16;
map[57] = 82;
map[58] = 85;
map[59] = 38;
map[60] = 60;
map[61] = 92;
map[62] = 132;
map[63] = 129;
map[64] = 168;
map[65] = 20;
map[66] = 173;
map[67] = 33;
map[68] = 150;
map[69] = 20;
map[70] = 154;
map[71] = 91;
map[72] = 85;
map[73] = 7;
map[74] = 22;
map[75] = 79;
map[76] = 148;
map[77] = 90;
map[78] = 142;
map[79] = 82;
map[80] = 151;
map[81] = 73;
map[82] = 29;
map[83] = 38;
map[84] = 4;
map[85] = 171;
map[86] = 189;
map[87] = 83;
map[88] = 16;
map[89] = 59;
map[90] = 134;
map[91] = 96;
map[92] = 69;
map[93] = 128;
map[94] = 169;
map[95] = 23;
map[96] = 159;
map[97] = 24;
map[98] = 157;
map[99] = 154;
map[100] = 167;
map[101] = 52;
map[102] = 112;
map[103] = 6;
map[104] = 135;
map[105] = 142;
map[106] = 87;
map[107] = 65;
map[108] = 177;
map[109] = 183;
map[110] = 19;
map[111] = 26;
map[112] = 58;
map[113] = 30;
map[114] = 45;
map[115] = 44;
map[116] = 83;
map[117] = 85;
map[118] = 0;
map[119] = 16;
map[120] = 141;
map[121] = 155;
map[122] = 83;
map[123] = 68;
map[124] = 123;
map[125] = 108;
map[126] = 132;
map[127] = 29;
map[128] = 15;
map[129] = 43;
map[130] = 52;
map[131] = 130;
map[132] = 16;
map[133] = 49;
map[134] = 93;
map[135] = 23;
map[136] = 82;
map[137] = 47;
map[138] = 76;
map[139] = 168;
map[140] = 67;
map[141] = 27;
map[142] = 191;
map[143] = 81;
map[144] = 144;
map[145] = 71;
map[146] = 24;
map[147] = 123;
map[148] = 22;
map[149] = 78;
map[150] = 69;
map[151] = 108;
map[152] = 141;
map[153] = 151;
map[154] = 65;
map[155] = 85;
map[156] = 39;
map[157] = 43;
map[158] = 145;
map[159] = 37;
map[160] = 181;
map[161] = 84;
map[162] = 78;
map[163] = 117;
map[164] = 63;
map[165] = 50;
map[166] = 158;
map[167] = 150;
map[168] = 17;
map[169] = 75;
map[170] = 57;
map[171] = 149;
map[172] = 5;
map[173] = 148;
map[174] = 175;
map[175] = 42;
map[176] = 33;
map[177] = 179;
map[178] = 146;
map[179] = 134;
map[180] = 120;
map[181] = 68;
map[182] = 5;
map[183] = 19;
map[184] = 6;
map[185] = 100;
map[186] = 162;
map[187] = 138;
map[188] = 4;
map[189] = 43;
map[190] = 99;
map[191] = 66;
map[192] = 7;
map[193] = 62;
map[194] = 27;
map[195] = 177;
map[196] = 83;
map[197] = 154;
map[198] = 102;
map[199] = 162;
map[200] = 46;
map[201] = 172;
map[202] = 132;
map[203] = 38;
map[204] = 98;
map[205] = 140;
map[206] = 42;
map[207] = 57;
map[208] = 165;
map[209] = 92;
map[210] = 160;
map[211] = 72;
map[212] = 14;
map[213] = 140;
map[214] = 182;
map[215] = 79;
map[216] = 91;
map[217] = 135;
map[218] = 45;
map[219] = 177;
map[220] = 159;
map[221] = 51;
map[222] = 63;
map[223] = 98;
map[224] = 45;
map[225] = 84;
map[226] = 25;
map[227] = 148;
map[228] = 8;
map[229] = 134;
map[230] = 116;
map[231] = 58;
map[232] = 151;
map[233] = 171;
map[234] = 111;
map[235] = 101;
map[236] = 119;
map[237] = 138;
map[238] = 20;
map[239] = 14;
map[240] = 47;
map[241] = 66;
map[242] = 188;
map[243] = 4;
map[244] = 110;
map[245] = 24;
map[246] = 3;
map[247] = 52;
map[248] = 31;
map[249] = 95;
map[250] = 19;
map[251] = 72;
map[252] = 125;
map[253] = 79;
map[254] = 55;
map[255] = 185;


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

public void gen_circle(int i)
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


public void keyPressed() {                                           // adjust globe[] accordingly
    if (globe_a == true) {
      if (key == 'w') {
        globe[1] = globe[1] + step;
      } else if (key == 's') {
        globe[1] = globe[1] - step;
      } else if (key == 'e') {
        radius = radius + .1f;
      } else if (key == 'd') {
        radius = radius - .1f;
      }
    } else {
      if (key == 'w') {
        globe_b[1] = globe_b[1] + step;
      } else if (key == 's') {
        globe_b[1] = globe_b[1] - step;
      } else if (key == 'e') {
        radius_b = radius_b + .1f;
      } else if (key == 'd') {
        radius_b = radius_b - .1f;
      }
    }
      
      
      if (key == 'b') {
        threshold = threshold +5;
      } else if (key == 'n') {
        threshold = threshold -5;
      }
}
static public void main(String args[]) {   PApplet.main(new String[] { "_1_02_calib" });}}