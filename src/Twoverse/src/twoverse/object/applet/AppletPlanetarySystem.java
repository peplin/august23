/**
 * Twoverse Planetary System Object (Applet Version)
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.PlanetarySystem;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletPlanetarySystem extends PlanetarySystem implements
        AppletBodyInterface {
    private PApplet mParent;

    public AppletPlanetarySystem(PApplet parent, PlanetarySystem system) {
        super(system);
        mParent = parent;
    }

    public void display() throws TwoDimensionalException {
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) getPosition().getX(),
                (float) getPosition().getY(),
                (float) getPosition().getZ());
        mParent.sphere((float) getMass());
        mParent.popMatrix();
    }
}
