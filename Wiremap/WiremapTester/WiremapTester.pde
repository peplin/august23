Wiremap map;

void setup() {
    size(640, 480);

    map = new Wiremap(this, 256, 3000, 1280, 32, 5, 5, .8, 3, "depths.txt");
    fill(255);
    stroke(255);

}

void draw() {
    background(0);

    map.sphere(300, 300, 10, 5);

}
