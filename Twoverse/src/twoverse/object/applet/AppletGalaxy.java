package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.Galaxy;

public class AppletGalaxy extends AbstractAppletCelestialBody {
    private Galaxy mGalaxy;

    public AppletGalaxy(PApplet parent, Galaxy galaxy) {
        mParent = parent;
        mGalaxy = galaxy;
    }

    @Override
    public void display() {
        mParent.noStroke();
        mParent.translate((float) mGalaxy.getPosition().getX(),
                          (float) mGalaxy.getPosition().getY(),
                          (float) mGalaxy.getPosition().getZ());
        mParent.sphere((float) mGalaxy.getMass());
    }
}
