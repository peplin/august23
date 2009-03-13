// RECTANGULAR SOLID builder


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

float depth = 32.5;                        // distance from projector Mapline
float map_length = 21.0;                   // mapline length, inches
float hyp_unit = 1.0/8.0;                  // units, in inches, along hyp
float map_unit = 1.0/4.0;                  // units, in inches, along mapline
int wire = 85;                             // number of wires in contraption

// Variables for the Projector

int wires_hit = 85;                        // how many wires the projector hits
float ppi = 48;                              // pixels per inch
float sliver_ratio = 0.85;                 // ratio each sliver gets hit

// Variables to build the Map

float[] map = new float[wire];             // distance from mapline to point of intersection along hypot
float[] x_by_ind = new float[wire];        // x coordinate for each wire
float[] z_by_ind = new float[wire];        // z coordinate for each wire
float[] y_bot_by_ind = new float[wire];
float[] y_top_by_ind = new float[wire];

// Calib variables

  int r_nudge = -1;                           // how far the slivers nudge right, in pxls
  int width_stretcher = 0;                   // how many pxls the width should stretch






void setup() {
  size(1024, 768);
  background(0);
}

void draw() {
  if (loaded == false) {
    load();
    loaded = true;
  }
  fill(0);
  stroke(255);
}

void load() {  // cube position, cube dimensions, corner dimensions

  cube_pos[0] = 0;
  cube_pos[1] = 8;
  cube_pos[2] = depth - 7;

  cube_dim[0] = 4.5;
  cube_dim[1] = 4.5;
  cube_dim[2] = 4.5;

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

  map[0] = 116.0;
  map[1] = 49.0;
  map[2] = 111.0;
  map[3] = 87.0;
  map[4] = 47.0;
  map[5] = 25.0 ;
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
    z_by_ind[j] = depth - (depth*map[j]/hyp*hyp_unit);
    x_by_ind[j] = xmap - xmap*map[j]/hyp*hyp_unit;
  }
  pitch(.1);
  yaw(.1);
  roll(.1);
}


void pitch(float rad)
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

void yaw(float rad)
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

void roll(float rad)
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


void find_bottom_corner() {
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

void wire_check(int this_wire)
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


void less_than_x(int ind, int clocka, int clockb, int clockc, int clockd) {

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


void greater_than_x(int ind, int clocka, int clockb, int clockc, int clockd) {

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


void find_y_bot(int ind, int point1, int point2, int point3) {

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




void find_y_top(int ind, int point1, int point2, int point3) {

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







void gen_sliver(int i)
{

      fill(255);                                                                    // draw a rectangle at that intersect
      noStroke();
      // rect 1 is top dot for sliver
      float left1 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top1 = (height/ppi - y_top_by_ind[i]) * ppi;
      float wide1 = width / wires_hit * sliver_ratio;
      float tall1 = dot_height;
      rect(left1, top1, wide1, tall1);                                                        // draw a rectangle at that intersect

      // rect 2 is bottom dot for sliver
      float left2 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top2 = (height/ppi - y_bot_by_ind[i]) * ppi - dot_height;
      float wide2 = width / wires_hit * sliver_ratio;
      float tall2 = dot_height;
      rect(left2, top2, wide2, tall2);                                                        // draw a rectangle at that intersect

      // rect 3 is filler for sliver
      fill(0,0,200);
      float left3 = (i-round((wire-wires_hit)/2)) * (width + width_stretcher) / wires_hit + r_nudge;
      float top3 = (height/ppi - y_top_by_ind[i]) * ppi + dot_height;
      float wide3 = width / wires_hit * sliver_ratio;
      float tall3 = (y_top_by_ind[i] - y_bot_by_ind[i]) * ppi - (dot_height * 2);
      //rect(left3, top3, wide3, tall3);                                                        // draw a rectangle at that intersect
}



void x_trans(float step) {
  for (int i = 0; i < 8; i ++) {
    corner[i][0] = corner[i][0] + step;
  }
  cube_pos[0] = cube_pos[0] + step;
}

void z_trans(float step) {
  for (int i = 0; i < 8; i ++) {
    corner[i][2] = corner[i][2] + step;
  }
  cube_pos[2] = cube_pos[2] + step;
}







void keyPressed() {
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
  } else if (key == CODED){
    if (keyCode == UP){
      x_trans(-0.1);
    } else if (keyCode == DOWN){
      x_trans(0.1);
    } else if (keyCode == LEFT){
      z_trans(-0.1);
    } else if (keyCode == RIGHT){
      z_trans(0.1);
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
