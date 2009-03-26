import wiremap.Wiremap;
import wiremap.WiremapGlowingSphere;

Wiremap map;
WiremapGlowingSphere glowingSphere;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 24, 32, .25, .25, 7,
            "depth128.txt");

    glowingSphere = new WiremapGlowingSphere(
            map, 500, 300, 10, color(255, 255, 0), 10, 
            color(255, 0, 0)); 
}

void draw() {
    background(0);
   
    glowingSphere.setPosition(mouseX, 300, (int)map(mouseY, 0, 768, 0, 20));
    glowingSphere.display();
}

