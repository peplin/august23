import codeanticode.gsvideo.*;

GSCapture video;

final int MINIMUM_DIFFERENCE_THRESHOLD = 20;

int[] previousFrame;

float averageColorR;
float averageColorG;
float averageColorB;

void setup() {
  size(640, 480, P3D); 
  video = new GSCapture(this, width, height, 24);
  previousFrame = new int[width * height];

  loadPixels();
}

void draw() {
  if (video.available()) {
    video.read(); 
    video.loadPixels();

    averageColorR = 0;
    averageColorG = 0;
    averageColorB = 0;
    
    int movementSum = 0; 
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
        // Add these differences to the running tally
        movementSum += diffR + diffG + diffB;
        // Render the difference image to the screen
        //pixels[i] = currentColor; //color(diffR, diffG, diffB);
        if(diffR < MINIMUM_DIFFERENCE_THRESHOLD
                && diffR < MINIMUM_DIFFERENCE_THRESHOLD
                && diffB < MINIMUM_DIFFERENCE_THRESHOLD) {
           // pixels[i] = 0;
        } else {
            averageColorR += currR;
            averageColorG += currG;
            averageColorB += currB;
            changedPixelCount++;
        }
        previousFrame[i] = currentColor;
    }
    //println(changedPixelCount);
    averageColorR /= changedPixelCount;
    averageColorG /= changedPixelCount;
    averageColorB /= changedPixelCount;
    background(color(averageColorR, averageColorG, averageColorB));
    // To prevent flicker from frames that are all black (no movement),
    // only update the screen if the image has changed.
    //if (movementSum > 0) {
     // updatePixels();
    //}
  }
}
