
#define N 1024
#define M 128

#define PI 3.14159265
#define SQRT3 1.73205081
#define DEG2RAD (PI/180.)

#define THETA 0
#define PHI 90


#define MAX(s,t) (((s) > (t)) ? (s) : (t))
#define MIN(s,t) (((s) < (t)) ? (s) : (t))


/* function prototypes for GLUT runtime callbacks */
void display(void);
void reshape(int w, int h);
void special(int key, int x, int y);
void keyboard(unsigned char key, int x, int y);
void mouse(int button, int state, int x, int y);
void motion(int x, int y);


void loadCoordinates(void);
void rescaleCoordinates(void);
void alignCoordinates(void);
void calculatePresentatives(void);


void obtainTextureList(void);
void loadObjectTextures(void);
void setObjectAppearances(void);
