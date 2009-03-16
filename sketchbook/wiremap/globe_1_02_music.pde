/*

Wiremap Renderer for 2 Globes
by 

              /      \
       ______/________\______
      /      \        /      \
     /________\      /________\______
    /                \         |     \
   /   /      \       \_____   |      \
      /________\______         |      /
      \        /      \        |_____/
       \      /________\
              \          \     /
               \_____      \ /
                           / \
                         /     \

For more information on the project please visit:
http://wiremap.phedhex.com

This program builds two separate 3d globes.  I have two separate functions (& sets of variables) because I haven't quite yet figured out how call a function twice.  Elementary stuff, I know, but I'll get to it when I can.

Some conventions to be aware of:

1 - This particular program builds in millimeters.
2 - Also, I use a left-handed coordinate system, with positive X going right, positive Y going up, and positive Z going forward.


*/



/* Variable declarations
---------------------------------------------------------*/

/* Physical Wiremap, in millimeters
---------------------------------------------------------*/

float depth             = 3000.00;            // The mapline lies 3 meters away from the projector's focal point
float map_length        = 1280.00;            // The mapline is 1.28 meters wide
float depth_unit        = 5.00;               // Each depth unit is 5 millimeters
float map_unit          = 5.00;               // Each mapline unit is 5 millimeters
int wire                = 256;                // There are 256 wires in this Wiremap


/* Projector
---------------------------------------------------------*/

float ppmm              = 0.80;               // Pixels per millimeter (unit conversion).  Only true for mapline plane - 4 pixels every 5 millimeters
int string_pix_count    = 2;                  // How many columns of pixels are being projected on each string


/* Map
---------------------------------------------------------*/

float[] map             = new float[wire];    // example: map[0] = 90 means that the first string is 45 cm away from the mapline
float[] x_by_ind        = new float[wire];    // x coordinate for each wire
float[] z_by_ind        = new float[wire];    // z coordinate for each wire


/* Globe A
---------------------------------------------------------*/

float[] globe           = new float[3];       // globe x,y,z coords
float radius            = 200.00;             // globe radius
int dot_height          = 5;                  // height of surface pixels.


/* Key input
---------------------------------------------------------*/

float step              = 5;                  // how far the globe moves / button press
boolean mouse           = true;               // is mouse clicked?
int colorval_r          = 0;                  // red
int colorval_g          = 0;                  // green
int colorval_b          = 255;                // blue
boolean xpin            = false;              // the mouse controls the globe's y & z axis
boolean ypin            = true;               // x & z
boolean zpin            = false;              // x & y
int start_time          = 0;                  // for beat mapper
int end_time            = 0;                  //
boolean gen_beat        = false;              //
boolean gen_flash       = false;              //
int period              = 1;                  //
int offset              = 1;                  //
float beat_multiplier   = 1;                  //
float radius_store      = 4;                  //
boolean gen_disco_ball  = false;              //
  
/* Globe B
---------------------------------------------------------*/

boolean b_visible       = false;              // globe b is visible
boolean globe_a         = true;               // mouse controls this globe_a (otherwise controls globe_b)
float[] globe_b         = new float[3];       // globe x,y,z coords
float radius_b          = 150.0;              // globe radius

int colorval_r_b        = 0;
int colorval_g_b        = 0;
int colorval_b_b        = 0;
boolean xpin_b          = false;
boolean ypin_b          = true;
boolean zpin_b          = false;



void setup() {
  size(1024, 768);
  background(255);
  loader();
}



void draw() {
  noStroke();
  frameRate(30);
  if (globe_a == true) {
    if (mouse == true) {
      if (xpin == true) {
        globe[1] = ((height - mouseY) / float(height)) * (900 + radius * 2) - (radius);
        globe[2] = depth + radius - (width - mouseX) / float(width) * (960 + radius * 2);
      } else if (ypin == true){
        globe[0] = (mouseX / float(width)) * (map_length + radius * 2) - (map_length / 2 + radius);
        globe[2] = depth + radius - (mouseY) / float(height) * (960 + radius * 2);
      } else {
        globe[0] = (mouseX / float(width)) * (map_length + radius * 2) - (map_length / 2 + radius);
        globe[1] = ((height - mouseY) / float(height)) * (900 + radius * 2) - (radius);
      }      
    }
  } else {
    if (mouse == true) {
      if (xpin_b == true) {
        globe_b[1] = ((height - mouseY) / float(height)) * (900 + radius_b * 2) - (radius_b);
        globe_b[2] = depth + radius_b - (width - mouseX) / float(width) * (960 + radius_b * 2);
      } else if (ypin_b == true){
        globe_b[0] = (mouseX / float(width)) * (map_length + radius_b * 2) - (map_length / 2 + radius_b);
        globe_b[2] = depth + radius_b - (mouseY) / float(height) * (960 + radius_b * 2);
      } else {
        globe_b[0] = (mouseX / float(width)) * (map_length + radius_b * 2) - (map_length / 2 + radius_b);
        globe_b[1] = ((height - mouseY) / float(height)) * (900 + radius_b * 2) - (radius_b);
      }      
    }
  }
  if (gen_beat == true) {
    gen_beat();
  }
  if (gen_flash == true) {
    gen_flash();
  }
  if (gen_disco_ball == true) {
    gen_disco_ball();
  }
  fill(0);
  rect(0, 0, width, height);
  gen_globe(globe[0], globe[1], globe[2], radius);
  if (b_visible == true) {
    gen_globe_b(globe_b[0], globe_b[1], globe_b[2], radius_b);
  }
}

void gen_globe(float x, float y, float z, float rad) {
  for(int i = 0; i < wire; i += 1) {
    if((x_by_ind[i] >= (x - rad)) && (x_by_ind[i] <= (x + rad))) {                  // if a wire's x coord is close enough to the globe's center
      float local_hyp = sqrt(sq(x_by_ind[i] - x) + sq(z_by_ind[i] - z));            // find the distance from the wire to the globe's center
      if(local_hyp <= rad) {                                                        // if the wire's xz coord is close enough to the globe's center
        float y_abs           = sqrt(sq(rad) - sq(local_hyp));                      // find the height of the globe at that point
        float y_top_coord     = y + y_abs;                                          // find the top & bottom coords
        float y_bot_coord     = y - y_abs;                                          // 
        float y_top_proj      = y_top_coord * depth / z_by_ind[i];                  // compensate for projection morphing
        float y_bot_proj      = y_bot_coord * depth / z_by_ind[i];
        float y_height_proj   = y_top_proj - y_bot_proj;


        /* Top dot
        ---------------------------------------------------------*/
        fill(colorval_r, colorval_g, colorval_b);                                   // Fill the globe pixels this color
        float left1           = i * (width) / wire;
        float top1            = (height/ppmm - y_top_proj) * ppmm + dot_height;     // ppmm = pixel / mm.  These are conversions to & from pixels and mm
        float wide1           = string_pix_count;
        float tall1           = y_height_proj * ppmm - (dot_height * 2);
        rect(left1, top1, wide1, tall1);


        fill(255);                                                                  // Fill the surface pixels White

        /* Top Surface
        ---------------------------------------------------------*/
        float left2           = i * (width) / wire;
        float top2            = (height/ppmm - y_top_proj) * ppmm;
        float wide2           = string_pix_count;
        float tall2           = dot_height;
        rect(left2, top2, wide2, tall2);

        /* Bottom Surface
        ---------------------------------------------------------*/
        float left3           = i * (width) / wire;
        float top3            = (height/ppmm - y_bot_proj) * ppmm - dot_height;
        float wide3           = string_pix_count;
        float tall3           = dot_height;
        rect(left3, top3, wide3, tall3);
      }
    }
  }
}


void gen_globe_b(float x, float y, float z, float rad) {
  for(int i = 0; i < wire; i += 1) {
    if((x_by_ind[i] >= (x - rad)) && (x_by_ind[i] <= (x + rad))) {                  // if a wire's x coord is close enough to the globe's center
      float local_hyp = sqrt(sq(x_by_ind[i] - x) + sq(z_by_ind[i] - z));            // find the distance from the wire to the globe's center
      if(local_hyp <= rad) {                                                        // if the wire's xz coord is close enough to the globe's center
        float y_abs           = sqrt(sq(rad) - sq(local_hyp));                      // find the height of the globe at that point
        float y_top_coord     = y + y_abs;                                          // find the top & bottom coords
        float y_bot_coord     = y - y_abs;                                          // 
        float y_top_proj      = y_top_coord * depth / z_by_ind[i];                  // compensate for projection morphing
        float y_bot_proj      = y_bot_coord * depth / z_by_ind[i];
        float y_height_proj   = y_top_proj - y_bot_proj;


        /* Top dot
        ---------------------------------------------------------*/
        fill(colorval_r_b, colorval_g_b, colorval_b_b);                                   // Fill the globe pixels this color
        float left1           = i * (width) / wire;
        float top1            = (height/ppmm - y_top_proj) * ppmm + dot_height;     // ppmm = pixel / mm.  These are conversions to & from pixels and mm
        float wide1           = string_pix_count;
        float tall1           = y_height_proj * ppmm - (dot_height * 2);
        rect(left1, top1, wide1, tall1);


        fill(255);                                                                  // Fill the surface pixels White

        /* Top Surface
        ---------------------------------------------------------*/
        float left2           = i * (width) / wire;
        float top2            = (height/ppmm - y_top_proj) * ppmm;
        float wide2           = string_pix_count;
        float tall2           = dot_height;
        rect(left2, top2, wide2, tall2);

        /* Bottom Surface
        ---------------------------------------------------------*/
        float left3           = i * (width) / wire;
        float top3            = (height/ppmm - y_bot_proj) * ppmm - dot_height;
        float wide3           = string_pix_count;
        float tall3           = dot_height;
        rect(left3, top3, wide3, tall3);
      }
    }
  }
}



/* gen_beat, gen_flash, and gen_disco_ball
---------------------------------------------------------*/
// These three functions rely on key entries for the first beat of two separate measures in music.

void gen_beat(){
  radius = 200 + 100 * cos(TWO_PI * beat_multiplier * float((millis() - offset) % period) / float(period));
}

void gen_flash(){
  if( cos(TWO_PI * beat_multiplier * float((millis() - offset) % period) / float(period)) > 0 ){
    radius = 300;
  } else {
    radius = 0;
  }
}

void gen_disco_ball() {
  int clicks = (floor(beat_multiplier * float(millis() - offset) / float(period))) % 8;
  switch(clicks){
    case 0:
      colorval_r = 0;
      colorval_g = 0;
      colorval_b = 0;
      break;
    case 1:
      colorval_r = 255;
      colorval_g = 0;
      colorval_b = 0;
      break;
    case 2:
      colorval_r = 0;
      colorval_g = 255;
      colorval_b = 0;
      break;
    case 3:
      colorval_r = 255;
      colorval_g = 255;
      colorval_b = 0;
      break;
    case 4:
      colorval_r = 0;
      colorval_g = 0;
      colorval_b = 255;
      break;
    case 5:
      colorval_r = 255;
      colorval_g = 0;
      colorval_b = 255;
      break;
    case 6:
      colorval_r = 0;
      colorval_g = 255;
      colorval_b = 255;
      break;
    case 7:
      colorval_r = 255;
      colorval_g = 255;
      colorval_b = 255;
      break;
  }
}

void keyPressed() {

  /* Globe A
  ---------------------------------------------------------*/
  if (globe_a == true) {                                    
    if (key == 'w') {                                       // adds value to the dimension that the mouse cannot move in
      if (xpin == true) {
        globe[0] = globe[0] + step;
      } else if (ypin == true) {
        globe[1] = globe[1] + step;
      } else if (zpin == true) {
        globe[2] = globe[2] + step;
      }
    } else if (key == 's') {
      if (xpin == true) {                                   // subtracts value from the dimension that the mouse cannot move in
        globe[0] = globe[0] - step;
      } else if (ypin == true) {
        globe[1] = globe[1] - step;
      } else if (zpin == true) {
        globe[2] = globe[2] - step;
      }
    } else if (key == 'e') {                                // adds to radius
      radius = radius + step;
    } else if (key == 'd') {                                // subs from to radius
      radius = radius - step;
    } else if (key == 'a') {                                // allows mouse control for radius (hold down 'a' and bring mouse up or down)
      radius = (height - mouseY) * .8;
      mouse = false;
    } else if (key == 'q') {                                // stops ball in place so that you can pop it somewhere else
      mouse = false;
    } else if (key == 'z') {                                // color control (hold down buttons and bring mouse up or down)
      colorval_r = (height - mouseY) * 255 / height;
    } else if (key == 'x') {
      colorval_g = (height - mouseY) * 255 / height;
    } else if (key == 'c') {
      colorval_b = (height - mouseY) * 255 / height;
    } else if (key == 'v') {
      colorval_r = (height - mouseY) * 255 / height;
      colorval_g = (height - mouseY) * 255 / height;
      colorval_b = (height - mouseY) * 255 / height;
    } else if (key == '1') {                                // x y z pin switches
      xpin = true;
      ypin = false;
      zpin = false;
    } else if (key == '2') {
      xpin = false;
      ypin = true;
      zpin = false;
    } else if (key == '3') {
      xpin = false;
      ypin = false;
      zpin = true;
    }else if (key == 't') {                                 // beat mapper buttons - start, stop, effects, and multipliers
      start_time = millis();
    } else if (key == 'y') {
      end_time = millis();
      period = end_time - start_time;
      offset = start_time % period;
    } else if (key == 'u') {
      gen_beat = true;
    } else if (key == 'i') {
      gen_beat = false;
    } else if (key == 'g') {
      beat_multiplier = 1;
    } else if (key == 'h') {
      beat_multiplier = 2;
    } else if (key == 'j') {
      beat_multiplier = 4;
    } else if (key == 'k') {
      beat_multiplier = 8;
    } else if (key == 'o') {
      radius_store = radius;
      gen_flash = true;
    } else if (key == 'p') {
      gen_flash = false;
      radius = radius_store;
    } else if (key == '7') {
      gen_disco_ball = true;
    } else if (key == '8') {
      gen_disco_ball = false;
    }
  /* Globe B
  ---------------------------------------------------------*/
  } else {
    if (key == 'w') {
      if (xpin == true) {
        globe_b[0] = globe_b[0] + step;
      } else if (ypin == true) {
        globe_b[1] = globe_b[1] + step;
      } else if (zpin == true) {
        globe_b[2] = globe_b[2] + step;
      }
    } else if (key == 's') {
      if (xpin == true) {
        globe_b[0] = globe_b[0] - step;
      } else if (ypin == true) {
        globe_b[1] = globe_b[1] - step;
      } else if (zpin == true) {
        globe_b[2] = globe_b[2] - step;
      }
    } else if (key == 'e') {
      radius_b = radius_b + step;
    } else if (key == 'd') {
      radius_b = radius_b - step;
    } else if (key == 'z') {
      colorval_r_b = (height - mouseY) * 255 / height;
    } else if (key == 'x') {
      colorval_g_b = (height - mouseY) * 255 / height;
    } else if (key == 'c') {
      colorval_b_b = (height - mouseY) * 255 / height;
    } else if (key == 'v') {
      colorval_r_b = (height - mouseY) * 255 / height;
      colorval_g_b = (height - mouseY) * 255 / height;
      colorval_b_b = (height - mouseY) * 255 / height;
    } else if (key == '1') {
      xpin_b = true;
      ypin_b = false;
      zpin_b = false;
    } else if (key == '2') {
      xpin_b = false;
      ypin_b = true;
      zpin_b = false;
    } else if (key == '3') {
      xpin_b = false;
      ypin_b = false;
      zpin_b = true;
    }
  }  
  if (key == 'b') {                                       // Switch from one globe to another
    if (b_visible == true) {
      if (globe_a == true) {
        globe_a = false;
      } else {
        globe_a = true;
      }
    }
  }
  else if (key == 'n') {                                  // toggle existence of globe b
    if (b_visible == true) {
      globe_a = true;
      b_visible = false;
    } else {
      b_visible = true;
    }
  }
}

void keyReleased() 
{
  if(mouse == false) {
    mouse = true;
  }
}
void mousePressed() {
  if (mouseButton == LEFT) {
    radius_store = radius;
    radius = 0;
  }
}
void mouseReleased() {
  radius = radius_store;
}




void loader() {                                           // loads data for this particular wiremap
  map[0]    = 90;
  map[1]    = 59;
  map[2]    = 118;
  map[3]    = 129;
  map[4]    = 173;
  map[5]    = 33;
  map[6]    = 56;
  map[7]    = 79;
  map[8]    = 53;
  map[9]    = 38;
  map[10]   = 28;
  map[11]   = 147;
  map[12]   = 117;
  map[13]   = 70;
  map[14]   = 105;
  map[15]   = 186;
  map[16]   = 179;
  map[17]   = 88;
  map[18]   = 34;
  map[19]   = 75;
  map[20]   = 107;
  map[21]   = 182;
  map[22]   = 185;
  map[23]   = 47;
  map[24]   = 70;
  map[25]   = 186;
  map[26]   = 123;
  map[27]   = 169;
  map[28]   = 12;
  map[29]   = 140;
  map[30]   = 164;
  map[31]   = 82;
  map[32]   = 72;
  map[33]   = 148;
  map[34]   = 78;
  map[35]   = 16;
  map[36]   = 122;
  map[37]   = 54;
  map[38]   = 7;
  map[39]   = 113;
  map[40]   = 148;
  map[41]   = 178;
  map[42]   = 40;
  map[43]   = 6;
  map[44]   = 19;
  map[45]   = 141;
  map[46]   = 162;
  map[47]   = 141;
  map[48]   = 107;
  map[49]   = 22;
  map[50]   = 174;
  map[51]   = 88;
  map[52]   = 18;
  map[53]   = 149;
  map[54]   = 43;
  map[55]   = 40;
  map[56]   = 16;
  map[57]   = 82;
  map[58]   = 85;
  map[59]   = 38;
  map[60]   = 60;
  map[61]   = 92;
  map[62]   = 132;
  map[63]   = 129;
  map[64]   = 168;
  map[65]   = 20;
  map[66]   = 173;
  map[67]   = 33;
  map[68]   = 150;
  map[69]   = 20;
  map[70]   = 154;
  map[71]   = 91;
  map[72]   = 85;
  map[73]   = 7;
  map[74]   = 22;
  map[75]   = 79;
  map[76]   = 148;
  map[77]   = 90;
  map[78]   = 142;
  map[79]   = 82;
  map[80]   = 151;
  map[81]   = 73;
  map[82]   = 29;
  map[83]   = 38;
  map[84]   = 4;
  map[85]   = 171;
  map[86]   = 189;
  map[87]   = 83;
  map[88]   = 16;
  map[89]   = 59;
  map[90]   = 134;
  map[91]   = 96;
  map[92]   = 69;
  map[93]   = 128;
  map[94]   = 169;
  map[95]   = 23;
  map[96]   = 159;
  map[97]   = 24;
  map[98]   = 157;
  map[99]   = 154;
  map[100]  = 167;
  map[101]  = 52;
  map[102]  = 112;
  map[103]  = 6;
  map[104]  = 135;
  map[105]  = 142;
  map[106]  = 87;
  map[107]  = 65;
  map[108]  = 177;
  map[109]  = 183;
  map[110]  = 19;
  map[111]  = 26;
  map[112]  = 58;
  map[113]  = 30;
  map[114]  = 45;
  map[115]  = 44;
  map[116]  = 83;
  map[117]  = 85;
  map[118]  = 0;
  map[119]  = 16;
  map[120]  = 141;
  map[121]  = 155;
  map[122]  = 83;
  map[123]  = 68;
  map[124]  = 123;
  map[125]  = 108;
  map[126]  = 132;
  map[127]  = 29;
  map[128]  = 15;
  map[129]  = 43;
  map[130]  = 52;
  map[131]  = 130;
  map[132]  = 16;
  map[133]  = 49;
  map[134]  = 93;
  map[135]  = 23;
  map[136]  = 82;
  map[137]  = 47;
  map[138]  = 76;
  map[139]  = 168;
  map[140]  = 67;
  map[141]  = 27;
  map[142]  = 191;
  map[143]  = 81;
  map[144]  = 144;
  map[145]  = 71;
  map[146]  = 24;
  map[147]  = 123;
  map[148]  = 22;
  map[149]  = 78;
  map[150]  = 69;
  map[151]  = 108;
  map[152]  = 141;
  map[153]  = 151;
  map[154]  = 65;
  map[155]  = 85;
  map[156]  = 39;
  map[157]  = 43;
  map[158]  = 145;
  map[159]  = 37;
  map[160]  = 181;
  map[161]  = 84;
  map[162]  = 78;
  map[163]  = 117;
  map[164]  = 63;
  map[165]  = 50;
  map[166]  = 158;
  map[167]  = 150;
  map[168]  = 17;
  map[169]  = 75;
  map[170]  = 57;
  map[171]  = 149;
  map[172]  = 5;
  map[173]  = 148;
  map[174]  = 175;
  map[175]  = 42;
  map[176]  = 33;
  map[177]  = 179;
  map[178]  = 146;
  map[179]  = 134;
  map[180]  = 120;
  map[181]  = 68;
  map[182]  = 5;
  map[183]  = 19;
  map[184]  = 6;
  map[185]  = 100;
  map[186]  = 162;
  map[187]  = 138;
  map[188]  = 4;
  map[189]  = 43;
  map[190]  = 99;
  map[191]  = 66;
  map[192]  = 7;
  map[193]  = 62;
  map[194]  = 27;
  map[195]  = 177;
  map[196]  = 83;
  map[197]  = 154;
  map[198]  = 102;
  map[199]  = 162;
  map[200]  = 46;
  map[201]  = 172;
  map[202]  = 132;
  map[203]  = 38;
  map[204]  = 98;
  map[205]  = 140;
  map[206]  = 42;
  map[207]  = 57;
  map[208]  = 165;
  map[209]  = 92;
  map[210]  = 160;
  map[211]  = 72;
  map[212]  = 14;
  map[213]  = 140;
  map[214]  = 182;
  map[215]  = 79;
  map[216]  = 91;
  map[217]  = 135;
  map[218]  = 45;
  map[219]  = 177;
  map[220]  = 159;
  map[221]  = 51;
  map[222]  = 63;
  map[223]  = 98;
  map[224]  = 45;
  map[225]  = 84;
  map[226]  = 25;
  map[227]  = 148;
  map[228]  = 8;
  map[229]  = 134;
  map[230]  = 116;
  map[231]  = 58;
  map[232]  = 151;
  map[233]  = 171;
  map[234]  = 111;
  map[235]  = 101;
  map[236]  = 119;
  map[237]  = 138;
  map[238]  = 20;
  map[239]  = 14;
  map[240]  = 47;
  map[241]  = 66;
  map[242]  = 188;
  map[243]  = 4;
  map[244]  = 110;
  map[245]  = 24;
  map[246]  = 3;
  map[247]  = 52;
  map[248]  = 31;
  map[249]  = 95;
  map[250]  = 19;
  map[251]  = 72;
  map[252]  = 125;
  map[253]  = 79;
  map[254]  = 55;
  map[255]  = 185;
  
  globe[0] = 0;                                    
  globe[1] = radius;
  globe[2] = 0;
  
  for(int j=0; j<wire; j++) {                           // calculate x and z coordinates of each wire
    float xmap = (0 - (map_length / 2)) + j*map_unit;
    float hyp = sqrt(sq(xmap) + sq(depth));
    z_by_ind[j] = depth - map[j]*5;
    x_by_ind[j] = xmap - xmap*map[j]/hyp*depth_unit;
  }
}
