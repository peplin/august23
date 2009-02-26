package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.Galaxy;

public class AppletGalaxy extends Galaxy implements AppletObjectInterface {
    private PApplet mParent;

    public AppletGalaxy(PApplet parent, Galaxy galaxy) {
        super(galaxy);
        mParent = parent;
    }

    public void display() {
        mParent.noStroke();
        mParent.translate((float) getPosition().getX(),
                          (float) getPosition().getY(),
                          (float) getPosition().getZ());
        mParent.sphere((float) getMass());
    }
}
