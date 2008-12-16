
#include <GL/glut.h>

extern int wd;
extern int ht;

/* this function is what will be actually called
   when the OpenGL windows is reshaped */

void reshape(int w, int h)
{
	/* save current context dimension */
    wd=w;
    ht=h;
	/* canvas size should not be smaller than 512x512 */
    if (wd<512 || ht<512) {
        glutReshapeWindow(512, 512);
        return;
    }
	/* force GLUT to render the scene */
    glutPostRedisplay();
}
