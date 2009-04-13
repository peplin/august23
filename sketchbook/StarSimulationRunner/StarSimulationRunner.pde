StarSimulation starSim;
PulsarSimulation pulsarSim;
StarSimulationWire starSimWire;

void setup() {
    size(1024, 768, P3D);
    //starSim = new StarSimulation(this, null);
    pulsarSim = new PulsarSimulation(this);
    starSimWire = new StarSimulationWire(this);
}

void draw() {
    //starSim.display();
    //pulsarSim.display();
    starSimWire.display();
}
