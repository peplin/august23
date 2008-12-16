
#include <math.h>
#include "util.h"



void normalize(float v[3])
{
    float r;

    r = sqrt( v[0]*v[0] + v[1]*v[1] + v[2]*v[2] );
    if (r == 0.0) return;

    v[0] /= r;
    v[1] /= r;
    v[2] /= r;
}


void cross(float v1[3], float v2[3], float result[3])
{
    result[0] = v1[1]*v2[2] - v1[2]*v2[1];
    result[1] = v1[2]*v2[0] - v1[0]*v2[2];
    result[2] = v1[0]*v2[1] - v1[1]*v2[0];
}



void LookAt(float x1, float y1, float z1,
			float x2, float y2, float z2,
		    float m[3][3])
{
    float forward[3], side[3], up[3];

    forward[0] = x1;
    forward[1] = y1;
    forward[2] = z1;

    up[0] = x2;
    up[1] = y2;
    up[2] = z2;

    normalize(forward);

    /* Side = forward x up */
    cross(forward, up, side);
    normalize(side);

    /* Recompute up as: up = side x forward */
    cross(side, forward, up);

	/*
    m[0][0] = 1; m[0][1] = 0; m[0][2] = 0
    m[1][0] = 0; m[1][1] = 1; m[1][2] = 0
    m[2][0] = 0; m[2][1] = 0; m[2][2] = 1
	*/

	m[0][0] = side[0];
    m[0][1] = side[1];
    m[0][2] = side[2];

    m[1][0] = up[0];
    m[1][1] = up[1];
    m[1][2] = up[2];

    m[2][0] = -forward[0];
    m[2][1] = -forward[1];
    m[2][2] = -forward[2];
}



void glmMultVectorByMatrix3f(float result[3], const float v[3], const float m[9])
{
  result[0]=m[0]*v[0]+m[3]*v[1]+m[6]*v[2];
  result[1]=m[1]*v[0]+m[4]*v[1]+m[7]*v[2];
  result[2]=m[2]*v[0]+m[5]*v[1]+m[8]*v[2];
}
