/**
 * Twoverse Camera
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.util;

import processing.core.PApplet;

/**
This class is a wrapper for the 3D camera in Processing.

It provides a neat
way to store camera position, rotation and direction, as well as a good
interface for changing each value and applying them to the screen every time
through the draw() method.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
public class Camera {
    private float mEyeX; // position of the camera in space
    private float mEyeY;
    private float mEyeZ;
    private float mCenterX; // where the eye is looking (center of screen)
    private float mCenterY;
    private float mCenterZ;
    private float mScale;
    private float mCenterXVelocity;
    private float mCenterYVelocity;
    private float mScaleTarget;
    private PApplet mParent;

    /**
    Construct a new instance of the camera. Requires a reference to the parent
    applet.

    TODO make a constructor that just takes the parent and sets everything
    else to the Processing defaults
    */
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
    }

    /**
    Applies the current camera settings to the parent applet's view.
    Slows down the velocity of any translation or scale in progress.
    */
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
        mScale = PApplet.lerp(mScale, mScaleTarget, (float) .02);
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
