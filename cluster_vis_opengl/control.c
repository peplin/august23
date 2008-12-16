
#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>
#include "GLmain.h"


extern int scr_x, scr_y;

extern int theta, phi;
extern float zoom;
extern float pan_x, pan_y;

extern int perspective;
extern int wireframe;
extern int blending;

extern int ptmode;
extern float ptzo;
extern int pto_x, pto_y;

extern float Rq;


/* this function handles special keyboard input event */
void special(int key, int x, int y)
{
    switch(key) {

    case GLUT_KEY_LEFT:
        theta+=-15+360;
        theta%=360;
        break;
    case GLUT_KEY_RIGHT:
        theta+=+15;
        theta%=360;
        break;
    case GLUT_KEY_UP:
        phi+=+15;
        phi%=360;
        break;
    case GLUT_KEY_DOWN:
        phi+=-15+360;
        phi%=360;
        break;

    default:
        printf("unassigned special,  code=%3d\n", key);
        return;
    }

    glutPostRedisplay();
}


/* this function handles standard keyboard input event */
void keyboard(unsigned char key, int x, int y)
{
    int modifiers = glutGetModifiers();

    switch(key) {

    case 27:	/* Esc */
        exit(0);
        return;

	case ' ':	/* space */
		break;

	case 'p':	/* P */
	case 'P':
		perspective=!perspective;
		break;

	case 'w':	/* W */
	case 'W':
		wireframe=!wireframe;
		break;

	case 'b':	/* B */
	case 'B':
		blending++;
		if (blending>2) blending=0;
		break;

    default:
		printf("unassigned keyboard, code=%3d\n", key);
        return;
    }

    glutPostRedisplay();
}


/* this function handles mouse button event */
void mouse(int button, int state, int x, int y)
{
    int modifiers = glutGetModifiers();

    switch(button) {
    case GLUT_LEFT_BUTTON:
        if (state==GLUT_DOWN && ptmode==0) {
            pto_x=x;
            pto_y=y;

			if (modifiers==GLUT_ACTIVE_SHIFT) {
				ptzo=Rq;
				ptmode=1;		/* adjust quad size */
			} else if (modifiers==GLUT_ACTIVE_CTRL) {
                ptzo=zoom;
                ptmode=2;		/* zoom */
            } else if (modifiers==GLUT_ACTIVE_ALT) {
                ptmode=3;		/* pan */
            } else {
                ptmode=4;		/* rotate as usual */
            }
        } else if (state==GLUT_UP && ptmode<5) {
            ptmode=0;
        }
        break;
    case GLUT_MIDDLE_BUTTON:
		break;
	case GLUT_RIGHT_BUTTON:
		break;
	default:   /* should never be executed */
        return;
    }

    glutPostRedisplay();
}


/* this function handles mouse motion event */
void motion(int x, int y)
{
    switch(ptmode) {
	case 0:
		return;

    case 1:		/* adjust quad size */
        if (y<pto_y) {
            Rq=ptzo/(1.+(pto_y-y)/(.5*scr_y)*3.);
            Rq=MAX(Rq,.0005);
        } else if (y>pto_y) {
            Rq=ptzo*(1.+(y-pto_y)/(.5*scr_y)*3.);
            Rq=MIN(Rq,.1);
		}
        break;
    case 2:		/* zoom */
        if (y<pto_y) {
            zoom=ptzo/(1.+(pto_y-y)/(.5*scr_y)*3.);
            zoom=MAX(zoom,.02);
        } else if (y>pto_y) {
            zoom=ptzo*(1.+(y-pto_y)/(.5*scr_y)*3.);
            zoom=MIN(zoom,50.);
		}
        break;
    case 3:		/* pan */
        pan_x+=(x-pto_x)/(.5*MIN(scr_x,scr_y))*3./zoom;
        pan_y+=(pto_y-y)/(.5*MIN(scr_x,scr_y))*3./zoom;
        pto_x=x;
        pto_y=y;
        break;
    case 4:		/* rotate as usual */
        theta+=(x-pto_x)+(scr_x+359)/360*360;
        phi  +=(pto_y-y)+(scr_y+359)/360*360;
        theta%=360;
        phi%=360;
        pto_x=x;
        pto_y=y;
        break;

    default:
        return;
    }

    glutPostRedisplay();
}
