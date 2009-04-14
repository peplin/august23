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
import wiremap.WiremapLighthouse;

public class StarSimulationWire {

      private int starState;
  private Wiremap map;
  private WiremapGlowingSphere glowingSphere;
  private WiremapRectangle rectangle;
  private WiremapLighthouse lighthouse;
  private int depth = 36;
  private float r0 = 0;
  // global variables that allow continuity
  private int c0_core = color( 0,  0, 0, 0 );
  private int c0_base = color( 0,  0, 0, 0 );
  private int c_eol,c_osc;
  private float rad0,rad1;
  // bkg stars
  private int nbkg = 200;
  private float xbkg[], ybkg[], zbkg[];
  private int col[];
  private float alpha_bkg;
  private float freq_osc;
  //times
  private float time;
  private float tbkg1,tbkg2,tbkg1a, tbkg2a;
  private float tcond1, tcond2;
  private float tsph1a, tsph2a,tsph1b, tsph2b, tsph1c,tsph2c;
  private float tosc1a, tosc2a,tosc1b, tosc2b;
  private float tgrow1, tgrow2;
  private float teol1, teol2, teol3;

  private float amp_rad_random = random(2,9);
  
  private color starColor = color(255);

  public StarSimulationWire(PApplet parent, Wiremap wiremap) {

    // wiremap
    map = wiremap;
   
    // initialize sphere
    glowingSphere = new WiremapGlowingSphere(map, 500, 300, 10, c0_base, r0, c0_core); 

    // initialize rectangles for bkg stars and condensing statge
    rectangle = new WiremapRectangle(map, 0,0,0,0,4,1,2,0,0);

    // initialize pulsar
    lighthouse = new WiremapLighthouse(map, width/2, height/2, 10, color(265, 120, 45), random(10,20), 10, 5, color(255, 255, 255));

    // initialize bkg stars
    xbkg = new float[nbkg];
    ybkg = new float[nbkg];
    zbkg = new float[nbkg];
    col  = new int[nbkg];
    initialize();
    setFrequency(1); //oscillations per second

  }

  public void initialize() {
    /*
    float endOfLifeWeight = random(1);
     if (endOfLifeWeight < .33) {
     c_eol = 0;
     } else if(endOfLifeWeight >= .33 && endOfLifeWeight < .66) {
     c_eol = 1;
     } else {
     c_eol = 2;
     }
     */
    float redValue = red(starColor);
    float greenValue = green(starColor);
    float blueValue = blue(starColor);
    
    if(redValue >= greenValue && redValue >= blueValue) {
       c_eol = 0; 
    } else if(greenValue >= redValue && greenValue >= blueValue) {
       c_eol = 1; 
    } else {
       c_eol = 2; 
    }

    for (int i = 0; i< nbkg; i++){
      xbkg[i] = (random(0,width));
    }
    for (int i = 0; i< nbkg; i++){
      ybkg[i] = (random(0,height));
    }
    for (int i = 0; i< nbkg; i++){
      zbkg[i] = (random(0,depth));
    }

    // times
    tbkg1  =millis();
    tbkg2  =tbkg1 + 10  * 1000; 
    
    tbkg1a = tbkg1 + 9*1000;
    tbkg2a = tbkg1 + 18*1000;

    tsph1a =tbkg1 + 9   * 1000;//fade in
    tsph2a =tbkg1 + 18  * 1000;  

    tsph1b =tbkg1 + 9  * 1000;//enlarge
    tsph2b =tbkg1 + 20  * 1000;

    tsph1c =tbkg1 + 22 * 1000;//fade to color
    tsph2c =tbkg1 + 29 * 1000;

    tosc1a =tbkg1 + 32 * 1000;//osc radius
    tosc2a =tbkg1 + 62 * 1000;

    tosc1b =tbkg1 + 0;//1130 * 1000;// osc color
    tosc2b =tbkg1 + 0;//1132 * 1000;  
    teol1  =tbkg1 + 65 * 1000;// end
    teol2  =tbkg1 + 85 * 1000;
    teol3  =tbkg1 + 115 * 1000;

    //test times; do not erase
/*
    tbkg2  =tbkg1 + 1  * 1000; 

    tsph1a =tbkg1 + 2   * 1000;//fade in
    tsph2a =tbkg1 + 3  * 1000;  

    tsph1b =tbkg1 + 3 * 1000;//enlarge
    tsph2b =tbkg1 + 5  * 1000;

    tsph1c =tbkg1 + 3 * 1000;//fade to color
    tsph2c =tbkg1 + 5 * 1000;

    tosc1a =tbkg1 + 5 * 1000;//osc radius
    tosc2a =tbkg1 + 10 * 1000;

    tosc1b =tbkg1 + 0;//1130 * 1000;// osc color
    tosc2b =tbkg1 + 0;//1132 * 1000;  
    teol1  =tbkg1 + 10 * 1000;// end
    teol2  =tbkg1 + 15 * 1000;
    teol3  =tbkg1 + 21 * 1000;
    */
  }

  public void setFrequency(float frequency) {
    freq_osc = frequency;
  }

  public boolean isEnded() {
    return millis() >= teol3;
  }

  public int getEndState() {
    return c_eol;
  }
  public int getStarState(){
    return starState;
  }
  
  public void setColor(color c) {
     starColor = c; 
  }

  void display() {
    background(0);
    time = millis();

    // BKG
    // fade in bkg particles:
    if (time < tbkg2 && time >= tbkg1) {
      alpha_bkg = 255*(time-tbkg1)/(tbkg2-tbkg1); 
      starState = 1;
    }
    
    if (time < tbkg2a && time >= tbkg1a) {
      alpha_bkg = 255 - 155*(time-tbkg1a)/(tbkg2a-tbkg1a);
    }

    pdraw(rectangle, xbkg, ybkg,zbkg,col,alpha_bkg,nbkg);

    // SPHERE
    // fade from black
    if (time < tsph2a && time >= tsph1a){
          starState = 2;
      float c1 = 255*(time-tsph1a)/(tsph2a-tsph1a); 
      glowingSphere.setBaseColor(color(c1,c1,c1,c1));
      glowingSphere.setCoreColor(color(c1,c1,c1,c1));
    }

    // grow 
    if (time <= tsph2b && time > tsph1b){
      rad0 = grow(r0, 7, 1. ,tsph1b, tsph2b);
       starState = 3;
    }

    // fade from white to red and orange
    if (time < tsph2c && time >= tsph1c){
                  starState = 4;
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
starState = 5;
      rad1 = osc( c_osc, amp_rad_random, rad0, freq_osc, tosc1a, tosc2a );
      //      rad1 = osc( c_osc, 2. , rad0, freq_osc, tosc1a, tosc2a );  
    }

    // eol
    if (time >= teol1 && time < teol3){ 
      
      eol(c_eol, rad1, freq_osc, teol1, teol2, teol3); 
    }

    // display glowing sphere
    glowingSphere.display();
  }

  // Internal Functions
  float osc( int osc_type, float amp_rad , float rini, float freq,  float t1, float t2 ) {
    float out=0;
    if (osc_type == 0){ 
      float freq_rad =  freq/1000.;

      float rad = rini + (amp_rad   *(abs(sin(freq_rad     *(time-t1))))); 
      glowingSphere.setRadius(rad);
      out= rad;    
    }
    if (osc_type == 1){ 
      float freq_col_core = freq;
      float amp_col_core = 255;
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

  float grow( float rini, float rfin, float rate , float t1, float t2 ){
    float rad = rini + ((rfin-rini)*((time-t1)/(t2-t1)))*rate;
    glowingSphere.setRadius(rad); 
    return rfin;
  }

  void eol( int eol_type, float rini, float freq, float t1, float t2, float t3 ){
    if (eol_type == 0){
      float rand = random(2,9);
      float rad2 = osc(0, rand,  rini, freq,  t1, t2 );
      //float rad2 = osc(1, random(0,1)*255, rad1 ,1./250.,   t1, t2 );
      float rad = grow(rad2, 0, 1. ,t1, t3);
      starState = 7;
    }
    if (eol_type == 1){

      //fade to white
      int c1 = int(c0_base+ (255-c0_base)*(time-teol1)/(teol2-teol1)); 
      int c2 = int(c0_core+ (255-c0_core)*(time-teol1)/(teol2-teol1)); 
      glowingSphere.setBaseColor(c1);
      glowingSphere.setCoreColor(c2);
      float rad2 = osc(0, random(3,6),  rini , freq,  t1, t2 );
      float rad  = grow(rad2, 25., 1.5, t2, t3);
      println(rad+" "+rad2);
      starState =8 ;
    }

    if (eol_type == 2){  
      // fade sphere to black
      int c1 = int(c0_base- (c0_base)*(teol2-time)/(teol2-teol1)); 
      glowingSphere.setBaseColor(color(c1,c1,c1,c1));
      float rad2 = osc(0, random(3,5),  rini,freq,  t1, t2 );
      float rad  = grow(rad2, 2.2, 3., t1, t2);

      //fade pulsar to color
      int c2 = int(255*(time-teol1)/(teol2-teol1)); 
      lighthouse.setBaseColor(color(c2,255-c2,c2,c2));
      
      // rotate pulsar
      float rotation = 2*PI*freq/1000.;
      lighthouse.rotate(rotation);
      lighthouse.display();
      starState = 9;
    }
  }

  void pdraw(WiremapRectangle myrect, float[] x, float[] y, float[] z, int[] c, float myalpha, int n){
    for (int i = 0; i< n; i++){
      c[i] = color(255,255,255, myalpha);
      myrect.setBaseColor(c[i]);
      myrect.setPosition(x[i],y[i],z[i]);
      myrect.display();
    }
  }
}



