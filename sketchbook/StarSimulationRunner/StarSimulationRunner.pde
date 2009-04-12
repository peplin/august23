StarSimulation sim;

void setup() {
    size(1024, 768, P3D);
    sim = new StarSimulation(null);
}

void draw() {
    sim.display();
}
