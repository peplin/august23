StarSimulation starSim;
PulsarSimulation pulsarSim;
StarSimulationWire starSimWire;

void setup() {
    size(1024, 768, P3D);
    //starSim = new StarSimulation(this, null);
    pulsarSim = new PulsarSimulation(this);
    starSimWire = new StarSimulationWire(this, new Wiremap(this, 256, 90, 36, 36, 48, .1875, .1875, 2,
            "../wiremap/depth256.txt"));
}

void draw() {
    //starSim.display();
    //pulsarSim.display();
    starSimWire.display();
}
