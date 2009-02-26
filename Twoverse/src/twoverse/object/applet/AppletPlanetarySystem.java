package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.PlanetarySystem;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletPlanetarySystem extends PlanetarySystem implements AppletBodyInterface {
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
