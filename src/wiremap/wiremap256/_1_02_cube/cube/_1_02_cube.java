import processing.core.*; import java.applet.*; import java.awt.*; import java.awt.image.*; import java.awt.event.*; import java.io.*; import java.net.*; import java.text.*; import java.util.*; import java.util.zip.*; public class _1_02_cube extends PApplet {// RECTANGULAR SOLID builder


// VARIABLES FOR CUBE AND CORNER POSITIONS
float[] cube_pos = new float[3];        // x y z
float[] cube_dim = new float[3];        // x y z (width, height, depth)
float[][] corner = new float[8][3];           // 8 corners, 3 coordinates

// METHOD variables

boolean loaded = false;
boolean gt1 = false;
boolean gt3 = false;
boolean gt5 = false;
int bot_corner = 0;  // this is an integer that renders the index (from 0 - 7) that corresponds to the bottom point.
int[] clock = new int[8];
boolean lt_x = false;
boolean gt_x = true;
  int dot_height = 5;                       // height of dots in pxs


// Variables for Physical Wiremap
// 09.12.06 - edited from wiremap 1.01... all the units are in mm, not inches!

float depth = 3000.00f;                        // distance from projector Mapline
float map_length = 1280.00f;                   // mapline length, mm
float depth_unit = 5.00f;                  // units, in inches, along hyp
float map_unit = 5.00f;                  // units, in inches, along mapline
int wire = 256;                             // number of wires in contraption

// Variables for the Projector

int wires_hit = 256;                        // how many wires the projector hits
float ppmm = 0.800f;                              // pixels per mm

// Variables to build the Map

float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
float[] x_by_ind = new float[wire];        // x coordinate for each wire
float[] z_by_ind = new float[wire];        // z coordinate for each wire
float[] y_bot_by_ind = new float[wire];
float[] y_top_by_ind = new float[wire];
float[] y_bot_proj = new float[wire];
float[] y_top_proj = new float[wire];


// Calib variables



public void setup() {
  size(1024, 768);
  background(0);
}

public void draw() {
  if (loaded == false) {
    load();
    loaded = true;
  }
  fill(0);
  stroke(255);
}

public void load() {  // cube position, cube dimensions, corner dimensions

  cube_pos[0] = 0;
  cube_pos[1] = 400;
  cube_pos[2] = depth - 400;

  cube_dim[0] = 250;
  cube_dim[1] = 250;
  cube_dim[2] = 250;

  corner[0][0] = -cube_dim[0] + cube_pos[0];
  corner[0][1] = -cube_dim[1] + cube_pos[1];
  corner[0][2] = -cube_dim[2] + cube_pos[2];

  corner[1][0] = -cube_dim[0] + cube_pos[0];
  corner[1][1] = -cube_dim[1] + cube_pos[1];
  corner[1][2] = cube_dim[2] + cube_pos[2];

  corner[2][0] = -cube_dim[0] + cube_pos[0];
  corner[2][1] = cube_dim[1] + cube_pos[1];
  corner[2][2] = -cube_dim[2] + cube_pos[2];

  corner[3][0] = -cube_dim[0] + cube_pos[0];
  corner[3][1] = cube_dim[1] + cube_pos[1];
  corner[3][2] = cube_dim[2] + cube_pos[2];

  corner[4][0] = cube_dim[0] + cube_pos[0];
  corner[4][1] = -cube_dim[1] + cube_pos[1];
  corner[4][2] = -cube_dim[2] + cube_pos[2];

  corner[5][0] = cube_dim[0] + cube_pos[0];
  corner[5][1] = -cube_dim[1] + cube_pos[1];
  corner[5][2] = cube_dim[2] + cube_pos[2];

  corner[6][0] = cube_dim[0] + cube_pos[0];
  corner[6][1] = cube_dim[1] + cube_pos[1];
  corner[6][2] = -cube_dim[2] + cube_pos[2];

  corner[7][0] = cube_dim[0] + cube_pos[0];
  corner[7][1] = cube_dim[1] + cube_pos[1];
  corner[7][2] = cube_dim[2] + cube_pos[2];


  // map to set up z & x_by_ind[]

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

  for(int j=0; j<wire; j++) {                         // calculates x and z coordinates of each wire
    float xmap = (0 - (map_length / 2)) + j*map_unit;
    float hyp = sqrt(sq(xmap) + sq(depth));
    z_by_ind[j] = depth - map[j]*5;
    x_by_ind[j] = xmap - xmap*map[j]/hyp*depth_unit;
  }
  pitch(.1f);
  yaw(.1f);
  roll(.1f);
}


public void pitch(float rad)
{
  for (int i = 0; i < 8; i ++ )
  {
    float z_dist = corner[i][2] - cube_pos[2];
    float y_dist = corner[i][1] - cube_pos[1];    
    float hyp = sqrt(sq(z_dist) + sq(y_dist));
    float theta = atan2(z_dist, y_dist);
    theta = (theta + (rad));
    corner[i][1] = cube_pos[1] + hyp * cos(theta);
    corner[i][2] = cube_pos[2] + hyp * sin(theta);
  }
}

public void yaw(float rad)
{
  for (int i = 0; i < 8; i ++ )
  {
    float x_dist = corner[i][0] - cube_pos[0];
    float z_dist = corner[i][2] - cube_pos[2];
    float hyp = sqrt(sq(x_dist) + sq(z_dist));
    float theta = atan2(x_dist, z_dist);
    theta = (theta + (rad));
    corner[i][2] = cube_pos[2] + hyp * cos(theta);
    corner[i][0] = cube_pos[0] + hyp * sin(theta);
  }
}

public void roll(float rad)
{
  for (int i = 0; i < 8; i ++ )
  {
    float x_dist = corner[i][0] - cube_pos[0];
    float y_dist = corner[i][1] - cube_pos[1];
    float hyp = sqrt(sq(x_dist) + sq(y_dist));
    float theta = atan2(y_dist, x_dist);
    theta = (theta + (rad));
    corner[i][0] = cube_pos[0] + hyp * cos(theta);
    corner[i][1] = cube_pos[1] + hyp * sin(theta);
  }
}


public void find_bottom_corner() {
  for (int ind = 0; ind < 8; ind ++ )
  {
    int counter = 0;
    for (int c_ind = 0; c_ind < 8; c_ind ++)
    {
      if ( corner[ind][1] > corner[c_ind][1] )
      {
        break;
      }
      else
      {
        counter = counter + 1;
      }
    }
    if (counter == 8)
    {
      bot_corner = ind;
      break;
    }
  }
}

//###########################----- this calculates TOP and BOTTOM for each wire -----##############

public void wire_check(int this_wire)
{
  switch(bot_corner)
  {
  case 0:
    clock[0] = 0;
    clock[1] = 1;
    clock[2] = 3;
    clock[3] = 2;
    clock[4] = 6;
    clock[5] = 4;
    clock[6] = 5;
    clock[7] = 7;
    break;
  case 1:
    clock[0] = 1;
    clock[1] = 0;
    clock[2] = 4;
    clock[3] = 5;
    clock[4] = 7;
    clock[5] = 3;
    clock[6] = 2;
    clock[7] = 6;
    break;
  case 2:
    clock[0] = 2;
    clock[1] = 0;
    clock[2] = 1;
    clock[3] = 3;
    clock[4] = 7;
    clock[5] = 6;
    clock[6] = 4;
    clock[7] = 5;
    break;
  case 3:
    clock[0] = 3;
    clock[1] = 1;
    clock[2] = 5;
    clock[3] = 7;
    clock[4] = 6;
    clock[5] = 2;
    clock[6] = 0;
    clock[7] = 4;
    break;
  case 4:
    clock[0] = 4;
    clock[1] = 0;
    clock[2] = 2;
    clock[3] = 6;
    clock[4] = 7;
    clock[5] = 5;
    clock[6] = 1;
    clock[7] = 3;
    break;
  case 5:
    clock[0] = 5;
    clock[1] = 1;
    clock[2] = 0;
    clock[3] = 4;
    clock[4] = 6;
    clock[5] = 7;
    clock[6] = 3;
    clock[7] = 2;
    break;
  case 6:
    clock[0] = 6;
    clock[1] = 2;
    clock[2] = 3;
    clock[3] = 7;
    clock[4] = 5;
    clock[5] = 4;
    clock[6] = 0;
    clock[7] = 1;
    break;
  case 7:
    clock[0] = 7;
    clock[1] = 3;
    clock[2] = 1;
    clock[3] = 5;
    clock[4] = 4;
    clock[5] = 6;
    clock[6] = 2;
    clock[7] = 0;
    break;
  } // end switch

  if (corner[clock[0]][2] > corner[clock[1]][2]) {
    gt1 = true;
  }
  else {
    gt1 = false;
  }

  if (corner[clock[0]][2] > corner[clock[3]][2]) {
    gt3 = true;
  }
  else {
    gt3 = false;
  }

  if (corner[clock[0]][2] > corner[clock[5]][2]) {
    gt5 = true;
  }
  else {
    gt5 = false;
  }

  if ((gt1 == true) && (gt3 == true)) {
    less_than_x(this_wire, 2, 3, 4, 5);
    greater_than_x(this_wire, 5, 6, 1, 2);
    if ((gt_x == true) && (lt_x == true)) {
                 // executes appropriate find_y_bot function.
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[3]][0]) / (corner[clock[0]][2] - corner[clock[3]][2])) * (z_by_ind[this_wire] - corner[clock[3]][2]) + corner[clock[3]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[5]][0] - corner[clock[0]][0]) / (corner[clock[5]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 3, 5);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
         find_y_bot(this_wire, 0, 1, 5);
      }
      else {
        find_y_bot(this_wire, 0, 1, 3);
      }
                 // executes appropriate find_y_top function.
                 
      if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[4]][0]) / (corner[clock[7]][2] - corner[clock[4]][2])) * (z_by_ind[this_wire] - corner[clock[4]][2]) + corner[clock[4]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
          find_y_top(this_wire, 7, 2, 4);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 6);
      }
      else {
        find_y_top(this_wire, 7, 4, 6);
      }
    }
  }
  else if (( gt1 == true ) && ( gt5 == true)) {
    less_than_x(this_wire, 6, 1, 2, 3);
    greater_than_x(this_wire, 3, 4, 5, 6);

    if ((gt_x == true) && (lt_x == true)) {
                       // executes appropriate find_y_bot function.
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[3]][0]) / (corner[clock[0]][2] - corner[clock[3]][2])) * (z_by_ind[this_wire] - corner[clock[3]][2]) + corner[clock[3]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 3);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[3]][0] - corner[clock[0]][0]) / (corner[clock[3]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 3, 5);
      }
      else {
        find_y_bot(this_wire, 0, 1, 5);
      }
      
                       // executes appropriate find_y_top function.
      if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 6);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[4]][0] - corner[clock[7]][0]) / (corner[clock[4]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 4, 6);
      }
      else {
        find_y_top(this_wire, 7, 2, 4);
      }
      
    }

  }
  else if (( gt3 == true ) && ( gt5 == true)) {
    less_than_x(this_wire, 4, 5, 6, 1);
    greater_than_x(this_wire, 1, 2, 3, 4);

    if ((gt_x == true) && (lt_x == true)) {
                       // executes appropriate find_y_bot function.
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 5);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[1]][0]) / (corner[clock[0]][2] - corner[clock[1]][2])) * (z_by_ind[this_wire] - corner[clock[1]][2]) + corner[clock[1]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[3]][0] - corner[clock[0]][0]) / (corner[clock[3]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 3);
      }
      else {
        find_y_bot(this_wire, 0, 3, 5);
      }
                // executes appropriate find_y_top function.
      if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[4]][0]) / (corner[clock[7]][2] - corner[clock[4]][2])) * (z_by_ind[this_wire] - corner[clock[4]][2]) + corner[clock[4]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[6]][0] - corner[clock[7]][0]) / (corner[clock[6]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 4, 6);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[4]][0]) / (corner[clock[7]][2] - corner[clock[4]][2])) * (z_by_ind[this_wire] - corner[clock[4]][2]) + corner[clock[4]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
         find_y_top(this_wire, 7, 2, 4);
      }
      else {
        find_y_top(this_wire, 7, 2, 6);
      }
    }
  }
  else if (( gt1 == false ) && ( gt3 == false)) {
    less_than_x(this_wire, 5, 6, 1, 2);
    greater_than_x(this_wire, 2, 3, 4, 5);

    if ((gt_x == true) && (lt_x == true)) {
                       // executes appropriate find_y_bot function.      
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 5);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[3]][0] - corner[clock[0]][0]) / (corner[clock[3]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 3, 5);
      }
      else {
        find_y_bot(this_wire, 0, 1, 3);
      }
                       // executes appropriate find_y_top function.      
      if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 6);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[2]][0]) / (corner[clock[7]][2] - corner[clock[2]][2])) * (z_by_ind[this_wire] - corner[clock[2]][2]) + corner[clock[2]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[4]][0] - corner[clock[7]][0]) / (corner[clock[4]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 4);
      }
      else {
        find_y_top(this_wire, 7, 4, 6);
      }  
    }



  }
  else if (( gt1 == false ) && ( gt5 == false)) {
    less_than_x(this_wire, 3, 4, 5, 6);
    greater_than_x(this_wire, 6, 1, 2, 3);


    if ((gt_x == true) && (lt_x == true)) {
                             // executes appropriate find_y_bot function.      
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[3]][0]) / (corner[clock[0]][2] - corner[clock[3]][2])) * (z_by_ind[this_wire] - corner[clock[3]][2]) + corner[clock[3]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[5]][0] - corner[clock[0]][0]) / (corner[clock[5]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 3, 5);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[1]][0]) / (corner[clock[0]][2] - corner[clock[1]][2])) * (z_by_ind[this_wire] - corner[clock[1]][2]) + corner[clock[1]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[3]][0] - corner[clock[0]][0]) / (corner[clock[3]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 3);
      }
      else {
        find_y_bot(this_wire, 0, 1, 5);
      }
                             // executes appropriate find_y_top function.      
      if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[4]][0]) / (corner[clock[7]][2] - corner[clock[4]][2])) * (z_by_ind[this_wire] - corner[clock[4]][2]) + corner[clock[4]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[6]][0] - corner[clock[7]][0]) / (corner[clock[6]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 4, 6);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 6);
      }
      else {
        find_y_top(this_wire, 7, 2, 4);
      }
      
      
      
    }



  }
  else if (( gt3 == false ) && ( gt5 == false)) {
    less_than_x(this_wire, 1, 2, 3, 4);
    greater_than_x(this_wire, 4, 5, 6, 1);

    if ((gt_x == true) && (lt_x == true)) {
                                   // executes appropriate find_y_bot function.      
      if ((x_by_ind[this_wire] > ((corner[clock[0]][0] - corner[clock[3]][0]) / (corner[clock[0]][2] - corner[clock[3]][2])) * (z_by_ind[this_wire] - corner[clock[3]][2]) + corner[clock[3]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 3);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[0]][0] - corner[clock[5]][0]) / (corner[clock[0]][2] - corner[clock[5]][2])) * (z_by_ind[this_wire] - corner[clock[5]][2]) + corner[clock[5]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[1]][0] - corner[clock[0]][0]) / (corner[clock[1]][2] - corner[clock[0]][2])) * (z_by_ind[this_wire] - corner[clock[0]][2]) + corner[clock[0]][0])) {
        find_y_bot(this_wire, 0, 1, 5);
      }
      else {
        find_y_bot(this_wire, 0, 3, 5);
      }
      
     if ((x_by_ind[this_wire] > ((corner[clock[7]][0] - corner[clock[4]][0]) / (corner[clock[7]][2] - corner[clock[4]][2])) * (z_by_ind[this_wire] - corner[clock[4]][2]) + corner[clock[4]][0]) &&
      (x_by_ind[this_wire] > ((corner[clock[2]][0] - corner[clock[7]][0]) / (corner[clock[2]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 2, 4);
      }
      else if ((x_by_ind[this_wire] < ((corner[clock[7]][0] - corner[clock[6]][0]) / (corner[clock[7]][2] - corner[clock[6]][2])) * (z_by_ind[this_wire] - corner[clock[6]][2]) + corner[clock[6]][0]) &&
      (x_by_ind[this_wire] < ((corner[clock[4]][0] - corner[clock[7]][0]) / (corner[clock[4]][2] - corner[clock[7]][2])) * (z_by_ind[this_wire] - corner[clock[7]][2]) + corner[clock[7]][0])) {
        find_y_top(this_wire, 7, 4, 6);
      }
      else {
        find_y_top(this_wire, 7, 2, 6);
      } 
    } // end of gtx ltx
  } // end of greater than 3 5
  if ((y_bot_by_ind[this_wire] != 0) && (y_top_by_ind[this_wire] != 0)) {
    gen_sliver(this_wire);
  }
} // end of wire_check() method


public void less_than_x(int ind, int clocka, int clockb, int clockc, int clockd) {

  float ax = corner[clock[clocka]][0];
  float az = corner[clock[clocka]][2];
  float bx = corner[clock[clockb]][0];
  float bz = corner[clock[clockb]][2];
  float cx = corner[clock[clockc]][0];
  float cz = corner[clock[clockc]][2];
  float dx = corner[clock[clockd]][0];
  float dz = corner[clock[clockd]][2];
  boolean lt_ax = false;
  boolean lt_bx = false;
  boolean lt_cx = false;


  if (x_by_ind[ind] < ((bx - ax) / (bz - az)) * (z_by_ind[ind] - az) + ax) {
    lt_ax = true;
  }
  else {
    lt_ax = false;
  }

  if (x_by_ind[ind] < ((cx - bx) / (cz - bz)) * (z_by_ind[ind] - bz) + bx) {
    lt_bx = true;
  }
  else {
    lt_bx = false;
  }

  if (x_by_ind[ind] < ((dx - cx) / (dz - cz)) * (z_by_ind[ind] - cz) + cx) {
    lt_cx = true;
  }
  else {
    lt_cx = false;
  }

  if ((lt_ax == true) && (lt_bx == true) && (lt_cx == true)) {
    lt_x = true;
  }
  else {
    lt_x = false;
  }
}  


public void greater_than_x(int ind, int clocka, int clockb, int clockc, int clockd) {

  float ax = corner[clock[clocka]][0];
  float az = corner[clock[clocka]][2];
  float bx = corner[clock[clockb]][0];
  float bz = corner[clock[clockb]][2];
  float cx = corner[clock[clockc]][0];
  float cz = corner[clock[clockc]][2];
  float dx = corner[clock[clockd]][0];
  float dz = corner[clock[clockd]][2];
  boolean gt_ax = false;
  boolean gt_bx = false;
  boolean gt_cx = false;



  if (x_by_ind[ind] > ((bx - ax) / (bz - az)) * (z_by_ind[ind] - az) + ax) {
    gt_ax = true;
  }
  else {
    gt_ax = false;
  }
  if (x_by_ind[ind] > ((cx - bx) / (cz - bz)) * (z_by_ind[ind] - bz) + bx) {
    gt_bx = true;
  }
  else {
    gt_bx = false;
  }
  if (x_by_ind[ind] > ((dx - cx) / (dz - cz)) * (z_by_ind[ind] - cz) + cx) {
    gt_cx = true;
  }
  else {
    gt_cx = false;
  }
  if ((gt_ax == true) && (gt_bx == true) && (gt_cx == true)) {
    gt_x = true;
  }
  else {
    gt_x = false;
  }
}  


public void find_y_bot(int ind, int point1, int point2, int point3) {

  float px = corner[clock[point1]][0];
  float py = corner[clock[point1]][1];
  float pz = corner[clock[point1]][2];

  float qx = corner[clock[point2]][0];
  float qy = corner[clock[point2]][1];
  float qz = corner[clock[point2]][2];

  float rx = corner[clock[point3]][0];
  float ry = corner[clock[point3]][1];
  float rz = corner[clock[point3]][2];

  float a1 = (qx - px);
  float a2 = (qy - py);
  float a3 = (qz - pz);

  float b1 = (rx - px);
  float b2 = (ry - py);
  float b3 = (rz - pz);

  float i_var = (a2 * b3) - (b2 * a3);
  float j_var = -((a1 * b3) - (b1 * a3));
  float k_var = (a1 * b2) - (b1 * a2);
  float stand_const = ((i_var*(-px)) + (j_var*(-py)) + (k_var*(-pz)));

  y_bot_by_ind[ind] = -(i_var *  x_by_ind[ind] + k_var * z_by_ind[ind] + stand_const) / j_var;


}




public void find_y_top(int ind, int point1, int point2, int point3) {

  float px = corner[clock[point1]][0];
  float py = corner[clock[point1]][1];
  float pz = corner[clock[point1]][2];

  float qx = corner[clock[point2]][0];
  float qy = corner[clock[point2]][1];
  float qz = corner[clock[point2]][2];

  float rx = corner[clock[point3]][0];
  float ry = corner[clock[point3]][1];
  float rz = corner[clock[point3]][2];

  float a1 = (qx - px);
  float a2 = (qy - py);
  float a3 = (qz - pz);

  float b1 = (rx - px);
  float b2 = (ry - py);
  float b3 = (rz - pz);

  float i_var = (a2 * b3) - (b2 * a3);
  float j_var = -((a1 * b3) - (b1 * a3));
  float k_var = (a1 * b2) - (b1 * a2);
  float stand_const = ((i_var*(-px)) + (j_var*(-py)) + (k_var*(-pz)));

  y_top_by_ind[ind] = -(i_var *  x_by_ind[ind] + k_var * z_by_ind[ind] + stand_const) / j_var;
}

public void gen_sliver(int i)
{

      y_bot_proj[i] = y_bot_by_ind[i] * depth / z_by_ind[i];
      y_top_proj[i] = y_top_by_ind[i] * depth / z_by_ind[i];
            
      fill(255);                                                                    // draw a rectangle at that intersect
      noStroke();
      // rect 1 is top dot for sliver
      float left1 = i*4;
      float top1 = (height/ppmm - y_top_proj[i]) * ppmm;
      float wide1 = 2;
      float tall1 = dot_height;
      rect(left1, top1, wide1, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = i*4;
      float top2 = (height/ppmm - y_bot_proj[i]) * ppmm - dot_height;
      float wide2 = 2;
      float tall2 = dot_height;
      rect(left2, top2, wide2, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0,0,128);
      float left3 = i*4;
      float top3 = (height/ppmm - y_top_proj[i]) * ppmm + dot_height;
      float wide3 = 2;
      float tall3 = (y_top_proj[i] - y_bot_proj[i]) * ppmm - (dot_height * 2);
      rect(left3, top3, wide3, tall3);                                                        // draw a rectangle at that intersect
}



public void x_trans(float step) {
  for (int i = 0; i < 8; i ++) {
    corner[i][0] = corner[i][0] + step;
  }
  cube_pos[0] = cube_pos[0] + step;
}

public void y_trans(float step) {
  for (int i = 0; i < 8; i ++) {
    corner[i][1] = corner[i][1] + step;
  }
  cube_pos[1] = cube_pos[1] + step;
}

public void z_trans(float step) {
  for (int i = 0; i < 8; i ++) {
    corner[i][2] = corner[i][2] + step;
  }
  cube_pos[2] = cube_pos[2] + step;
}






public void keyPressed() {
  if (key == 'w') {
    pitch( PI / 50 );
  }
  else if (key == 's'){
    pitch( -PI / 50 );
  }
  else if (key == 'a'){
    yaw( -PI / 50 );
  }
  else if (key == 'd'){
    yaw( PI / 50 );
  }
  else if (key == 'q'){
    roll( PI / 50);
  }
  else if (key == 'e'){
    roll( - PI / 50 );
  }
  else if (key == 'r'){
    y_trans(5);
  }
  else if (key == 'f'){
    y_trans(-5);
  }
  else if (key == CODED){
    if (keyCode == UP){
      x_trans(-5);
    } else if (keyCode == DOWN){
      x_trans(5);
    } else if (keyCode == LEFT){
      z_trans(-5);
    } else if (keyCode == RIGHT){
      z_trans(5);
    }
  }
  fill(0);
  find_bottom_corner();
  rect(-1,-1,width+1,height+1);
  for (int zero_to_wire = 0; zero_to_wire < wire; zero_to_wire ++) {
    y_bot_by_ind[zero_to_wire] = 0;
    y_top_by_ind[zero_to_wire] = 0;
    wire_check(zero_to_wire);       // should later be attached to a for iterator from zero to wire
  }

/*
  int m = 30;


  line(300 + corner[0][0] * m, width - (500 + corner[0][1] * m), 300 + corner[1][0] * m, width - (500 + corner[1][1] * m));
  line(300 + corner[0][0] * m, width - (500 + corner[0][1] * m), 300 + corner[2][0] * m, width - (500 + corner[2][1] * m));
  line(300 + corner[0][0] * m, width - (500 + corner[0][1] * m), 300 + corner[4][0] * m, width - (500 + corner[4][1] * m));
  line(300 + corner[1][0] * m, width - (500 + corner[1][1] * m), 300 + corner[3][0] * m, width - (500 + corner[3][1] * m));
  line(300 + corner[1][0] * m, width - (500 + corner[1][1] * m), 300 + corner[5][0] * m, width - (500 + corner[5][1] * m));
  line(300 + corner[2][0] * m, width - (500 + corner[2][1] * m), 300 + corner[3][0] * m, width - (500 + corner[3][1] * m));
  line(300 + corner[2][0] * m, width - (500 + corner[2][1] * m), 300 + corner[6][0] * m, width - (500 + corner[6][1] * m));
  line(300 + corner[3][0] * m, width - (500 + corner[3][1] * m), 300 + corner[7][0] * m, width - (500 + corner[7][1] * m));
  line(300 + corner[4][0] * m, width - (500 + corner[4][1] * m), 300 + corner[5][0] * m, width - (500 + corner[5][1] * m));
  line(300 + corner[4][0] * m, width - (500 + corner[4][1] * m), 300 + corner[6][0] * m, width - (500 + corner[6][1] * m));
  line(300 + corner[5][0] * m, width - (500 + corner[5][1] * m), 300 + corner[7][0] * m, width - (500 + corner[7][1] * m));
  line(300 + corner[6][0] * m, width - (500 + corner[6][1] * m), 300 + corner[7][0] * m, width - (500 + corner[7][1] * m));
  */
}
static public void main(String args[]) {   PApplet.main(new String[] { "_1_02_cube" });}}