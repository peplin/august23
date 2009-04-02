import codeanticode.gsvideo.*;

public class ActiveColorGrabber {
    private final int MINIMUM_DIFFERENCE_THRESHOLD = 100;
    private final float AVERAGE_PERCENTAGE_CHANGE = .05;
    private final int AVERAGE_THRESHOLD = 10;
    private GSCapture mVideo;
    private int[] mPreviousFrame;
    private float mAverageColorR = 255;
    private float mAverageColorG = 255;
    private float mAverageColorB = 255;
    private float mTargetAverageR;
    private float mTargetAverageG;
    private float mTargetAverageB;

    public ActiveColorGrabber(PApplet parent) {
        parent.registerDraw(this);
        mVideo = new GSCapture(parent, width, height, 24);
        mPreviousFrame = new int[width * height];
    }

    public color getActiveColor() {
        color averageColor = color(mAverageColorR, mAverageColorG, mAverageColorB);
        /*float hue = hue(averageColor);
        float saturation = constrain(saturation(averageColor), 150, 255);
        float brightness = constrain(brightness(averageColor), 200, 255);;
        colorMode(HSB, 255);
        averageColor = color(hue, saturation, brightness);
        colorMode(RGB, 255);
        */
        return (color)averageColor;
    }

    //TODO consider going to black if there is no change
    public void draw() {
        float newTargetR = 0;
        float newTargetG = 0;
        float newTargetB = 0;
        
        if (mVideo.available()) {
            mVideo.read(); 
            mVideo.loadPixels();

            int changedPixelCount = 0;
            for (int i = 0; i < width * height; i++) { 
                color currentColor = mVideo.pixels[i];
                color prevColor = mPreviousFrame[i];
                // Extract the red, green, and blue components from current pixel
                int currR = (currentColor >> 16) & 0xFF; // Like red(), but faster
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
                    newTargetR += currR;
                    newTargetG += currG;
                    newTargetB += currB;
                    changedPixelCount++;
                }
                mPreviousFrame[i] = currentColor;
            }

            if(changedPixelCount > 0) {
                newTargetR /= changedPixelCount;
                newTargetG /= changedPixelCount;
                newTargetB /= changedPixelCount;
                mTargetAverageR = (mTargetAverageR + newTargetR) / 2;
                mTargetAverageG = (mTargetAverageG + newTargetG) / 2;
                mTargetAverageB = (mTargetAverageB + newTargetB) / 2;
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
