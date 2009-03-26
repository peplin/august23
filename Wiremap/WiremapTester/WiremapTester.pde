Wiremap map;
WiremapSphere sphere, outlinedSphere, glowingSphere;
WiremapRectangle rectangle;
WiremapSliver sliver;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 24, 32, .25, .25, 3,
            "depth128.txt");

    sphere = new WiremapSphere(
            map, 200, 300, 10, color(267, 120, 45), 5);
    outlinedSphere = new WiremapOutlinedSphere(
            map, 500, 200, 5, color(180, 90, 45), 5, 15,
            color(255, 255, 255));
    glowingSphere = new WiremapGlowingSphere(
            map, 500, 300, 10, color(255, 255, 0), 10, 
            color(255, 0, 0)); 

    rectangle = new WiremapRectangle(map, 200, 300, 10, color(265, 120, 45),
            42, 34, 12, 5, color(255, 255, 255));

    sliver = new WiremapSliver(map, 400, 300, 10, color(265, 120, 45),
            150, 5, color(255, 255, 255));
}

void draw() {
    background(0);

/*    sphere.setPosition(mouseY, 300, (int)map(mouseX, 0, 1024, 0, 20));
    sphere.display();

    outlinedSphere.setPosition(width-mouseX, 300, (int)map(mouseY, 0, 768, 0, 20));
    outlinedSphere.display();
    
    glowingSphere.setPosition(mouseX, 300, (int)map(mouseY, 0, 768, 0, 20));
    glowingSphere.display();
    */
    rectangle.display();
    rectangle.yaw(1);
    //sliver.display();

}

