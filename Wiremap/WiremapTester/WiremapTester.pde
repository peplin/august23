Wiremap map;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 256, 70, 20, 24, 32, .25, .25, 20, 4, "depths.txt");
    fill(255);
    stroke(255);

}

void draw() {
    background(0);

    WiremapSphere sphere = new WiremapSphere(
                    map, 800, 300, 10, 10, color(267, 120, 45));
    sphere.display();
}
