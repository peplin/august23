package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.Planet;
import twoverse.util.Point.TwoDimensionalException;

public class AppletPlanet extends AbstractAppletCelestialBody {
    private Planet mPlanet;

    public AppletPlanet(PApplet parent, Planet planet) {
        mParent = parent;
        mPlanet = planet;
    }

    @Override
    public void display() throws TwoDimensionalException {
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) mPlanet.getPosition().getX(),
                (float) mPlanet.getPosition().getY(),
                (float) mPlanet.getPosition().getZ());
        mParent.sphere((float) mPlanet.getRadius());
        mParent.popMatrix();
    }

}
