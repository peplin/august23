/**
 ** Active Color Grabber
 **
 ** by Christopher Peplin (chris.peplin@rhubarbtech.com)
 ** for August 23, 1966 (GROCS Project Group)
 ** University of Michigan, 2009
 **
 ** http://august231966.com
 ** http://www.dc.umich.edu/grocs
 **
 ** Copyright 2009 Christopher Peplin 
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at 
 ** http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 ** See the License for the specific language governing permissions and
 ** limitations under the License. 
 */

import codeanticode.gsvideo.*;
import java.awt.event.KeyEvent;

/**
 * This is a utility class for keeping track of the average active color in a
 * video capture feed. Rather than grab the overall average color of the screen,
 * this class only averages pixels that are determined to be "active".
 * 
 * In order to know which pixels are active, the class must be registered with
 * the parent applet in order to be called every time the draw() method is
 * called.
 * 
 * A pixel is considered "active" if all of the R, G and B values have changed
 * more than MINIMUM_DIFFERENCE_THRESHOLD. This value can be lowered to count
 * smaller changes in color/luminosity.
 * 
 * Once the count of active pixels is determined for each frame, the total is
 * compared with mBackgroundActivePixels. Since the video signal is usually
 * noisy, there will often be many "changed" pixels. By subtracting the average
 * number of "changed" pixels when there is actually no activity, we can filter
 * out most of this noise.
 * 
 * If there are truly a number of changed pixels, we set a new target average
 * color based on the average of all of the "changed" pixels. TODO This is
 * counting even the "background" pixels, which significantly darkens the image.
 * Every time draw() is called, this class moves the average color a small bit
 * closer to the target, giving a nice smooth color transition to anyone using
 * this class.
 * 
 * Interaction:
 * 
 * Press "B" to clear the background. Do this if the camera changes viewpoints,
 * or if the background image changes at all.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class ActiveColorGrabber {
    private final int MINIMUM_DIFFERENCE_THRESHOLD = 100;
    private final float AVERAGE_PERCENTAGE_CHANGE = .1;
    private final int AVERAGE_THRESHOLD = 10;
    private GSCapture mVideo;
    private int[] mPreviousFrame;
    private float mAverageColorR = 255;
    private float mAverageColorG = 255;
    private float mAverageColorB = 255;
    private float mTargetAverageR;
    private float mTargetAverageG;
    private float mTargetAverageB;
    private int mBackgroundActivePixels = 650000;
    private int mLastBackgroundActivePixels = 650000;

    /**
     * Construct a new ActiveColorGrabber.
     * 
     * Registers with the parent for draw and key events. Initializes video
     * capture feed to default capture device.
     * 
     * @param parent
     *            parent applet
     */
    public ActiveColorGrabber(PApplet parent) {
        parent.registerDraw(this);
        parent.registerKeyEvent(this);
        mVideo = new GSCapture(parent, 640, 480, 24);
        mPreviousFrame = new int[width * height];
    }

    /**
     * Returns a slightly brighter version of the current average active color.
     * 
     * This number is already stored - there is no video frame analysis in this
     * method, so it's pretty quick and cheap.
     * 
     * @return current active color
     */
    public color getActiveColor() {
        color averageColor =
                color(mAverageColorR, mAverageColorG, mAverageColorB);
        float hue = hue(averageColor);
        // optionally, brightens up colors a little because webcam is dark
        float saturation = constrain(saturation(averageColor) * 1.25, 0, 255);
        float brightness = constrain(brightness(averageColor) * 1.25, 0, 255);
        colorMode(HSB, 255);
        averageColor = color(hue, saturation, brightness);
        colorMode(RGB, 255);
        return (color) averageColor;
    }

    public void keyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_B) {
            mBackgroundActivePixels = mLastBackgroundActivePixels;
            println("Active pixel threshold is now " + mBackgroundActivePixels);
        }

    }

    public void draw() {
        float newTargetR = 0;
        float newTargetG = 0;
        float newTargetB = 0;

        if(mVideo.available()) {
            mVideo.read();
            mVideo.loadPixels();

            int changedPixelTotal = 0;
            for (int i = 0; i < width * height; i++) {
                color currentColor = mVideo.pixels[i];
                color prevColor = mPreviousFrame[i];
                // Extract the red, green, and blue components from current
                // pixel
                int currR = (currentColor >> 16) & 0xFF; // Like red(), but
                // faster
                int currG = (currentColor >> 8) & 0xFF;
                int currB = currentColor & 0xFF;
                // Extract red, green, and blue components from previous pixel
                int prevR = (prevColor >> 16) & 0xFF;
                int prevG = (prevColor >> 8) & 0xFF;
                int prevB = prevColor & 0xFF;
                // Compute the difference of the red, green, and blue values
                int diffR = abs(currR - prevR);
                int diffG = abs(currG - prevG);
                int diffB = abs(currB - prevB);
                if(diffR < MINIMUM_DIFFERENCE_THRESHOLD
                        && diffR < MINIMUM_DIFFERENCE_THRESHOLD
                        && diffB < MINIMUM_DIFFERENCE_THRESHOLD) {
                    changedPixelTotal += diffR + diffG + diffB;
                }
                newTargetR += currR;
                newTargetG += currG;
                newTargetB += currB;
                mPreviousFrame[i] = currentColor;
            }

            mLastBackgroundActivePixels = changedPixelTotal;
            if(changedPixelTotal > mBackgroundActivePixels) {
                newTargetR /= width * height;
                newTargetG /= width * height;
                newTargetB /= width * height;
                mTargetAverageR =
                        constrain((3 * mTargetAverageR + newTargetR) / 4,
                                0,
                                255);
                mTargetAverageG =
                        constrain((3 * mTargetAverageG + newTargetG) / 4,
                                0,
                                255);
                mTargetAverageB =
                        constrain((3 * mTargetAverageB + newTargetB) / 4,
                                0,
                                255);
            } else {
                mTargetAverageR = min(0, mTargetAverageR * .99);
                mTargetAverageG = min(0, mTargetAverageG * .99);
                mTargetAverageB = min(0, mTargetAverageB * .99);
            }

            if(mTargetAverageR > mAverageColorR + AVERAGE_THRESHOLD) {
                mAverageColorR *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorR, 0, mTargetAverageR);
            } else if(mTargetAverageR < mAverageColorR - AVERAGE_THRESHOLD) {
                mAverageColorR *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorR, mTargetAverageR, 255);
            }
            if(mTargetAverageG > mAverageColorG + AVERAGE_THRESHOLD) {
                mAverageColorG *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorG, 0, mTargetAverageG);
            } else if(mTargetAverageG < mAverageColorG - AVERAGE_THRESHOLD) {
                mAverageColorG *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorG, mTargetAverageG, 255);
            }
            if(mTargetAverageB > mAverageColorB + AVERAGE_THRESHOLD) {
                mAverageColorB *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorB, 0, mTargetAverageB);
            } else if(mTargetAverageB < mAverageColorB - AVERAGE_THRESHOLD) {
                mAverageColorB *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
                constrain(mAverageColorB, mTargetAverageB, 255);
            }
        }
    }
}
