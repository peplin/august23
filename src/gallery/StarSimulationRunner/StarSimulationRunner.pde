/**
** Star Simulation Runner (tester for simulations)
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
**
** Copyright 2009 Christopher Peplin 
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at 
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and
** limitations under the License. 
*/

StarSimulation starSim;
PulsarSimulation pulsarSim;
StarSimulationWire starSimWire;

void setup() {
    size(1024, 768, P3D);
    starSim = new StarSimulation(this, null);
    pulsarSim = new PulsarSimulation(this);
    /*starSimWire = new StarSimulationWire(this, new Wiremap(this, 256, 90, 36, 36, 48, .1875, .1875, 2,
            "../wiremap/depth256.txt"));*/
}

void draw() {
    starSim.display();
    //pulsarSim.display();
    //starSimWire.display();
}
