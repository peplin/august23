
#include <stdlib.h>
#include <math.h>
#include <GL/glut.h>
#include "GLmain.h"
#include "util.h"


extern int wd, ht;							/* canvas size */
extern int theta, phi;						/* view orientation */
extern float zoom;							/* zoom ratio */
extern float pan_x, pan_y;					/* pan shift */

extern int perspective;						/* perspective projection */
extern int wireframe;						/* wireframe skelecton */
extern int blending;						/* blending factor */

extern int nobj;							/* number of objects */
extern float obj_pos[N][3];					/* object coordinates */
extern float obj_norm[N][3];				/* tile orientation */
extern float obj_quad[N][2][3];				/* quad diagonal */
extern int obj_tex[N];						/* appearance texture */

extern GLsizei ntex;						/* number of textures */
extern GLuint tex[M];						/* texture binding */

extern float Rq;							/* quad radius */

extern float Rd;							/* viewing radius */
extern float eye[3];						/* eye position */


int indexes[N];
float depth[N];
float vertices[N][4][3];



/* setup canvas and camera */
void setup()
{
    float aspect;
    float stretch_h, stretch_v;
	float xe, ye, ze;

	/* rd should be 1. but we'd like get some extra space here */
	/*
	Rd = 1.1;
	*/

	/* eye position */
	xe = Rd*2.*SQRT3*cos(PHI*DEG2RAD)*cos(THETA*DEG2RAD);
	ye = Rd*2.*SQRT3*cos(PHI*DEG2RAD)*sin(THETA*DEG2RAD);
	ze = Rd*2.*SQRT3*sin(PHI*DEG2RAD);
	eye[0] = cos((PHI-phi)*DEG2RAD)*cos((THETA-theta)*DEG2RAD);
    eye[1] = cos((PHI-phi)*DEG2RAD)*sin((THETA-theta)*DEG2RAD);
    eye[2] = sin((PHI-phi)*DEG2RAD);

	/* canvas aspect ratio */
	aspect = (double)wd / (double)ht;

	/* horizontal and vertical strech ratio */
    if (aspect>1.) {
        stretch_h = aspect;
        stretch_v = 1.;
    } else {
        stretch_h = 1.;
        stretch_v = 1./aspect;
    }


    glViewport(0,0, wd,ht);


    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    if (perspective) {
        glFrustum(-Rd/zoom*stretch_h,Rd/zoom*stretch_h,
                  -Rd/zoom*stretch_v,Rd/zoom*stretch_v,
                   Rd*SQRT3,         Rd*3.*SQRT3);
    } else {
        glOrtho(-Rd*SQRT3/zoom*stretch_h,Rd*SQRT3/zoom*stretch_h,
                -Rd*SQRT3/zoom*stretch_v,Rd*SQRT3/zoom*stretch_v,
                 Rd*SQRT3,               Rd*SQRT3*3.);
    }


  /*glMatrixMode(GL_TEXTURE);
    glLoadIdentity();*/


    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glTranslatef(pan_x,pan_y,0.);
    gluLookAt(xe,ye,ze, 0.,0.,0., 0.,0.,1.);
    glRotatef(theta, 0.,0.,1.);
    glRotatef(phi, -sin((theta-THETA)*DEG2RAD),-cos((theta-THETA)*DEG2RAD),0.);
}


void refreshVertices(void)
{
	int i;
	float zfactor = 4.f;
	float u[3],v[3];

	/* for central object only */
    /*
	cross(obj_norm[0],eye,u);
    */
    v[0]=0.;
    v[1]=0.;
    v[2]=-1.;
    cross(v,eye,u);
	normalize(u);
	cross(u,eye,v);
	normalize(v);
	vertices[0][0][0]=obj_pos[0][0]+zfactor*Rq*u[0];
	vertices[0][0][1]=obj_pos[0][1]+zfactor*Rq*u[1];
	vertices[0][0][2]=obj_pos[0][2]+zfactor*Rq*u[2];
	vertices[0][2][0]=obj_pos[0][0]-zfactor*Rq*u[0];
	vertices[0][2][1]=obj_pos[0][1]-zfactor*Rq*u[1];
	vertices[0][2][2]=obj_pos[0][2]-zfactor*Rq*u[2];
	vertices[0][1][0]=obj_pos[0][0]+zfactor*Rq*v[0];
	vertices[0][1][1]=obj_pos[0][1]+zfactor*Rq*v[1];
	vertices[0][1][2]=obj_pos[0][2]+zfactor*Rq*v[2];
	vertices[0][3][0]=obj_pos[0][0]-zfactor*Rq*v[0];
	vertices[0][3][1]=obj_pos[0][1]-zfactor*Rq*v[1];
	vertices[0][3][2]=obj_pos[0][2]-zfactor*Rq*v[2];

	for(i=1;i<nobj;i++) {
		/* calculate vertices for this quad */
		/* clockwise order */
		vertices[i][0][0]=obj_pos[i][0]+Rq*obj_quad[i][0][0];
		vertices[i][0][1]=obj_pos[i][1]+Rq*obj_quad[i][0][1];
		vertices[i][0][2]=obj_pos[i][2]+Rq*obj_quad[i][0][2];
		vertices[i][2][0]=obj_pos[i][0]-Rq*obj_quad[i][0][0];
		vertices[i][2][1]=obj_pos[i][1]-Rq*obj_quad[i][0][1];
		vertices[i][2][2]=obj_pos[i][2]-Rq*obj_quad[i][0][2];
		vertices[i][1][0]=obj_pos[i][0]+Rq*obj_quad[i][1][0];
		vertices[i][1][1]=obj_pos[i][1]+Rq*obj_quad[i][1][1];
		vertices[i][1][2]=obj_pos[i][2]+Rq*obj_quad[i][1][2];
		vertices[i][3][0]=obj_pos[i][0]-Rq*obj_quad[i][1][0];
		vertices[i][3][1]=obj_pos[i][1]-Rq*obj_quad[i][1][1];
		vertices[i][3][2]=obj_pos[i][2]-Rq*obj_quad[i][1][2];
	}
}


/* compare function for qsort */
int compare(const void *p1,const void *p2)
{
	const int *i1 = p1, *i2 = p2;
	float val1 = depth[*i1];
	float val2 = depth[*i2];

	if (val1 < val2)
		return 1;
	else if (val1 > val2)
		return -1;
	else
		return 0;
}


void sortTilesByOrigin(void)
{
	int i;
	float m[9];

	LookAt(eye[0], eye[1], eye[2],
		   0., 0., 1.,
		   m);

	for(i=0;i<nobj;i++) {
		indexes[i]=i;
		depth[i]=m[2]*obj_pos[i][0]+m[5]*obj_pos[i][1]+m[8]*obj_pos[i][2];
	}

	qsort(indexes,nobj,sizeof(int),compare);
}


/* actually render the scene */
void render(void)
{
	int i, j;

	glDisable(GL_LIGHTING);
    glColor3f(1.,1.,1.);
	glEnable(GL_TEXTURE_2D);
    glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

	for(j=0;j<nobj;j++) {
		i=indexes[j];

		if (i==0)
		glBindTexture(GL_TEXTURE_2D, tex[0]);
		else
		glBindTexture(GL_TEXTURE_2D, tex[obj_tex[i]]);

		if (i==0 || fabs(obj_norm[i][0]*eye[0]+
						 obj_norm[i][1]*eye[1]+
						 obj_norm[i][2]*eye[2]) > 0.0871557) {	/* angle > 5 degrees */
		/* keep clockwise order */
	glBegin(GL_QUADS);
		glTexCoord2f(0.,0.);  glVertex3fv(vertices[i][0]);
		glTexCoord2f(0.,1.);  glVertex3fv(vertices[i][1]);
		glTexCoord2f(1.,1.);  glVertex3fv(vertices[i][2]);
		glTexCoord2f(1.,0.);  glVertex3fv(vertices[i][3]);
	glEnd();
		}
	}

    glDisable(GL_TEXTURE_2D);
}


void renderWireframe(void)
{
	int i;

	glDisable(GL_LIGHTING);

	/* the cube */
	glColor3f(1.,0.,0.);
	glutWireCube(2.);

	/*
	glColor3f(1.,1.,1.);
	glBegin(GL_POINTS);
	for(i=0;i<nobj;i++) {
		glVertex3fv(obj_pos[i]);
	}
	glEnd();
	*/

	/* normal */
	/*
	glColor3f(1.,1.,1.);
	glBegin(GL_LINES);
	for(i=0;i<nobj;i++) {
		glVertex3fv(obj_pos[i]);
		glVertex3f (obj_pos[i][0]+obj_norm[i][0]*Rq,
				 	obj_pos[i][1]+obj_norm[i][1]*Rq,
					obj_pos[i][2]+obj_norm[i][2]*Rq);
	}
	glEnd();
	*/

	/* base cross */
	glColor3f(1.,1.,0.);
	glBegin(GL_LINES);
	for(i=0;i<nobj;i++) {
		glVertex3fv(vertices[i][0]);
		glVertex3fv(vertices[i][2]);
		glVertex3fv(vertices[i][1]);
		glVertex3fv(vertices[i][3]);
	}
	glEnd();

	/* base quad */
	/* clockwise again */
	glColor3f(0.,1.,1.);
	for(i=0;i<nobj;i++) {
	glBegin(GL_LINE_LOOP);
		glVertex3fv(vertices[i][0]);
		glVertex3fv(vertices[i][1]);
		glVertex3fv(vertices[i][2]);
		glVertex3fv(vertices[i][3]);
	glEnd();
	}
}


/* this function is the entry point to render primary scene */
void display(void)
{
	/* routine preparation to render something properly */
    glEnable(GL_DEPTH_TEST);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  /*glDisable(GL_COLOR_MATERIAL);*/

	/* this is very important for realistic visual effect */
	switch (blending) {
		case 0:
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); break;
		case 1:
	glBlendFunc(GL_ONE, GL_ONE); break;
		case 2:
	glBlendFunc(GL_SRC_ALPHA, GL_ONE); break;

		default:	/* should never be executed */
		break;
	}


	/* setup canvas and camera */
	setup();

	/* some preparation */
	refreshVertices();
	sortTilesByOrigin();

	/* render primary scene */
	if (wireframe) renderWireframe();
    glDepthMask(GL_FALSE);
	glEnable(GL_BLEND);
	render();
	glDepthMask(GL_TRUE);
	glDisable(GL_BLEND);

	/* flush OpenGL pipeline */
	glFinish();

	/* swap front and back display buffer */
	glutSwapBuffers();
}
