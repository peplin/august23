/**
 ** Pulsar Simulation for Twoverse Client
 **
 ** by Jiangang Hao (jghao@umich.ed)
 ** and Christopher Peplin (chris.peplin@rhubarbtech.com)
 ** for August 23, 1966 (GROCS Project Group)
 ** University of Michigan, 2009
 **
 ** http://august231966.com
 ** http://www.dc.umich.edu/grocs
 **
 ** Copyright 2009 Jiangang Hao, Christopher Peplin 
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

import processing.opengl.*;

/**
 * The Pulsar Simulation is a 3D Processing visualization of a pulsar spinning. <br>
 * <br>
 * 
 * The sketch was created for the August 23, 1966 exhibition, where visitors
 * could create a star with their own heartbeat and use the color of their
 * clothes to determine some property of its creation. The pulsar was one
 * possible end state.
 * 
 * @author Jiangang Hao (jghao@umich.ed)- timing, graphics, data set
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com) - cleanup, P3D
 *         tweaking
 * @version 1.0, Copyright 2009 under Apache License
 */
public class PulsarSimulation {
    private float jet[];
    private float core[];
    private float jetRotation;

    public PulsarSimulation(PApplet parent) {
        core = loadPoints("core.csv");
        jet = loadPoints("jet.csv");
    }

    public void display() {
        background(0);
        rotateZ(-PI / 4);
        rotateY(jetRotation);
        stroke(5, 255, 255, 255);
        scale(0.3);
        for (int i = 0; i < core.length; i += 3) {
            point(core[i], core[i + 1], core[i + 2]);
        }

        stroke(5, 5, 250, 255);
        scale(20, 10, 10);
        for (int i = 0; i < jet.length; i += 3) {
            point(jet[i], jet[i + 1], jet[i + 2]);
        }
        jetRotation = (jetRotation + .05) % (2 * PI);
    }

    private float[] loadPoints(String path) {
        String[] raw = loadStrings(path);
        float[] points = new float[raw.length * 3];
        for (int i = 0; i < raw.length; i++) {
            String[] thisLine = split(raw[i], ",");

            points[i * 3] = new Float(thisLine[0]).floatValue();
            points[i * 3 + 1] = new Float(thisLine[1]).floatValue();
            points[i * 3 + 2] = new Float(thisLine[2]).floatValue();
        }
        return points;
    }
}
