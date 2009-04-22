/**
 * Twoverse Star Object (Applet Version)
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
import twoverse.object.Star;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletStar extends Star implements AppletBodyInterface {
    private PApplet mParent;

    public AppletStar(PApplet parent, Star star) {
        super(star);
        mParent = parent;
    }

    public void display() throws TwoDimensionalException {
        if(getState() == 1) {
            drawPulsar();
        } else if(getState() == 2) {
            drawBlackHole();
        } else if(getState() == 3) {
            drawSupernova();
        } else if(getState() == 4) {
            drawInert();
        } else {
            drawFormation();
        }
    }

    private void drawPulsar() {
        // TODO
        drawFormation();
    }

    private void drawSupernova() {
        // TODO
        drawFormation();
    }

    private void drawInert() {
        // TODO
        drawFormation();
    }

    private void drawBlackHole() {
        // TODO
        drawFormation();
    }

    private void drawFormation() {
        mParent.pushMatrix();
        mParent.noStroke();
        try {
            mParent.translate((float) getPosition().getX(),
                    (float) getPosition().getY(),
                    (float) getPosition().getZ());
        } catch(TwoDimensionalException e) {
        }
        mParent.fill(getColorR(), getColorG(), getColorB());
        mParent.ellipse(0, 0, (float) getRadius(), (float) getRadius());

        for(int i = 2; i < 25; i++) {
            mParent.fill(getColorR(),
                    getColorG(),
                    getColorB(),
                    (float) (255.0 / i / 2.0));
            mParent.ellipse(0, 0, (float) getRadius() + i, (float) getRadius()
                    + i);
        }
        mParent.popMatrix();
    }
}
