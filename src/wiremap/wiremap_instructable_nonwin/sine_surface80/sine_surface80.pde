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

//  Fullscreen stuff:

/* Variable declarations
---------------------------------------------------------*/

/* Physical Wiremap, in inches
---------------------------------------------------------*/

float depth             = 70.0;               // The mapline lies 3 meters away from the projector's focal point
float map_length        = 35.0;               // The mapline is 1.28 meters wide
float depth_unit        = 0.4375;               // Each depth unit is 5 millimeters
float map_unit          = 0.4375;               // Each mapline unit is 5 millimeters
int wire                = 80;                // There are 128 wires in this Wiremap
float depth_thickness   = 20.0;                 // How deep is the field (perpendicular to the mapline)


/* Projector
---------------------------------------------------------*/

float ppi               = 32;                  // Pixels per millimeter (unit conversion).  Only true for mapline plane - 4 pixels every 5 millimeters
int string_pix_count    = 3;                   // How many columns of pixels are being projected on each string


/* Map
---------------------------------------------------------*/

float[] map             = new float[wire];    // example: map[0] = 90 means that the first string is 45 cm away from the mapline
float[] x_by_ind        = new float[wire];    // x coordinate for each wire
float[] z_by_ind        = new float[wire];    // z coordinate for each wire


/* Globe A
---------------------------------------------------------*/

float[] globe           = new float[3];       // globe x,y,z coords
float radius            = 31.00;              // default globe radius
int dot_height          = 15;                 // height of surface pixels.
boolean render_globe    = true;               // toggle globe rendering

/* Key input
---------------------------------------------------------*/

float step              = .2;                 // how far the globe moves / button press
boolean mouse           = true;               // is mouse clicked?
int colorval_r          = 0;                  // red
int colorval_g          = 0;                  // green
int colorval_b          = 255;                // blue
boolean xpin            = false;              // the mouse controls the globe's y & z axis
boolean ypin            = true;               // x & z
boolean zpin            = false;              // x & y
int start_time          = 0;                  // for beat mapper
int end_time            = 0;                  //
float beat_multiplier   = 1;                  // multiplies freqeuncy of how often beat hits



/* Beat Mapper Variables
---------------------------------------------------------*/
 
int[] last_32 = new int[32];                        // last 32 times the spacebar has been pressed
int times_struck;                                   // nubmer of times spacebar struck since timer was reset
int first_strike;                                   // millis value for when timer was reset
int period = 500;                                   // time between beats (this is the metronome)
int offset = 1;                                     // how far away in time we are from the last beat


/* wave variables
 ---------------------------------------------------------*/
int trail = 350;                                    // number of iterations of past mouse clicks we keep
int[] click_time = new int[trail];                  // array of times (millis) associated w/ clicks
int[] click_x = new int[trail];                     // array of x locales for clicks
int[] click_y = new int[trail];                     // array of y locales for clicks
float[] click_x_trans = new float[trail];           // translations from mouse x to xyz
float[] click_y_trans = new float[trail];           // translations from mouse y to xyz
float amplitude = .6;                              // amplitude of waves
int decay = 3000;                                   // how long it takes for waves to die down
float wave_velocity = .035;                          // inches/milliseconds
int trail_frequency = 10;                           // milliseconds - NOT frequency of wave, but how often a new value gets pushed into the trail arrays (above)
int trail_cycle_count;                              // this gets updated once every (trail_frequency)
int trail_cycle_count_compare;                      // this is used to check to see if we need a new value
int water_color = 0;                                // 

float plane_angle = .0;                            // the angle of the plane of the water (think m in y = mx + b)
float plane_intercept = 0;                        // where the plane intersects the origin (think b in y = mx + b)


import processing.opengl.*;

static public void main(String args[]) {
  PApplet.main(new String[] { "--present", "sine_surface80" });
}

void setup() {
  size(screen.width, screen.height, OPENGL);
  background(255);
  loader();
}

void draw() {
  noCursor();
  noStroke();
  frameRate(30);
  fill(0);
  rect(0, 0, width, height);
  colorval_r = 255;
  colorval_g = 0;
  colorval_b = 0;
  sineSurface();
}

void sineSurface() {
  
  /*     trail_frequency appends clicks to the mouse trail arrays   */
  
  int remainder = millis() % trail_frequency;
  trail_cycle_count = (millis() - remainder) / trail_frequency;
  if (trail_cycle_count != trail_cycle_count_compare) {
    trail_cycle_count_compare = trail_cycle_count;
    append_click(mouseX, mouseY);
  }
  
  
  float[] time_since_click = new float[trail];                  //  the difference between now and the array of clicks
  float[] amp_modifier = new float[trail];                      //  the amp according to decay and time since click
  float[] distance_since_pass = new float[trail];               //  the distance since the head of the wave has passed the string
  float[] distance_since_pass_fraction = new float[trail];      //  the distance gets multiplied by a fraction for beat mapping (period)
  float[] time_since_pass = new float[trail];                   //  amount of time that has passed since head of wave & wire intersection
  float[] wave_head_distance = new float[trail];                //  distance between epicenter and head of wave
  float[] amp = new float[trail];                               //  amplitude of wave @ wire point according to mouse movement & beatmapping
  
  /*  for each wire...   */
  
  for(int i=0; i<wire; i+=1) {
    float final_amp = z_by_ind[i]*plane_angle + plane_intercept ;                     //  the baseline for the final amplitude is an upward slope when looking @ the wiremap... used y = mx + b
    
    for(int x = 0; x < trail; x ++ ) {

      float local_hyp = sqrt(sq(x_by_ind[i] - click_x_trans[x])+sq(z_by_ind[i] - click_y_trans[x]));
      time_since_click[x] = millis() - click_time[x];
      wave_head_distance[x] = time_since_click[x] * wave_velocity;
      distance_since_pass[x] = wave_head_distance[x] - local_hyp;
      distance_since_pass_fraction[x] = distance_since_pass[x] / float(period / 6);
      time_since_pass[x] = distance_since_pass[x] / wave_velocity;
      if (time_since_pass[x] > 0 && time_since_pass[x] < decay ) {
        amp_modifier[x] = time_since_pass[x] / decay - 1;
      } 
      else {
        amp_modifier[x] = 0;
      }
      amp[x] = - amplitude * amp_modifier[x] * sin((2 * PI * distance_since_pass_fraction[x]));
      
      final_amp = final_amp + amp[x];
    }

    float y_top_coord = final_amp;
    float y_bot_coord = -20;
    float y_top_proj = y_top_coord * depth / z_by_ind[i];                      // compensate for projection morphing IN INCHES
    float y_bot_proj = y_bot_coord * depth / z_by_ind[i];
    float y_height_proj = y_top_proj - y_bot_proj;
    fill(255);                                                                    // draw a rectangle at that intersect

    // rect 1 is top dot for sliver
    float left1 = i * (width) / wire;
    float top1 = (height/ppi - y_top_proj) * ppi;
    float wide1 = string_pix_count;
    float tall1 = dot_height;
    rect(left1, top1, string_pix_count, tall1);                                                        // draw a rectangle at that intersect

    // rect 3 is filler for sliver
    fill(0, 0, water_color);
    float left3 = i * (width) / wire;
    float top3 = (height/ppi - y_top_proj) * ppi + dot_height;
    float wide3 = string_pix_count;
    float tall3 = y_height_proj * ppi - (dot_height * 2);
    rect(left3, top3, string_pix_count, tall3);                                                        // draw a rectangle at that intersect
  }

  float globe_x = (mouseX / float(width)) * (map_length) - (map_length / 2);
  float globe_z = depth - (mouseY) / float(height) * (depth_thickness);
  float y_amp = globe_z*plane_angle + plane_intercept;
     for(int x = 0; x < trail; x ++ ) {

      float local_hyp = sqrt(sq(globe_x - click_x_trans[x])+sq(globe_z - click_y_trans[x]));
      time_since_click[x] = millis() - click_time[x];
      wave_head_distance[x] = time_since_click[x] * wave_velocity;

      distance_since_pass[x] = wave_head_distance[x] - local_hyp;
      distance_since_pass_fraction[x] = distance_since_pass[x] / float(period / 6);
      time_since_pass[x] = distance_since_pass[x] / wave_velocity;
      if (time_since_pass[x] > 0 && time_since_pass[x] < decay ) {
        amp_modifier[x] = - time_since_pass[x] / decay + 1;
      } 
      else {
        amp_modifier[x] = 0;
      }
      amp[x] = - amplitude * amp_modifier[x] * sin((2 * PI * distance_since_pass_fraction[x]));

      y_amp = y_amp + amp[x];
    }
  float globe_y = y_amp;
  float radius = (sin(TWO_PI * float((millis() - offset) % period) / float(period)) + 1) / 2;
  if(render_globe == true) {
    gen_globe(globe_x, -globe_y, globe_z, 5);
  }
  //println(globe_x + " " + globe_y);
  if (millis() > 10000) {
  }
}


void append_click(int local_mouseX, int local_mouseY) {
  click_time = subset(click_time, 1);
  click_x = subset(click_x, 1);
  click_y = subset(click_y, 1);
  click_x_trans = subset(click_x_trans, 1);
  click_y_trans = subset(click_y_trans, 1);
  click_time = append(click_time, millis());
  click_x = append(click_x, local_mouseX);
  click_y = append(click_y, local_mouseY);
  click_x_trans = append(click_x_trans, (local_mouseX / float(width)) * (map_length) - (map_length / 2));
  click_y_trans = append(click_y_trans, depth - (local_mouseY) / float(height) * (depth_thickness));
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
        float top1            = (height/ppi - y_top_proj) * ppi + dot_height;     // ppi = pixel / mm.  These are conversions to & from pixels and mm
        float wide1           = string_pix_count;
        float tall1           = y_height_proj * ppi - (dot_height * 2);
        rect(left1, top1, wide1, tall1);


        fill(255);                                                                  // Fill the surface pixels White

        /* Top Surface
        ---------------------------------------------------------*/
        float left2           = i * (width) / wire;
        float top2            = (height/ppi - y_top_proj) * ppi;
        float wide2           = string_pix_count;
        float tall2           = dot_height;
        rect(left2, top2, wide2, tall2);

        /* Bottom Surface
        ---------------------------------------------------------*/
        float left3           = i * (width) / wire;
        float top3            = (height/ppi - y_bot_proj) * ppi - dot_height;
        float wide3           = string_pix_count;
        float tall3           = dot_height;
        rect(left3, top3, wide3, tall3);
      }
    }
  }
}



void mousePressed() {
  if (mouseButton == LEFT) {
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
    append_click(mouseX, mouseY);
  } else if (mouseButton == RIGHT) {
    if (water_color == 255) {
      water_color = 0;
    } else {
      water_color = 255;
    }
  }
}

void keyPressed() {

  /* Globe A
  ---------------------------------------------------------*/
  if (true == true) {                                    
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
    } else if (key == 't') {                                 // beat mapper buttons - start, stop, effects, and multipliers
      start_time = millis();
    } else if (key == 'y') {
      end_time = millis();
      period = end_time - start_time;
      offset = start_time % period;
    } else if (key == 'g') {
      beat_multiplier = 1;
    } else if (key == 'h') {
      beat_multiplier = 2;
    } else if (key == 'j') {
      beat_multiplier = 4;
    } else if (key == 'k') {
      beat_multiplier = 8;
    } else if (key == 'b') {
      if (render_globe == false) {
        render_globe = true;
      } else {
        render_globe = false;
      }
    }
  }  
}


void keyReleased() 
{
  if(mouse == false) {
    mouse = true;
  }
  if(key==' ') {
    if(millis()-last_32[31] > 1500) {
      last_32[31] = 0;
    }
    last_32 = subset(last_32, 1);
    last_32 = append(last_32, millis());
    for(int i=31; i>=0; i--) {
      if(last_32[i] == 0) {
        times_struck = 31 - i;
        first_strike = last_32[i+1];
        break;
      } else {
        times_struck = 32;
        first_strike = last_32[0];
      }
    }
    if(times_struck > 1) {
      period = (last_32[31] - first_strike) / (times_struck - 1);
    }
    offset = last_32[31];
  }
}


void loader() {                                           // loads data for this particular wiremap
map[0] = 41;
map[1] = 2;
map[2] = 40;
map[3] = 42;
map[4] = 24;
map[5] = 32;
map[6] = 2;
map[7] = 8;
map[8] = 23;
map[9] = 14;
map[10] = 9;
map[11] = 35;
map[12] = 5;
map[13] = 31;
map[14] = 26;
map[15] = 22;
map[16] = 16;
map[17] = 19;
map[18] = 21;
map[19] = 0;
map[20] = 32;
map[21] = 7;
map[22] = 12;
map[23] = 15;
map[24] = 18;
map[25] = 10;
map[26] = 6;
map[27] = 29;
map[28] = 39;
map[29] = 25;
map[30] = 33;
map[31] = 19;
map[32] = 39;
map[33] = 11;
map[34] = 28;
map[35] = 4;
map[36] = 34;
map[37] = 13;
map[38] = 38;
map[39] = 24;
map[40] = 0;
map[41] = 26;
map[42] = 16;
map[43] = 14;
map[44] = 29;
map[45] = 41;
map[46] = 35;
map[47] = 43;
map[48] = 4;
map[49] = 34;
map[50] = 12;
map[51] = 20;
map[52] = 11;
map[53] = 1;
map[54] = 13;
map[55] = 3;
map[56] = 9;
map[57] = 21;
map[58] = 17;
map[59] = 1;
map[60] = 37;
map[61] = 31;
map[62] = 8;
map[63] = 36;
map[64] = 0;
map[65] = 7;
map[66] = 17;
map[67] = 30;
map[68] = 44;
map[69] = 20;
map[70] = 22;
map[71] = 27;
map[72] = 15;
map[73] = 5;
map[74] = 3;
map[75] = 25;
map[76] = 37;
map[77] = 6;
map[78] = 43;
map[79] = 28;

  for(int i = 0; i < trail; i ++ ) {
    click_time[i] = 0 - (i * 500);
  }
  for(int j=0; j<wire; j++) {                           // calculate x and z coordinates of each wire
    float xmap = (0 - (map_length / 2)) + j*map_unit;
    float hyp = sqrt(sq(xmap) + sq(depth));
    z_by_ind[j] = depth - map[j]*depth_unit;
    x_by_ind[j] = xmap - xmap*map[j]/hyp*depth_unit;
  }
}
