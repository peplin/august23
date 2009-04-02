import codeanticode.gsvideo.*;

GSCapture video;

final int MINIMUM_DIFFERENCE_THRESHOLD = 100;
final float AVERAGE_PERCENTAGE_CHANGE = .05;

int[] previousFrame;

float averageColorR = 255;
float averageColorG = 255;
float averageColorB = 255;

float targetAverageR;
float targetAverageG;
float targetAverageB;

void setup() {
  size(320, 240, P3D); 
  video = new GSCapture(this, width, height, 24);
  previousFrame = new int[width * height];

  loadPixels();
}

void draw() {
    color averageColor = color(averageColorR, averageColorG, averageColorB);
    float averageHue = hue(averageColor);
    float averageSaturation = saturation(averageColor);
    float averageBrightness = brightness(averageColor);
    colorMode(HSB, 255);
    background(color(averageHue, averageSaturation, averageBrightness));
    colorMode(RGB);

    float newTargetR = 0;
    float newTargetG = 0;
    float newTargetB = 0;
    
    if (video.available()) {
        video.read(); 
        video.loadPixels();

        int changedPixelCount = 0;
        for (int i = 0; i < width * height; i++) { 
            color currentColor = video.pixels[i];
            color prevColor = previousFrame[i];
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
            // Render the difference image to the screen
            if(diffR < MINIMUM_DIFFERENCE_THRESHOLD
                    && diffR < MINIMUM_DIFFERENCE_THRESHOLD
                    && diffB < MINIMUM_DIFFERENCE_THRESHOLD) {
                newTargetR += currR;
                newTargetG += currG;
                newTargetB += currB;
                changedPixelCount++;
            }
            previousFrame[i] = currentColor;
        }

        if(changedPixelCount > 0) {
            newTargetR /= changedPixelCount;
            newTargetG /= changedPixelCount;
            newTargetB /= changedPixelCount;
            targetAverageR = (targetAverageR + newTargetR) / 2;
            targetAverageG = (targetAverageG + newTargetG) / 2;
            targetAverageB = (targetAverageB + newTargetB) / 2;
        }

        if(targetAverageR > averageColorR) {
            averageColorR *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorR, 0, targetAverageR);
        } else if(targetAverageR < averageColorR) {
            averageColorR *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorR, targetAverageR, 255);
        }
        if(targetAverageG > averageColorG) {
            averageColorG *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorG, 0, targetAverageG);
        } else if(targetAverageG < averageColorG) {
            averageColorG *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorG, targetAverageG, 255);
        }
        if(targetAverageB > averageColorB) {
            averageColorB *= 1.0 + AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorB, 0, targetAverageB);
        } else if(targetAverageB < averageColorB) {
            averageColorB *= 1.0 - AVERAGE_PERCENTAGE_CHANGE;
            constrain(averageColorB, targetAverageB, 255);
        }
    }
}
