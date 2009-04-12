import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;
import wiremap.WiremapRectangle;

public class StarSimulation {
    private Wiremap wmap;
    private WiremapRectangle wrect;
    private int sqheight;
    private int sqwidth;
    private int sqdepth;

    /** Constants **/
    private final float tamin = 0;
    private final float t0min = 5000;
    private final float t1min = 10000;
    private final float t2min = 10000;
    private final float t2aamin = 10000;
    private final float dta = 5000;
    private final float dt0 = 5000;
    private final float dt1 = 5000;
    private final float dt2 = 5000;
    private final float dt3 = 5000;
    private final float dt4 = 15000;
    private final float dt2aa = 5000;
    private final float t2amin = 500000;
    private final float t2bmin = 15000;
    private final float dt2b = 5000;
    private final float t2cmin = 200000;
    private final float dt2c = 5000;
    private final float t3min = 20000;
    private final float t4min = 25000;
    private final float tamax = tamin + dta;
    private final float t0max = t0min + dt0;
    private final float t1max = t1min + dt1;
    private final float t2max = t2min + dt2;
    private final float t2aamax = t2aamin + dt2aa;
    private final float t2bmax = t2bmin + dt2b;
    private final float t2cmax = t2cmin + dt2c;
    private final float t3max = t3min + dt3;
    private final float t4max = t4min + dt4;
    private final float c_cond = 5.0;
    private final float amp_osc = .2;
    private final float amp_conv = .4; 
    private final float freq_conv = 9;
    private final float rbkg = 2.e2;
    private final int nbkg = 400;
    private final int nsph = 200;
    private final int nc = 1000;

    // evo color for using up material and for expressing inherent metallicity
    // create pulsar beacon

    private float freq_osc;
    private float xsph[], ysph[], zsph[] ;
    private float xbkg[], ybkg[], zbkg[], rsph[] ;
    private float rsph_max;
    private int  eol;
    private float zoom = 0;
    private float oscb = 1;

    // times
    private float dt2a;
    private float t2amax = t2amin  + dt2a;

    private final float c_bh = 4;
    private final float c_sn = 15;
    
    /**
    ** Set map to null to use regular graphics
    */
    public StarSimulation(Wiremap map) {
        if (wmap != null){
            wmap = map;
            wrect = new WiremapRectangle(wmap, 400,400, 5 , 255, sqwidth, sqheight, sqdepth, 0, 0);
        }

        initialize();
    }

    public void setFrequency(float frequency) {
        freq_osc = frequency;
        dt2a   = 2*PI/freq_osc;
        t2amax = t2amin + dt2a;
    }
    
    public boolean isEnded() {
      return millis() / 1000 >= t4max;
    }

    public void display() {
        float osc = 0;
        float tevo = millis();
        float rconv;
        float cevo_p = 255;

        if (tevo >= t2aamin && tevo <= t2aamax){ 
            cevo_p = 255* (tevo - t2aamin)/dt2aa;
        }

        // BACKGROUND SCENE
        if ( tevo >= t4min && tevo <= t4max && eol == 1) {
            background(255* (tevo - t4min)/dt4); // fade to white
        }
        else{
            background(0);
        }
        translate(width/2,height/2);

        // ===================================
        // === Background particles
        
        if (tevo > tamin && tevo <=tamax) { // === fade open particles
            zoom = 1;
        } else if ( tevo > t0min && tevo <=t0max){ // === rotate view
            rotateY(2*PI*(tevo-t0min)/dt0); // make decelerate; then move rotate down near scale
            zoom = 1;
        } else if ( tevo > t1min && tevo <=t1max ){ // === zoom in
            zoom = 1 + log(1 + (tevo-t1min)/dt1 );
        }

        if (tevo > tamin){
            stroke(constrain(255 * (tevo - tamin), 0, 255));
            pushMatrix();
            scale(zoom);
            drawPoints(xbkg, ybkg, zbkg, nbkg,0.,rbkg*1000000);
            popMatrix();
        }


        // ===================================
        // === Star particles  
        float dcolldt = zoom * 1./(1+c_cond*(tevo-t2min)/dt2); 
        if ( tevo >= t2min && tevo <=t2max ){         // condense (in expanding bkg)
            rotateY(2*PI*(tevo-t2min)/dt2);

            dcolldt = zoom * 1./(1+c_cond*(tevo-t2min)/dt2);
            scale(dcolldt);
        }

        float t;
        if ( tevo <= t2bmin) {
            oscb = 1;
        }

        if ( tevo >= t2bmin && tevo <=t2bmax){         // oscillate
            t = (tevo-t2bmin)/dt2b;
            oscb = dcolldt +  amp_osc*abs(sin(freq_osc*t));
            scale(oscb);
        }

        if ( tevo >= t3min && tevo <= t3max) {         // convection
            t = (tevo-t3min)/dt3;
            osc = oscb + amp_conv* abs(sin(freq_conv*t));
        }

        if ( tevo > t2min && tevo <=t3max ){           // DRAW PARTICLES
            stroke(cevo_p);
            scale(oscb); //TODO aren't we scaling this twice now?
            drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000);
        }

        if ( tevo  >=t3min && tevo <= t3max){  // DRAW PARTICLES (CONV)
            rconv = 0.3*rsph_max;
            scale(osc);
            stroke(204, 102, 0);
            drawPoints(xsph, ysph, zsph, nsph,rconv, rconv+5.);
        }

        // ===================================
        // === End life
        if ( tevo >=t4min && tevo <= t4max) {
            // BLACK HOLE
            if ( eol == 0 ) {
                stroke(255 * (t4max - tevo)/dt4);
                scale(oscb * 1./(1+c_bh*(tevo-t4min)/dt4)); 
                drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
            }

            // SUPERNOVA
            if ( eol == 1 ) {
                // particle blow out
                scale(oscb * exp(c_sn * pow((tevo-t4min)/dt4, 3))); 
                drawPoints(xsph, ysph, zsph, nsph,0., rsph_max*1000.);
                rotate(PI/3);
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

            for (int i = 0; i< n; i++){
                zw = (int)map(z[i], zmin, zmax, wzmin, wzmax);
                a = (int)x[i];
                b = (int)y[i];
                c = zw;
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
        float phi[] = new float[nsph];
        float the[] = new float[nsph];
        xsph = new float[nsph];
        ysph = new float[nsph];
        zsph = new float[nsph];
        rsph = new float[nsph];
        xbkg = new float[nbkg];
        ybkg = new float[nbkg];
        zbkg = new float[nbkg];


        float endOfLifeWeight = random(1);
        if (endOfLifeWeight < .33) {
            eol = 0;
        } else if(endOfLifeWeight >= .33 && endOfLifeWeight < .66) {
            eol = 1;
        } else {
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
        setFrequency(6);
    }
}
