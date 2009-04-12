import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;
import wiremap.WiremapRectangle;

public class StarSimulation {
    /** Constants **/
    private final float dta = 5000;
    private final float dt0 = 5000;
    private final float dt1 = 5000;
    private final float dt2 = 5000;
    private final float dt3 = 5000;
    private final float dt4 = 15000;
    private final float dt2aa = 5000;
    private final float dt2b = 5000;
    private final float dt2c = 5000;
    private final float c_cond = 5.0;
    private final float amp_osc = .2;
    private final float amp_conv = .4; 
    private final float freq_conv = 9;
    private final float rbkg = 2.e2;
    private final int nbkg = 400;
    private final int nsph = 200;
    private final int nc = 1000;
    private final float c_bh = 4;
    private final float c_sn = 15;

    private Wiremap wmap;
    private WiremapRectangle wrect;
    private int sqheight;
    private int sqwidth;
    private int sqdepth;

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
    private float tamin;
    private float t0min;
    private float t1min;
    private float t2min;
    private float t3min;
    private float t4min;
    private float tamax;
    private float t0max;
    private float t1max;
    private float t2max;
    private float t3max;
    private float t4max;
    private float t2aamin;
    private float t2aamax;
    private float t2bmin;
    private float t2bmax;
    private float t2cmin;
    private float t2cmax;
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
    
    public boolean isEnded() {
      return millis() >= t4max;
    }

    public int getEndState() {
        return eol;
    }

    public void setEndState(int endState) {
        eol = endState;
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
                pushMatrix();
                translate(-width/2, -height/2);
                pulsarSim.display();
                popMatrix();
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

        tamin = millis();
        tamax = tamin + dta;
        t0min = tamax;
        t0max = t0min + dt0;
        t1min = t0max;
        t1max = t1min + dt1;
        t2min = t1max;
        t2max = t2min + dt2;
        t3min = t2max;
        t3max = t3min + dt3;
        t4min = t3max;
        t4max = t4min + dt4;
        t2aamin = tamin + 10000;
        t2aamax = t2aamin + dt2aa;
        t2bmin = tamin + 15000; 
        t2bmax = t2bmin + dt2b;
        t2cmin = tamin + 20000;
        t2cmax = t2cmin + dt2c;

        float endOfLifeWeight = random(1);
        if (endOfLifeWeight < .33) {
            eol = 0;
        } else if(endOfLifeWeight >= .33 && endOfLifeWeight < .66) {
            eol = 1;
        } else {
            eol = 2;
        }

        eol=2;

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
