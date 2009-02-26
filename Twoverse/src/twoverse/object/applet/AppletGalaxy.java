package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.Galaxy;
import twoverse.util.Point.TwoDimensionalException;

public class AppletGalaxy extends AbstractAppletCelestialBody {
    private Galaxy mGalaxy;

    public AppletGalaxy(PApplet parent, Galaxy galaxy) {
        mParent = parent;
        mGalaxy = galaxy;
    }

    @Override
    public void display() throws TwoDimensionalException {
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) mGalaxy.getPosition().getX(),
                          (float) mGalaxy.getPosition().getY(),
                          (float) mGalaxy.getPosition().getZ());
        mParent.sphere((float) mGalaxy.getMass());
        mParent.popMatrix();
    }
}
