import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;
import wiremap.WiremapRectangle;

public class StarSimulation {
    private Wiremap wmap;
    private WiremapRectangle wrect;
    private int sqheight;
    private int sqwidth;
    private int sqdepth;
    private MultitouchModeInterface mListener;

    // evo color for using up material and for expressing inherent metallicity
    // create pulsar beacon

    private float bh,sn;
    private float rconv;
    private float rbkg;
    private int nosc;
    private float eyecheck;
    private float tamin,tamax,dt, cevo,t;
    private float cevo_p, cevo_bkg, cevo_eol;
    private float osc, freq, freq_osc, freq_conv;
    private float amp_osc, amp_conv;
    private float dcolldt;
    private float dt0, dt1, dt2,dt3,dt4,dt5,dt6,dta;
    private float dt2a,dt2b,dt2c;
    private float arot;
    private float drotdt;
    private float tau; // time constant for rotation
    private float the[];
    private float phi[];
    private float xsph[], ysph[], zsph[] ;
    private float xc[], yc[], zc[] ;
    private float xbkg[], ybkg[], zbkg[], rsph[] ;
    private float xsph0, ysph0, zsph0, rsph0;
    private float rsph_max;
    private float zoom;
    private float rcoll;
    private int i,k;
    private int  eol, evo;
    private int nbkg = 400;
    private int nsph = 200;
    private int nc = 1000;
    private float snback;

    // times
    private float tevo, t0min, t0max, t1min, t1max, t2min,t2max, t3min,t3max, t4min, t4max, t5min, t5max;
    private float t2amin, t2amax, t2bmin,t2bmax,t2cmin,t2cmax,t2aamin, t2aamax,dt2aa;
    private float osca=0,oscb=0,oscc=0;

    // core oscillation
    private float freq_core, amp_core;

    // neutron star
    private float fneut,s4, s4i, rate_neut;

    // supernovae
    private float fsn, rate_sn;

    private float c_bh;
    private float c_sn;
    private float c_cond;
    
    public void setFrequency(int frequency) {
      freq_osc = frequency;
    }
    
    public boolean isEnded() {
      return millis() / 1000 >= t4max;
    }

    /**
    ** Set map to null to use regular graphics
    */
    public StarSimulation(Wiremap map, MultitouchModeInterface listener) {
        size(1024,768, P3D);

        mListener = listener;

        if (wmap != null){
            wmap = map;
            wrect = new WiremapRectangle(wmap, 400,400, 5 , 255, sqwidth, sqheight, sqdepth, 0, 0);
        }

        initialize();

        float a0 = 10;
        rbkg = 2.e2;
        rsph0 = 4.e2;
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

        for(int i=0; i<nsph; i++) {
            u1 = random(0,1);
            u2 = random(0,1);
            phi[i] = PI*sqrt(-2*log(u1))*cos(2*PI*u2);
            the[i] = PI*sqrt(-2*log(u1))*sin(2*PI*u2);

            u1 = random(0,1);
            u2 = random(0,1);
            phi[i] = PI*sqrt(-2*log(u1))*cos(2*PI*u2);
            the[i] = PI*sqrt(-2*log(u1))*sin(2*PI*u2);
}

        for (int i = 0; i<nbkg; i++){
            xbkg[i] = random(-rbkg,rbkg);
            ybkg[i] = random(-rbkg,rbkg);
            zbkg[i] = random(-rbkg,rbkg);
        }

        eyecheck = random(-1,1);
        if (eyecheck < 0){
            eyecheck = -1;
        }
        else{
            eyecheck = 1;
        }

        // CONDENSE
        c_cond = 5.0;

        // OSCILLATION
        freq_osc = 6.;
        amp_osc = 0.2;

        // CONVECTION
        freq_conv = 9.;
        amp_conv = 0.4;


        // === Times
        // - Bkgnd
        dta = 5 * 1000; 
        dt0 = 5 * 1000;
        dt1 = 5 * 1000.;
        tamin  = 0 * 1000;  // fade
        t0min = 5 * 1000;  // rotation
        t1min = 10 * 1000;  // zoom

        // - Particles

        t2min   = 10 * 1000; // condense
        dt2   = 5 * 1000;

        t2aamin = 10 * 1000; // fade in
        dt2aa = 5 * 1000;

        // oscillation
        t2amin = 500 * 1000; // osc : ramp up
        dt2a   = nosc*2*PI/freq_osc ;//5   * 1000;
        t2bmin = 15  * 1000;  // osc
        dt2b   = 5   * 1000;
        t2cmin = 200 * 1000; // osc : ramp down
        dt2c   = 5.0 * 1000;

        // convection
        t3min = 20*1000; 
        dt3   = 5*1000;

        // - EOL
        t4min = 25 * 1000; 
        dt4   = 15 * 1000;

        tamax   = tamin   + dta;
        t0max   = t0min   + dt0;
        t1max   = t1min   + dt1;
        t2max   = t2min   + dt2;
        t2aamax = t2aamin + dt2aa;
        t2amax  = t2amin  + dt2a;
        t2bmax  = t2bmin  + dt2b;
        t2cmax  = t2cmin  + dt2c;
        t3max = t3min +dt3;
        t4max = t4min + dt4 ;
    }

    public void display() {
        tevo = millis();

        // BACKGROUND SCENE
        if ( tevo >=t4min && tevo <= t4max && eol == 1) {
            cevo_eol = 255* (tevo - t4min)/dt4; // fade to white
            background(cevo_eol);    
        }
        else{
            background(0);
        }
        translate(width/2,height/2,0.);

        // ===================================
        // === Background particles
        if (tevo > tamin && tevo <=tamax) { // === fade open particles
            cevo_bkg = 255* (tevo-tamin)/dta;
            stroke(cevo_bkg,cevo_bkg,cevo_bkg,cevo_bkg);
            zoom = 1;
        }

        if ( tevo > t0min && tevo <=t0max){ // === rotate view
            drotdt = 2*PI*(tevo-t0min)/dt0; // make decelerate; then move rotate down near scale
            rotateY(drotdt);
            zoom = 1;
        }

        if ( tevo > t1min && tevo <=t1max ){ // === zoom in
            zoom =1 + log( 1+ (tevo-t1min)/dt1 );
        }

        if (tevo > tamin){
            stroke(cevo_bkg,cevo_bkg,cevo_bkg,cevo_bkg);
            scale(zoom,zoom,zoom);
            drawPoints(xbkg, ybkg, zbkg, nbkg,0.,rbkg*1000000);
        }

        // ===================================
        // === Star particles  
        if ( tevo >= t2aamin && tevo <= t2aamax){      //fade
            cevo_p = 255* (tevo - t2aamin)/dt2aa;
        }

        if ( tevo >= t2min && tevo <=t2max ){         // condense (in expanding bkg)
            drotdt = 2*PI*(tevo-t2min)/dt2;
            rotateY(drotdt);

            dcolldt =zoom * 1./(1+c_cond*(tevo-t2min)/dt2); 
            scale(dcolldt,dcolldt,dcolldt);    
        }

        if ( tevo <= t2bmin) { 
            oscb = 1;
        }
        if ( tevo >= t2bmin && tevo <=t2bmax){         // oscillate
            t = (tevo-t2bmin)/dt2b;
            oscb = dcolldt +  amp_osc*abs(sin(freq_osc*t));
            scale(oscb,oscb,oscb);
        }

        if ( tevo >= t3min && tevo <= t3max) {         // convection
            t = (tevo-t3min)/dt3;
            osc = oscb + amp_conv* abs(sin(freq_conv*t));
        }

        if ( tevo > t2min && tevo <=t3max ){           // DRAW PARTICLES
            stroke(cevo_p,cevo_p,cevo_p,cevo_p);
            scale(oscb,oscb,oscb);
            drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
        }

        if ( tevo  >=t3min && tevo <= t3max){  // DRAW PARTICLES (CONV)
            rconv = 0.3*rsph_max;
            scale(osc,osc,osc);
            stroke(204, 102, 0);
            drawPoints(xsph, ysph, zsph, nsph,rconv, rconv+5.);
        }

        // ===================================
        // === End life
        if ( tevo >=t4min && tevo <= t4max) {
            // BLACK HOLE
            if ( eol == 0 ) {
            c_bh = 4.;
            cevo_eol = 255* (t4max - tevo)/dt4;
            stroke(cevo_eol,cevo_eol,cevo_eol,cevo_eol); 
            bh = oscb * 1./(1+c_bh*(tevo-t4min)/dt4); 
            scale(bh,bh,bh);
            drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
            }

            // SUPERNOVA
            if ( eol == 1 ) {
            // particle blow out
            c_sn = 15.0;
            sn =oscb * exp(c_sn*pow((tevo-t4min)/dt4,3.)); 
            scale(sn,sn,sn);  
            drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
            rotate(PI/3.);
            drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
            }

            // PULSAR
            if (eol == 2) {
                //TODO insert jiangang's code
            }
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

            // println(zmin+" "+zmax);
            
            for (int i = 0; i< n; i++){
                zw = (int)map(z[i], zmin, zmax, wzmin, wzmax);
                a = (int)x[i];
                b = (int)y[i];
                c = zw;
                ;
                println(a+" "+b+" "+c);
                wrect.setPosition(a,b,c);
                wrect.display();
            }
        } else {
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
        phi = new float[nsph];
        the = new float[nsph];

        xsph = new float[nsph];
        ysph = new float[nsph];
        zsph = new float[nsph];
        rsph = new float[nsph];

     /*   xc = new float[nsph];
        yc = new float[nsph];
        zc = new float[nsph];
        */

        xbkg = new float[nbkg];
        ybkg = new float[nbkg];
        zbkg = new float[nbkg];


        float c_eol = random(-1,1);
        if (c_eol < 0){
           eol = 0;
        }else{
          eol = 1;
        }
     

        i=0; 
        k = 0;

        xsph0 = 0; 
        ysph0 = 0; 
        zsph0 = 0;

        float a0 = 10;
        rbkg = 1.e2;
        rsph0 = 2.e2;
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

        for(int i=0; i<nsph; i++) {
            u1 = random(0,1);
            u2 = random(0,1);
            phi[i] = PI*sqrt(-2*log(u1))*cos(2*PI*u2);
            the[i] = PI*sqrt(-2*log(u1))*sin(2*PI*u2);

            u1 = random(0,1);
            u2 = random(0,1);
            phi[i] = PI*sqrt(-2*log(u1))*cos(2*PI*u2);
            the[i] = PI*sqrt(-2*log(u1))*sin(2*PI*u2);
        }

        for (int i = 0; i<nbkg; i++){
            xbkg[i] = random(-rbkg,rbkg);
            ybkg[i] = random(-rbkg,rbkg);
            zbkg[i] = random(-rbkg,rbkg);
        }

        eyecheck = random(-1,1);
        if (eyecheck < 0){
            eyecheck = -1;
        }
        else{
            eyecheck = 1;
        }

        // CONDENSE
        c_cond = 4.0;

        // OSCILLATION
        freq_osc = 6.;
        amp_osc = 0.3;

        // CONVECTION
        freq_conv = 9.;
        amp_conv = 0.4;


        // === Times
        // - Bkgnd
        dta = 5 * 1000; 
        dt0 = 5 * 1000;
        dt1 = 5 * 1000.;
        tamin  = 0 * 1000;  // fade
        t0min = 5 * 1000;  // rotation
        t1min = 10 * 1000;  // zoom

        // - Particles

        t2min   = 10 * 1000; // condense
        dt2   = 5 * 1000;

        t2aamin = 10 * 1000; // fade in
        dt2aa = 5 * 1000;

        // oscillation
        t2amin = 500 * 1000; // osc : ramp up
        dt2a   = nosc*2*PI/freq_osc ;//5   * 1000;
        t2bmin = 15  * 1000;  // osc
        dt2b   = 5   * 1000;
        t2cmin = 200 * 1000; // osc : ramp down
        dt2c   = 5.0 * 1000;

        // convection
        t3min = 20*1000; 
        dt3   = 5*1000;

        // - EOL
        t4min = 25 * 1000; 
        dt4   = 15 * 1000;

        tamax   = tamin   + dta;
        t0max   = t0min   + dt0;
        t1max   = t1min   + dt1;
        t2max   = t2min   + dt2;
        t2aamax = t2aamin + dt2aa;
        t2amax  = t2amin  + dt2a;
        t2bmax  = t2bmin  + dt2b;
        t2cmax  = t2cmin  + dt2c;
        t3max = t3min +dt3;
        t4max = t4min + dt4 ;
    }
}
