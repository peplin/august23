import wiremap.Wiremap;
import wiremap.WiremapSphere;

Wiremap map;
WiremapSphere sphere;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 24, 32, .25, .25, 7,
            "depth128.txt");

    sphere = new WiremapSphere(
            map, 200, 300, 10, color(267, 120, 45), 5);
}

void draw() {
    background(0);

    sphere.setPosition(mouseX, 300, (int)map(mouseX, 0, 1024, 0, 20));
    sphere.display();
}

