
/* Please set TAB size to 4 */


#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <FreeImage.h>
#include <GL/glut.h>
#include "GLmain.h"


/* global variables */

int scr_x, scr_y;					/* screen resolution */

int wd, ht;							/* canvas size */
int theta = 0, phi = 0;				/* view orientation */
float zoom = 1.;					/* zoom ratio */
float pan_x = 0., pan_y = 0.;		/* pan shift */

int perspective = 1;				/* perspective projection */
int wireframe = 1;					/* wireframe skelecton */
int blending = 0;					/* blending factor */

int ptmode = 0;						/* mouse status */
float ptzo;							/* previous value */
int pto_x, pto_y;					/* previous position */

int nobj;							/* number of objects */
float obj_pos[N][3];				/* object coordinates */
float obj_norm[N][3];				/* tile orientation */
float obj_quad[N][2][3];			/* quad diagonal */
int obj_tex[N];						/* appearance texture */

GLsizei ntex;						/* number of textures */
GLuint tex[M];						/* texture binding */

float Rq = .01;						/* quad radius */

float Rd = 1.1;						/* viewing radius */
float eye[3];						/* eye position */



void cleanup(void)
{
#ifdef FREEIMAGE_LIB
	FreeImage_DeInitialise();
#endif // FREEIMAGE_LIB
}



int main( int argc, char* argv[] )
{
#ifdef FREEIMAGE_LIB
	FreeImage_Initialise();
#endif // FREEIMAGE_LIB

	atexit(cleanup);


	/* seed random number generator */
    srand((unsigned int)time(NULL));
  /*srand(1);*/


	/* first of all, load coordinates */
	loadCoordinates();
	/* then rescale to fit best */
	rescaleCoordinates();
	/* align them along a vector */
	alignCoordinates();
	/* generate one unit quad with random orientation for each object */
	calculatePresentatives();


	/* now should know where to load textures */
	obtainTextureList();


	/* initialise GLUT runtime */
    glutInit(&argc, argv);
	/* determine OpenGL window position and size */
    scr_x = glutGet(GLUT_SCREEN_WIDTH);
    scr_y = glutGet(GLUT_SCREEN_HEIGHT);
    glutInitWindowPosition(scr_x/8, scr_y/8);
    glutInitWindowSize(scr_x*2/3, scr_y*2/3);
	/* set display mode: RGBA pixel format, dual buffer, with z-depth enabled */
    glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);

	/* create OpenGL context to be used as primary canvas */
	if (glutCreateWindow("AstroViz - Release 0.1 - Press Esc to exit") == 0) {
		printf("Failed to setup GLUT\n");
		exit(1);
	}


	/* load textures from individual files and process them, then upload to OpenGL context */
	loadObjectTextures();
	/* randomly pick each object up a texture as its appearance */
	setObjectAppearances();


	/* register callback funtions for GLUT runtime */
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutSpecialFunc(special);
    glutKeyboardFunc(keyboard);
    glutMouseFunc(mouse);
    glutMotionFunc(motion);


	/* event-driven main loop of GLUT runtime */
	glutMainLoop();

	return 0;
}
