StarSimulation starSim;
PulsarSimulation pulsarSim;

void setup() {
    size(1024, 768, P3D);
    //starSim = new StarSimulation(this, null);
    pulsarSim = new PulsarSimulation(this);
}

void draw() {
    //starSim.display();
    pulsarSim.display();
}
