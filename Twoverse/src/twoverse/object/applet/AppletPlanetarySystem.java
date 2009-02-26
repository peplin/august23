package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.PlanetarySystem;
import twoverse.util.Point.TwoDimensionalException;

public class AppletPlanetarySystem extends AbstractAppletCelestialBody {
    private PlanetarySystem mPlanetarySystem;

    public AppletPlanetarySystem(PApplet parent, PlanetarySystem system) {
        mParent = parent;
        mPlanetarySystem = system;
    }

    @Override
    public void display() throws TwoDimensionalException {
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) mPlanetarySystem.getPosition().getX(),
                          (float) mPlanetarySystem.getPosition().getY(),
                          (float) mPlanetarySystem.getPosition().getZ());
        mParent.sphere((float) mPlanetarySystem.getMass());
        mParent.popMatrix();
    }
}
