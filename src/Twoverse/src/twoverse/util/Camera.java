package twoverse.util;

import processing.core.PApplet;

public class Camera {
    private float mEyeX; // position of the camera in space
    private float mEyeY;
    private float mEyeZ;
    private float mCenterX; // where the eye is looking (center of screen)
    private float mCenterY;
    private float mCenterZ;
    private float mScale;
    private PApplet mParent;
    
    private float mCenterXVelocity;
    private float mCenterYVelocity;
    private float mScaleTarget;

    public Camera(PApplet parent, float eyeX, float eyeY, float eyeZ,
            float centerX, float centerY, int centerZ, float scale) {
        mEyeX = eyeX;
        mEyeY = eyeY;
        mEyeZ = eyeZ;
        mCenterX = centerX;
        mCenterY = centerY;
        mCenterZ = centerZ;
        mScale = scale;
        mScaleTarget = scale;
        mParent = parent;
        //mParent.pushMatrix();
    }

    public void setCamera() {
        mParent.camera(mEyeX,
                mEyeY,
                mEyeZ,
                (float) (mParent.width / 2.0),
                (float) (mParent.height / 2.0),
                0,
                0,
                1,
                0);

        mCenterX += mCenterXVelocity;
        mCenterY += mCenterYVelocity;
        
        mParent.translate(mCenterX, mCenterY);
        mParent.scale(mScale);
        
        mCenterXVelocity *= .75;
        mCenterYVelocity *= .75;
        mScale = mParent.lerp(mScale, mScaleTarget, (float) .02);
    }

    public void moveEye(float differenceX, float differenceY, float differenceZ) {
        mEyeX += differenceX;
        mEyeY += differenceY;
        mEyeZ += differenceZ;
    }
    
    public void zoom(float difference) {
        mScaleTarget += difference;
    }
    
    public void resetScale() {
        mScaleTarget = 1;
    }
    
    public void changeTranslateVelocity(float differenceX, float differenceY) {
        mCenterXVelocity += differenceX * .1;
        mCenterYVelocity += differenceY * .1;
    }
    
    public float getCenterX() {
        return mCenterX;
    }
    
    public float getCenterY() {
        return mCenterY;
    }
    
    public float getScale() {
        return mScale;
    }
}
