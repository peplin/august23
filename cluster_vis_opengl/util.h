
void normalize(float v[3]);

void cross(float v1[3], float v2[3], float result[3]);


void LookAt(float x1, float y1, float z1,
			float x2, float y2, float z2,
		    float m[3][3]);

void glmMultVectorByMatrix3f(float result[3], const float v[3], const float m[9]);
