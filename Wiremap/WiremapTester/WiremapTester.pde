Wiremap map;
WiremapSphere sphere, outlinedSphere, glowingSphere;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 24, 32, .25, .25, 32, 3, "depth128.txt");
    fill(255);
    stroke(255);

    sphere = new WiremapSphere(
            map, 200, 300, 10, 5, color(267, 120, 45));
    outlinedSphere = new WiremapOutlinedSphere(
            map, 500, 200, 5, 5, color(180, 90, 45), 15,
            color(255, 255, 255));
    glowingSphere = new WiremapGlowingSphere(
            map, 500, 300, 10, 10, color(250, 150, 100), 15,
            color(255, 255, 255)); 
}

void draw() {
    background(0);

    //sphere.display();
    //outlinedSphere.display();
    glowingSphere.display();
}
