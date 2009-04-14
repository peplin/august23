import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;
import wiremap.WiremapRectangle;

public class StarSimulation {
  /** Constants **/
  private final float c_cond = 5.0;
  private final float amp_osc = .2;
  private final float amp_osc_p = .2;
  private final float amp_osc_core = .2;
  private final float rbkg = 2.e2;
  private final int nbkg = 300;
  private final int nsph = 200;
  private final int nc = 1000;
  private final float c_bh = 4;
  private final float c_sn = 15;
  private float zoom = 0;
  
  private Wiremap wmap;
  private WiremapRectangle wrect;
  private int sqheight;
  private int sqwidth;
  private int sqdepth;

  private color starColor; 
  private float xsph[], ysph[], zsph[] ;
  private float xbkg[], ybkg[], zbkg[], rsph[] ;
  private float rsph_max;
  private int  eol;
  
  private float rad_core;
  private float rad_core0;
  private float freq_osc; 
  private float osc_p = 1;
  private float color_p;
  private float osc_core;
  private float color_core;
  private float scale_p;
  private float scale_core;
  private float scale0;

  // times
  private float teol1; 
  private float teol2;
  private float teol3;
  private float tbkg1;
  private float tbkg2;
  private float tbkg1a;
  private float tbkg2a;
  private float tbkg1b; 
  private float tbkg2b;
  private float tsph1;
  private float tsph2;
  private float tsph1a;
  private float tsph2a;
  private float tsph1b;
  private float tsph2b;
  private float tsph1c;
  private float tsph2c;
  private float tsph1d;
  private float tsph2d;
  private PulsarSimulation pulsarSim;

  /**
   ** Set map to null to use regular graphics
   */
  public StarSimulation(PApplet parent, Wiremap map) {
    if (wmap != null){
      wmap = map;
      wrect = new WiremapRectangle(wmap, 400,400, 5 , 255, sqwidth, sqheight, sqdepth, 0, 0);
    }
    pulsarSim = new PulsarSimulation(parent);
    initialize();
  }

  public void setFrequency(float frequency) {
    freq_osc = frequency;
  }

  public void setColor(color c) {
    starColor = c; 
  }

  public boolean isEnded() {
    return millis() >= teol3;
  }

  public int getEndState() {
    return eol;
  }

  public void setEndState(int endState) {
    eol = endState;
  }

  public void display() {
    float time = millis();
    float color_p = 255;


    // BACKGROUND SCENE
    if ( time >= teol1 && time <= teol3 && eol == 1) {
      background(255* (time - teol1)/(teol3-teol1)); // fade to white (SN)
    }
    else{
      background(0);
    }
    translate(width/2,height/2);

    // ===================================
    // === Background particles
    if (time > tbkg1 && time <=tbkg2) { // === fade open particles
      zoom = 1;
      color_p = 255* (time - tbkg1)/(tbkg2-tbkg1);
    } 
    else if ( time > tbkg1a && time <=tbkg2a){ // === rotate view
      rotateY(2*PI*(time-tbkg1a)/(tbkg2a-tbkg1a)); 
      zoom = 1;
    } 
    else if ( time > tbkg1b && time <=tbkg2b ){ // === zoom in
      zoom = 1 + log(1 + (time-tbkg1b)/(tbkg2b - tbkg1b) );
    }

    if (time > tbkg1){
      stroke(constrain(255 * (time - tbkg1), 0, 255));
      pushMatrix();
      scale(zoom);
      drawPoints(xbkg, ybkg, zbkg, nbkg,0.,rbkg*1000000);
      popMatrix();
    }

    // ===================================
    // === Star particles  
    // CONDENSE (in expanding bkg)
    if ( time >= tsph1 && time <=tsph2 ){    
      rotateY(2*PI*(time-tsph2)/(tsph2-tsph1));
      float scale0 = zoom * 1./(1 + c_cond*(time-tsph2)/(tsph2-tsph1));
      scale_p = scale0;
    }

    if ( time <= tsph2) {
      osc_p = 1;   
    }

    // FUSION starts
    if ( time >=tsph1a && time <= tsph2a){
      rad_core = rad_core + rad_core0*(time-tsph1)/(tsph2-tsph1);
    }

    if ( time >=tsph1b && time <= tsph2b){
      float t = (time-tsph1b)/(tsph2b-tsph1b);
      float c1 = 100 * t;
      float c2 = 255 * t;
      float color_core = color(255-c2,255-c2,255-c2, 0);
      stroke(color_core);
    }

    // oscillate
    if ( time >= tsph1c && time <=tsph2c){         
      float t = (time-tsph1c)/(tsph2c-tsph1c);
      osc_p = scale_p + amp_osc_p*abs(sin(freq_osc*t));
      osc_core = scale_core + amp_osc_core*abs(sin(freq_osc*t));      
    }

   
    // ===================================
    // === End life
    if ( time >=teol3 && time <= teol1) {
      // BLACK HOLE
      if ( eol == 0 ) {
        color_p = 255 * (teol3 - time)/(teol3-teol1);
        osc_p = 1 +  1./(1+c_bh*(time-teol1)/(teol3-teol1));       

      }

      // SUPERNOVA
      if ( eol == 1 ) {
        // particle blow out
        osc_p = exp(c_sn * pow((time-teol1)/(teol3-teol1), 3));
      }

      // PULSAR
      if (eol == 2) {
        float c_pulsar0 = 255 *(time-teol1)/(teol3-teol1);
        float c_pulsar1 = color(c_pulsar0, c_pulsar0,c_pulsar0, c_pulsar0);
        stroke(c_pulsar1);
        pushMatrix();
        pulsarSim.display();
        popMatrix();
      }
    }
    
    // ===================================
    // === DRAW PARTICLES
    if ( time > tsph1d && time <=tsph2d ){           // DRAW PARTICLES
    
      stroke(color_p);
      scale(osc_p); //TODO aren't we scaling this twice now? ... no maintains scaling.
      drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000);
      
      scale(osc_core);
      stroke(color_core);
      sphere(rad_core);
    }
    
  }

  private void drawPoints(float[] x, float[] y, float[] z, int n,
  float rmin, float rmax) {
    if(wmap != null) {
      int zw;
      float zmin = min(z);
      float zmax = max(z);
      float wzmin = 0.;
      float wzmax = 36.;
      int a,b,c;

      for (int i = 0; i< n; i++){
        zw = (int)map(z[i], zmin, zmax, wzmin, wzmax);
        a = (int)x[i];
        b = (int)y[i];
        c = zw;
        wrect.setPosition(a,b,c);
        wrect.display();
      }
    } 
    else {
      float r2;
      for (int j=0;j<n;j++){
        r2 = x[j]*x[j]+y[j]*y[j]+z[j]*z[j];
        if (r2 > rmin*rmin && r2 < rmax*rmax){
          point(x[j],y[j],z[j]);
        }
      } 
    }
  }

  public void initialize() {
    xsph = new float[nsph];
    ysph = new float[nsph];
    zsph = new float[nsph];
    rsph = new float[nsph];
    
    xbkg = new float[nbkg];
    ybkg = new float[nbkg];
    zbkg = new float[nbkg];

    tbkg1  = millis();
    tbkg2  = tbkg1 + 10*1000;
    tbkg1a = tbkg1 + 15*1000;
    tbkg2a = tbkg1 + 20*1000;
    tbkg1b = tbkg1 + *1000;
    tbkg2b = tbkg1 + *1000;
    tsph1  = tbkg1 + *1000;
    tsph2  = tbkg1 + *1000;
    tsph1a = tbkg1 + *1000;
    tsph2a = tbkg1 + *1000;
    tsph1b = tbkg1 + *1000;
    tsph2b = tbkg1 + *1000;
    tsph1c = tbkg1 + *1000;
    tsph2c = tbkg1 + *1000;
    tsph1d = tbkg1 + *1000;
    tsph2d = tbkg1 + *1000;

    teol3  = tbkg1 + *1000;
    teol2  = tbkg1 + *1000;
    teol1  = tbkg1 + *1000;

    float endOfLifeWeight = random(1);
    if (endOfLifeWeight < .33) {
      eol = 0;
    } 
    else if(endOfLifeWeight >= .33 && endOfLifeWeight < .66) {
      eol = 1;
    } 
    else {
      eol = 2;
    }

    float a0 = 40;
    float u1,u2;
    for(int i=0; i<nsph; i++) {
      u1 = random(0,1);
      u2 = random(0,1);
      xsph[i] =a0*PI*sqrt(-2*log(u1))*cos(2*PI*u2);
    }
    for(int i=0; i<nsph; i++) {
      u1 = random(0,1);
      u2 = random(0,1);
      ysph[i] =a0*PI*sqrt(-2*log(u1))*cos(2*PI*u2);
    }
    for(int i=0; i<nsph; i++) {
      u1 = random(0,1);
      u2 = random(0,1);
      zsph[i] =a0*PI*sqrt(-2*log(u1))*cos(2*PI*u2);
      rsph[i] = sqrt(xsph[i]*xsph[i]+ysph[i]*ysph[i]+zsph[i]*zsph[i]);
    }

    rsph_max = max(rsph);

    for (int i = 0; i<nbkg; i++){
      xbkg[i] = random(-rbkg,rbkg);
      ybkg[i] = random(-rbkg,rbkg);
      zbkg[i] = random(-rbkg,rbkg);
    }
    setFrequency(6);
  }
}

