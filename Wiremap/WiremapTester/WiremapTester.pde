Wiremap map;

void setup() {
    size(1024, 768);

    map = new Wiremap(this, 128, 70, 20, 32, .25, .25, 20, 4, "depths.txt");
    fill(255);
    stroke(255);

}

void draw() {
    background(0);

    map.sphere(500, 15, 0, 10);

}
