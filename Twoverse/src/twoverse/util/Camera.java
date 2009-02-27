package twoverse.util;

import processing.core.PApplet;

public class Camera {
    private float mEyeX = 0; // position of the camera in space
    private float mEyeY = -50;
    private float mEyeZ = 0;
    private float mCenterX = 0; // where the eye is looking (center of screen)
    private float mCenterY = 0;
    private float mCenterZ = 0;
    private float mUpX = 0; // 0.0, 1.0 or -1.0,to determine which axis is up
    private float mUpY = 1;
    private float mUpZ = 0;
    private PApplet mParent;

    public Camera(PApplet parent, float eyeX, float eyeY, float eyeZ, float centerX,
            float centerY, int centerZ, int upX, int upY, int upZ) {
        mEyeX = eyeX;
        mEyeY = eyeY;
        mEyeZ = eyeZ;
        mCenterX = centerX;
        mCenterY = centerY;
        mCenterZ = centerZ;
        mUpX = upX;
        mUpY = upY;
        mUpZ = upZ;
        mParent = parent;
    }

    public void setCamera() {
        mParent.camera(mEyeX,
                mEyeY,
                mEyeZ,
                mCenterX,
                mCenterY,
                mCenterZ,
                mUpX,
                mUpY,
                mUpZ);
    }

    public void moveEye(float differenceX, float differenceY, float differenceZ) {
        mEyeX += differenceX;
        mEyeY += differenceY;
        mEyeZ += differenceZ;
    }

    public void moveCenter(float differenceX, float differenceY,
            float differenceZ) {
        mCenterX += differenceX;
        mCenterY += differenceY;
        mCenterZ += differenceZ;
    }
    
    public void setXUp() {
        mUpX = 1;
        mUpY = 0;
        mUpZ = 0;
    }
    
    public void setYUp() {
        mUpX = 0;
        mUpY = 1;
        mUpZ = 0;
    }
    
    public void setZUp() {
        mUpX = 0;
        mUpY = 0;
        mUpZ = 1;
    }
}
