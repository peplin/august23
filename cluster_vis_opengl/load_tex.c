
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <FreeImage.h>
#include <GL/glut.h>
#include "GLmain.h"


char tex_file[M][256];

extern int nobj;							/* number of objects */
extern int obj_tex[N];						/* appearance texture */

extern GLsizei ntex;						/* number of textures */
extern GLuint tex[M];						/* texture binding */



void obtainTextureList(void)
{
	FILE* fp;
	char buf[256];

	if ( (fp=fopen("res/tex_list.txt","r")) == NULL ) {
		printf("Failed to load texture list\n");
		exit(1);
	}
	ntex=0;
	while (feof(fp)==0) {
		if (ntex==M) {
			printf("Failed to load so much textures\n");
			exit(1);
		}
		if ( fscanf(fp, "%s", buf) == 1) {
			strcpy(tex_file[ntex], "res/");
			strcat(tex_file[ntex], buf);
			ntex++;
		}	/* else NULL lines */
	}
	fclose(fp);

	printf("loaded texture list with %d entries\n", ntex);

	/*
	printf("%s\n", tex_file[ntex-1]);
	*/
}



FIBITMAP* GenericLoader(const char* lpszPathName, int flag) {
	FREE_IMAGE_FORMAT fif = FIF_UNKNOWN;

	// check the file signature and deduce its format
	// (the second argument is currently not used by FreeImage)
	fif = FreeImage_GetFileType(lpszPathName, 0);
	if(fif == FIF_UNKNOWN) {
		// no signature ?
		// try to guess the file format from the file extension
		fif = FreeImage_GetFIFFromFilename(lpszPathName);
	}
	// check that the plugin has reading capabilities ...
	if((fif != FIF_UNKNOWN) && FreeImage_FIFSupportsReading(fif)) {
		// ok, let's load the file
		FIBITMAP *dib = FreeImage_Load(fif, lpszPathName, flag);
		// unless a bad file format, we are done !
		return dib;
	}
	return NULL;
}


BOOL GenericWriter(FIBITMAP* dib, const char* lpszPathName, int flag)
{
	FREE_IMAGE_FORMAT fif = FIF_UNKNOWN;
    BOOL bSuccess = FALSE;

	if(dib) {
		// try to guess the file format from the file extension
		fif = FreeImage_GetFIFFromFilename(lpszPathName);
		if(fif != FIF_UNKNOWN ) {
			// check that the plugin has sufficient writing and export capabilities ...
			WORD bpp = FreeImage_GetBPP(dib);
			if(FreeImage_FIFSupportsWriting(fif) && FreeImage_FIFSupportsExportBPP(fif, bpp)) {
				// ok, we can save the file
				bSuccess = FreeImage_Save(fif, dib, lpszPathName, flag);
				// unless an abnormal bug, we are done !
			}
		}
	}
    return (bSuccess == TRUE) ? TRUE : FALSE;
}


FIBITMAP* CreateAlphaFromLightness(FIBITMAP *src) {
	// create a 32-bit image from the source
	FIBITMAP *dst = FreeImage_ConvertTo32Bits(src);

	// create a 8-bit mask
  //FreeImage_Invert(src);
	FIBITMAP *mask = FreeImage_ConvertTo8Bits(src);
	FreeImage_Invert(src);

	// insert the mask as an alpha channel
	FreeImage_SetChannel(dst, mask, FICC_ALPHA);

	// free the mask and return
	FreeImage_Unload(mask);

	return dst;
}



void loadObjectTextures(void)
{
	int i;
	FIBITMAP *src,*dst,*tmp;
	BYTE *pixels;

	glGenTextures(ntex, tex);

	for(i=0;i<ntex;i++) {
	src = GenericLoader(tex_file[i], 0);

	if(src) {
		if (i==0) {
		tmp = FreeImage_Rescale(src, 256, 256, FILTER_BICUBIC);
		} else {
		tmp = FreeImage_Rescale(src, 64, 64, FILTER_BICUBIC);
		}
		// Create a transparent image from the lightness image of src
		dst = CreateAlphaFromLightness(tmp);
		// Free tmp
		FreeImage_Unload(tmp);
		if(dst) {
			pixels = (BYTE*)FreeImage_GetBits(dst);

			glBindTexture( GL_TEXTURE_2D, tex[i] );
			glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
			glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );

			glTexImage2D( GL_TEXTURE_2D, 0, 4,
						  FreeImage_GetWidth(dst), FreeImage_GetHeight(dst), 0,
						  GL_BGRA_EXT, GL_UNSIGNED_BYTE, pixels );

			// Free pixels
			// should we free pixels manually?
		  //FreeImage_Unload(pixels);

			// Free dst
			FreeImage_Unload(dst);
		}

		// Free src
		FreeImage_Unload(src);

		printf("%3d -> texture in %s loaded\n",i,tex_file[i]);
	}
	}
}


void setObjectAppearances(void)
{
	int i;

	obj_tex[0]=0;

	for(i=1;i<nobj;i++) {
		obj_tex[i]=((double)rand()/((double)(RAND_MAX)+1.))*(ntex-1)+1;
		if (obj_tex[i]==0) {
			printf("object texture id set to zero\n");
		} else if (obj_tex[i]==ntex) {
			printf("object texture id overflow\n");
		}
	}
}
