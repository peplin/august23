
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "GLmain.h"
#include "util.h"


extern int nobj;							/* number of objects */
extern float obj_pos[N][3];					/* object coordinates */
extern float obj_norm[N][3];				/* tile orientation */
extern float obj_quad[N][2][3];				/* quad diagonal */


void loadCoordinates(void)
{
	FILE* fp;

	if ( (fp=fopen("galaxy_position.txt","r")) == NULL ) {
		printf("Failed to load positions\n");
		exit(1);
	}
	nobj=0;
	while (feof(fp)==0) {
		if (nobj==N) {
			printf("Failed to load so much positions\n");
			exit(1);
		}
		if ( fscanf(fp, "%f %f %f\n", &obj_pos[nobj][0], &obj_pos[nobj][1], &obj_pos[nobj][2]) == 3 ) {
			nobj++;
		}	/* else empty lines */
	}
	fclose(fp);

	printf("positions loaded for %d objects\n", nobj);

	/*
	printf("%f %f %f\n", x[nobj-2],y[nobj-2],z[nobj-2]);
	printf("%f %f %f\n", x[nobj-1],y[nobj-1],z[nobj-1]);
	*/
}


void rescaleCoordinates(void)
{
	int i;
	float rescale = .2f;

	for(i=0;i<nobj;i++) {
		obj_pos[i][0]*=rescale;
		obj_pos[i][1]*=rescale;
		obj_pos[i][2]*=rescale;
	}
}


void alignCoordinates(void)
{
	/* not implemented yet */
}


void calculatePresentatives(void)
{
	int i;
	int done;
    float t1,t2,s,t;
	float m[3][3];
	float p[3],q[3];
	float u[3],v[3];


	/* determine a random orientation for each object */
	for(i=0;i<nobj;i++) {
	done=0;
    while (done==0) {
        t1=((double)rand()/((double)(RAND_MAX)+1.)-.5)*2.;
        t2=((double)rand()/((double)(RAND_MAX)+1.)-.5)*2.;
        s=t1*t1+t2*t2;
        if (s<1.) {
            t=2.*sqrt(1.-s);
            obj_norm[i][0]=t1*t;
            obj_norm[i][1]=t2*t;
            obj_norm[i][2]=1.-2.*s;
            done=1;
        }
    }
	}

	/* calculate quad diagonal using its origin and normal */
	for(i=0;i<nobj;i++) {
		LookAt(obj_norm[i][0],obj_norm[i][1],obj_norm[i][2], 0.,0.,1., m);

		/* make a random unit vector on the plane */
		t=((double)rand()/((double)(RAND_MAX)+1.)-.5)*2.*PI;
		p[0]=cos(t);
		p[1]=sin(t);
		p[2]=0.;

		/* transform it back to the real coordiniates */
		glmMultVectorByMatrix3f(q,p,m);

		/* store this vector */
		obj_quad[i][0][0]=q[0];
		obj_quad[i][0][1]=q[1];
		obj_quad[i][0][2]=q[2];


		/* then another diagonal */

		u[0]=q[0];
		u[1]=q[1];
		u[2]=q[2];

		v[0]=obj_norm[i][0];
		v[1]=obj_norm[i][1];
		v[2]=obj_norm[i][2];

		cross(u,v,q);
		normalize(q);

		obj_quad[i][1][0]=q[0];
		obj_quad[i][1][1]=q[1];
		obj_quad[i][1][2]=q[2];
	}
}
