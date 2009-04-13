/*******************************************************************
 * Simulated Stellar Evolution in Wiremap Coordinate System
 * 
 * INPUTS: 
 * observer's Integer Color: RED more massive; Blue less massive; eol depends on mass.
 * observer's Heart Rate: frequency of diameter oscillation
 * 
 * USES:
 * wiremap
 * WiremapRectangle
 * WiremapGlowingSphere
 * 
 * 
 * Evolutionary stages
 * 0) condensation of cold gas from molecular cloud: fill in random points within a sphere, color gray
 * 1) onset of fusion: fade from gray to white
 * 2) change of elements through fusion: fade from white everywhere to red in core and orange in shell
 * 3) oscillation of diameter due to changes in force balance
 * oscillate radius of sphere
 * oscillate color of outer shell from inner color to an orange color (or use input of person's mass here)
 * 4) end of life
 * a) black hole:
 * i)  chaotic vibrations modulated by a slow shrinking and 
 * ii) fast shrink 
 * b) supernova:
 * i)  fade star to white and slow shrink to a minimum radius
 * ii) chaotic vibrations
 * iii) fast expansion from minimum state to infinity.
 * 
 * 
 * 
 ******************************************************************/
import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;
import wiremap.WiremapRectangle;

Wiremap map;
WiremapGlowingSphere glowingSphere;
WiremapRectangle rectangle;
int depth = 36;

int r0 = 5;
; // global variables that allow continuity
int c0_core = color( 0,  0, 0, 0 );
int c0_base = color( 0,  0, 0, 0 );
int c_eol,c_osc;
int rad0,rad1;

// bkg stars
int nbkg = 100;
int xbkg[], ybkg[], zbkg[], col[];
float alpha_bkg;

//times
float time;
float tbkg1,tbkg2;
float tcond1, tcond2;
float tsph1a, tsph2a,tsph1b, tsph2b, tsph1c,tsph2c;
float tosc1a, tosc2a,tosc1b, tosc2b;
float tgrow1, tgrow2;
float teol1, teol2, teol3;

void setup() {
  size(1024, 768);

  //wiremap
  map = new Wiremap(this, 256, 90, 36, 36, 48, .1875, .1875, 4,
  "/Users/bnord/bzr/sketchbook/wiremap/depth256.txt");

  //initialize sphere
  glowingSphere = new WiremapGlowingSphere(map, 500, 300, 10, c0_base, r0, c0_core); 

  // initialize rectangles for bkg stars and condensing statge
  rectangle = new WiremapRectangle(map, 0,0,0,0,4,2,2,0,0);

  //initialize bkg stars
  xbkg = new int[nbkg];
  ybkg = new int[nbkg];
  zbkg = new int[nbkg];
  col = new int[nbkg];
  for (int i = 0; i< nbkg; i++){
    xbkg[i] = int(random(0,width));
  }
  for (int i = 0; i< nbkg; i++){
    ybkg[i] = int(random(0,height));
  }
  for (int i = 0; i< nbkg; i++){
    zbkg[i] = int(random(0,depth));
  }

  //times
  tbkg1  =0     *1000;
  tbkg2  =1     *1000; 
  tsph1a =1     *1000;//fade in
  tsph2a =3     *1000;  
  tsph1b =5     *1000;//enlarge
  tsph2b =7     *1000;
  tsph1c =7    *1000;//fade to color
  tsph2c =10    *1000;
  tosc1a =0     *1000;//osc radius
  tosc2a =0     *1000;
  tosc1b =0     *1000;// osc color
  tosc2b =0     *1000;  
  teol1  =10     *1000;// end
  teol2  =10    *1000;
  teol3  =15    *1000;

  //eol: becomes input
}

//smaller slivers (smaller y)
void draw() {

  background(0);
  time = millis();

  // BKG
  // fade in bkg particles:
  if (time < tbkg2 && time >= tbkg1) {
    alpha_bkg = 255*(time-tbkg1)/(tbkg2-tbkg1); 
  }

  pdraw(rectangle, xbkg, ybkg,zbkg,col,alpha_bkg,nbkg);

  // SPHERE
  // fade from black
  if (time < tsph2a && time >= tsph1a){
    float c1 = 255*(time-tsph1a)/(tsph2a-tsph1a); 
    glowingSphere.setBaseColor(color(c1,c1,c1,c1));
    glowingSphere.setCoreColor(color(c1,c1,c1,c1));
  }

  // grow 
  if (time <= tsph2b && time > tsph1b){
    rad0 = grow(r0, 8, tsph1b, tsph2b);  
  }

  // fade from white to red and orange
  if (time < tsph2c && time >= tsph1c){
    float c1 = 100 *(time-tsph1c)/(tsph2c-tsph1c); 
    float c2 = 50  *(time-tsph1c)/(tsph2c-tsph1c); 
    float c3 = 255 *(time-tsph1c)/(tsph2c-tsph1c);
    c0_base = color(255-c2,255-c2, 255-c3, 0);
    c0_core = color(255-c1,255-c3,255-c3, 0);
    glowingSphere.setBaseColor(c0_base);
    glowingSphere.setCoreColor(c0_core);
  }

  // oscillate
  c_osc = 0;
  if (time < tosc2a && time >= tosc1a){ 
    float freq = 1./2000.;
    rad1 = osc( c_osc, rad0, freq, tosc1a, tosc2a ); 
  }

  /* c_osc = 1;
   if (time < tosc2b && time >= tosc1b){ 
   float freq = 1./2000.;
   osc( freq, c_osc, tosc1b, tosc2b ); 
   }*/

  // eol
  c_eol = 0;
  if (time >= teol1 && time < teol3){ 
    eol(c_eol,rad0, teol1, teol2, teol3); 
  }

  // display glowing sphere
  glowingSphere.display();

}

// Internal Functions
int osc( int osc_type, int rini, float freq,  float t1, float t2 ) {
  int out=0;
  if (osc_type == 0){ 
    float freq_rad = freq;
    int amp_rad = 7;
    int rad = rini + int(amp_rad   *(abs(sin(freq_rad     *(time-t1))))); 
    glowingSphere.setRadius(rad);
    out= rad;    
  }
  if (osc_type == 1){ 
    float freq_col_core = freq;
    int amp_col_core = 255;
    int col_core = c0_core + int(amp_col_core*(abs(sin(freq_col_core*(time-t1))))); 
    glowingSphere.setBaseColor(col_core); 
    out= col_core;
  }
  if (osc_type == 2){ 
    float freq_col_base = freq;
    int amp_col_base = 150;
    int col_base = c0_base + int(amp_col_base*(abs(sin(freq_col_base*(time-t1)))));   
    glowingSphere.setCoreColor(col_base); 
    out = col_base;
  }
  return out;
}

int grow(int rini, int rfin, float t1, float t2){
  int rad = rini + int((rfin-rini)*((time-t1)/(t2-t1)));
  glowingSphere.setRadius(rad); 
  return rad;
}

void eol(int eol_type,int rini,  float t1, float t2, float t3){
  if (eol_type == 0){
  //  osc(rini,  1,1/500.,  t1, t2 );
  // osc(rini, 1./250., t1, t2);
   int rad = grow(rini, 1, t2, t3);
  }
  if (eol_type == 1){
    //osc( rini, 1, 1/500.,  t1, t2 );
    int rad = grow(rini, 1000, t2, t3);
       println(rad);
  }
  if (eol_type == 2){
  }
}

void pdraw(WiremapRectangle myrect, int[] x, int[] y, int[] z, int[] c, float myalpha, int n){
  int wx,wy,wz;
  for (int i = 0; i< n; i++){
    wx = (int)x[i];
    wy = (int)y[i];
    wz = (int)z[i];
    c[i] = color(255,255,255, myalpha);
    myrect.setBaseColor(c[i]);
    myrect.setPosition(wx,wy,wz);
    myrect.display();
  }
}



















