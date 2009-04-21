import processing.opengl.*;
import ddf.minim.*;
import ddf.minim.signals.*;

Minim minim;
AudioOutput out;
SineWave sine;
AudioPlayer groove;

PImage bg;
PImage sun,mercury,venus,earth,mars,jupiter,saturn,uranus,neptune,pluto;
float t = 0.;
float theta=PI/2.;
float xs,ys,zs,xe,ye,ze,xv,yv,zv,xm,ym,zm,xmr,ymr,zmr,xj,yj,zj,xsa,ysa,zsa,xu,yu,zu,xn,yn,zn,xp,yp,zp;
float eyex,eyey,eyez;
float ss = 1.;  // the scales
int sDetail = 40;  //Sphere detail setting
float rotationX = 0;
float rotationY = 0;
float velocityX = 0;
float velocityY = 0;
//float globeRadius = 300;
float pushBack = 0;

float[] cx,cz,sphereX,sphereY,sphereZ;
float sinLUT[];
float cosLUT[];
float SINCOS_PRECISION = 0.5f;
int SINCOS_LENGTH = int(360.0 / SINCOS_PRECISION);
PFont fontA;


void setup()
{
  size(900, 600, OPENGL);  
  bg=loadImage("nightsky.jpg");
  
  fontA = loadFont("Ziggurat-HTF-Black-32.vlw");
  textFont(fontA, 32);
   
  sun = loadImage("sunmap.jpg");  
  mercury = loadImage("mercurymap.jpg");
  venus = loadImage("venusmap.jpg");
  earth = loadImage("earthmap.jpg"); 
  mars = loadImage("marsmap.jpg");
  jupiter = loadImage("jupitermap.jpg");  
  saturn = loadImage("saturnmap.jpg");
  uranus = loadImage("uranusmap.jpg");
  neptune = loadImage("neptunemap.jpg");
  pluto = loadImage("plutomap.jpg");
  initializeSphere(sDetail);
  

  minim = new Minim(this);
  // get a line out from Minim, default bufferSize is 1024, default sample rate is 44100, bit depth is 16
  out = minim.getLineOut(Minim.STEREO);
  // create a sine wave Oscillator, set to 440 Hz, at 0.5 amplitude, sample rate from line out
  // sine = new SineWave(440, 0.5, out.sampleRate());
  sine = new SineWave(600, 1, 50000);     
  // set the portamento speed on the oscillator to 200 milliseconds
  sine.portamento(20);
  // add the oscillator to the line out
 // out.addSignal(sine);  // sine monotone 
  
  //--------music--------------------
  groove = minim.loadFile("gaoshan.mp3", 800);
  groove.sampleRate();
  groove.loop();
  
}


void draw()
{    
   background(0);  
   fill(255);
   text("Solar System - August23",180,50);
 //  draw_circle(250.);
   
  if(mousePressed) {
    eyex=mouseX;
    eyey=mouseY;
    eyez=mouseX*5;
  } else {
    eyex=width/2.;
    eyey=300;
    eyez=800;;
  }
  
 
  
 // lights();    // light up the Sun
  xs=width/2.; // Sun coordinates
  ys=width/2.;
  zs=0.;
 
  //-------mercury----------
  xm=90*ss*sin(theta)*sin(2*t);
  ym=-90*ss*cos(theta);
  zm=90*ss*sin(theta)*cos(2*t);
 
 //------venus-----------
  xv=150*ss*sin(theta)*sin(1.5*t);
  yv=-150*ss*cos(theta);
  zv=150*ss*sin(theta)*cos(1.5*t);
 
  //-------earth----------
  xe=250*ss*sin(theta)*sin(t);
  ye=-250*ss*cos(theta);
  ze=250*ss*sin(theta)*cos(t);
  
   //------mars----------
  xmr=350*ss*sin(theta)*sin(0.8*t);
  ymr=-350*ss*cos(theta);
  zmr=350*ss*sin(theta)*cos(0.8*t);
  
  //------jupiter----------
  xj=450*ss*sin(theta)*sin(0.5*t);
  yj=-450*ss*cos(theta);
  zj=450*ss*sin(theta)*cos(0.5*t);
  
   //------saturn----------
  xsa=550*ss*sin(theta)*sin(0.3*t);
  ysa=-550*ss*cos(theta);
  zsa=550*ss*sin(theta)*cos(0.3*t);
  
    //------uranus----------
  xu=650*ss*sin(theta)*sin(0.1*t);
  yu=-650*ss*cos(theta);
  zu=650*ss*sin(theta)*cos(0.1*t);
  
    //------neptune----------
  xn=750*ss*sin(theta)*sin(0.08*t);
  yn=-750*ss*cos(theta);
  zn=750*ss*sin(theta)*cos(0.08*t);
  
    //------pluto----------
  xp=850*ss*sin(theta)*sin(0.05*t);
  yp=-850*ss*cos(theta);
  zp=850*ss*sin(theta)*cos(0.05*t);
  
  camera(eyex, eyey, eyez, xs, ys, zs, 0, 1, 0);
  
  //-----sun-----
  translate(xs,ys,zs);
  renderGlobe(sun,60.*ss); 
  
  //---mercury----
  translate(xm,ym,zm);
  renderGlobe(mercury,6.*ss);
  translate(-xm,-ym,-zm);
  
  //---venus----
  translate(xv,yv,zv);
  renderGlobe(venus,10.*ss);
  translate(-xv,-yv,-zv);
  
  //----earth-------
  translate(xe,ye,ze);
  renderGlobe(earth,10.*ss);
  translate(-xe,-ye,-ze);
  
  //----mars-------
  translate(xmr,ymr,zmr);
  renderGlobe(mars,12.*ss);
  translate(-xmr,-ymr,-zmr);
  
   //----jupiter-------
  translate(xj,yj,zj);
  renderGlobe(jupiter,15.*ss);
  translate(-xj,-yj,-zj);
  /*
   //----saturn-------
  translate(xsa,ysa,zsa);
  renderGlobe(saturn,20.*ss);
  translate(-xsa,-ysa,-zsa);
  
  //----uranus-------
  translate(xu,yu,zu);
  renderGlobe(uranus,10.*ss);
  translate(-xu,-yu,-zu);
  
  //----neptune-------
  translate(xn,yn,zn);
  renderGlobe(neptune,11.*ss);
  translate(-xn,-yn,-zn);
  
  //----pluto-------
  translate(xp,yp,zp);
  renderGlobe(pluto,9.*ss);
  translate(-xp,-yp,-zp);
  */
   t=t+0.1;
   
}


void renderGlobe(PImage texmap, float globeRadius) 
{
  pushMatrix();
  //  translate(width/2.0, height/2.0, pushBack);
  pushMatrix();
  noFill();
  stroke(255,200);
  strokeWeight(2);
  smooth();
  popMatrix();
 // lights();    
  pushMatrix();
  rotateX( radians(-rotationX) );  
  rotateY( radians(270 - rotationY) );
  fill(200);
  noStroke();
  textureMode(IMAGE);  
  texturedSphere(globeRadius, texmap);
  popMatrix();  
  popMatrix();
  rotationX += velocityX;
  rotationY += velocityY;
  velocityX *= 0.95;
  velocityY *= 0.95;

  // Implements mouse control (interaction will be inverse when sphere is  upside down)
  if(mousePressed){
    velocityX += (mouseY-pmouseY) * 0.01;
    velocityY -= (mouseX-pmouseX) * 0.01;
  }
}

void initializeSphere(int res)
{
  sinLUT = new float[SINCOS_LENGTH];
  cosLUT = new float[SINCOS_LENGTH];

  for (int i = 0; i < SINCOS_LENGTH; i++) {
    sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
    cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
  }

  float delta = (float)SINCOS_LENGTH/res;
  float[] cx = new float[res];
  float[] cz = new float[res];

  // Calc unit circle in XZ plane
  for (int i = 0; i < res; i++) {
    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
  }

  // Computing vertexlist vertexlist starts at south pole
  int vertCount = res * (res-1) + 2;
  int currVert = 0;

  // Re-init arrays to store vertices
  sphereX = new float[vertCount];
  sphereY = new float[vertCount];
  sphereZ = new float[vertCount];
  float angle_step = (SINCOS_LENGTH*0.5f)/res;
  float angle = angle_step;

  // Step along Y axis
  for (int i = 1; i < res; i++) {
    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
    for (int j = 0; j < res; j++) {
      sphereX[currVert] = cx[j] * curradius;
      sphereY[currVert] = currY;
      sphereZ[currVert++] = cz[j] * curradius;
    }
    angle += angle_step;
  }
  sDetail = res;
}

// Generic routine to draw textured sphere
void texturedSphere(float r, PImage t) 
{
  int v1,v11,v2;
  // r = (r + 240 ) * 0.33;
  // r =  * 0.33;
  beginShape(TRIANGLE_STRIP);
  texture(t);
  float iu=(float)(t.width-1)/(sDetail);
  float iv=(float)(t.height-1)/(sDetail);
  float u=0,v=iv;
  for (int i = 0; i < sDetail; i++) {
    vertex(0, -r, 0,u,0);
    vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
    u+=iu;
  }
  vertex(0, -r, 0,u,0);
  vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
  endShape();   

  // Middle rings
  int voff = 0;
  for(int i = 2; i < sDetail; i++) {
    v1=v11=voff;
    voff += sDetail;
    v2=voff;
    u=0;
    beginShape(TRIANGLE_STRIP);
    texture(t);
    for (int j = 0; j < sDetail; j++) {
      vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
      vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
      u+=iu;
    }

    // Close each ring
    v1=v11;
    v2=voff;
    vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
    endShape();
    v+=iv;
  }
  u=0;

  // Add the northern cap
  beginShape(TRIANGLE_STRIP);
  texture(t);
  for (int i = 0; i < sDetail; i++) {
    v2 = voff + i;
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
    vertex(0, r, 0,u,v+iv);    
    u+=iu;
  }
  vertex(0, r, 0,u, v+iv);
  vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
  endShape();

}

/*

void draw_circle(float r)
{
  float xc=width/2.; // Sun coordinates
  float yc=width/2.;
  float zc=0.;
  float xx,yy,zz;
   
 for (int s = 0; s < 700; s++) 
 {
   fill(255);
   xx=r*sin(PI/2.)*sin(s/100.)+xc;
   yy=-r*cos(PI/2.)+yc;
   zz=r*sin(PI/2.)*cos(s/100.)+zc;
   point(xx,yy,zz);
   
 }
 
}

*/
