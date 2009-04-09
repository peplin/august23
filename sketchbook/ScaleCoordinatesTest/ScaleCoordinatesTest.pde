
void setup() {
    size(800, 600, P3D);

}

void draw() {
    background(0);
    int centerX = width/2;
    int centerY = height/2;
    translate(centerX, centerY);
    float scaleFactor = 1;
    scale(scaleFactor);
    float newXMargin = centerX/2/scaleFactor;
    float newYMargin = centerY/2/scaleFactor;
    translate(-newXMargin, -newYMargin);
    float newX = map(mouseX, 0, width, -newXMargin, newXMargin * 3);
    float newY = map(mouseY, 0, height, -newYMargin, newYMargin * 3);
    rect(newX, newY, 20, 20);
}
