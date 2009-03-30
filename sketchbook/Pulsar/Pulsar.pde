/** Configuration Parameters **/
float beatsPerSecond = 1;
float beatDurationMs = 100;
float radius = 200;
float radiusScale = 1;
float minimumRadiusScale = .99;
float rotationPerMinute = 2;

/** Sketch globals */
float contractDistance = radiusScale - minimumRadiusScale;
float lastBeatMs = 0;
float lastDrawMs = 0;
float lastBeatKeyPress = 0;
float targetBeatsPerSecond = beatsPerSecond;
boolean contracting = false;

float rotationRad = 0;

void setup() {
    size(640, 480, P3D);
    frameRate(30);

    lastBeatMs = millis();
}

void draw() {
    background(0);
    lights();

    float now = millis();
    float scaleStep = contractDistance / beatDurationMs
            * (now - lastDrawMs);

    if(contracting) {
        radiusScale -= scaleStep * 2.0 / 3.0;
        if(radiusScale <= minimumRadiusScale) {
            contracting = false;
        }
    } else {
        radiusScale += scaleStep * 1.0 / 3.0;
        if(now - lastBeatMs >= beatsPerSecond * 1000) {
            contracting = !contracting;
            lastBeatMs = now;
        }
    }
    radiusScale = constrain(radiusScale, minimumRadiusScale, 1);
    rotationRad += rotationPerMinute  / 60.0 / 1000.0 * (now - lastDrawMs);

    translate(width/2, height/2);
    rotateY(rotationRad);
    sphere(radius * radiusScale);
    
    if(targetBeatsPerSecond > beatsPerSecond) {
        beatsPerSecond =
                constrain(beatsPerSecond + .001, 0, targetBeatsPerSecond);
    } else if(targetBeatsPerSecond < beatsPerSecond) {
        beatsPerSecond =
                constrain(beatsPerSecond - .001, 0, targetBeatsPerSecond);
    }
    lastDrawMs = now;
}

void keyPressed() {
    if(keyCode == UP) {
        beatsPerSecond += .1;
    } else if(keyCode == DOWN) {
        beatsPerSecond -= .1;
    } else if(keyCode == java.awt.event.KeyEvent.VK_SPACE) {
        float now = millis();
        targetBeatsPerSecond = (targetBeatsPerSecond + 
                ((now - lastBeatKeyPress) / 1000.0)) / 2.0;
        lastBeatKeyPress = now;
    }
}
