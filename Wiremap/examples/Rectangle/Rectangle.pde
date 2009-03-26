import wiremap.Wiremap;
import wiremap.WiremapRectangle;

Wiremap map;
WiremapRectangle rectangle;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 24, 32, .25, .25, 7,
            "depth128.txt");

    rectangle = new WiremapRectangle(map, mouseX, mouseY, 10, color(265, 120, 45),
            10, 10, 10, 5, color(255, 255, 255));
}

void draw() {
    background(0);

    rectangle.setPosition(mouseX, mouseY, 10);
    rectangle.display();

}

