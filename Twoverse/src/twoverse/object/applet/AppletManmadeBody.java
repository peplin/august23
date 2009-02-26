package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.ManmadeBody;

public class AppletManmadeBody extends AbstractAppletCelestialBody {
    private ManmadeBody mManmadeBody;

    public AppletManmadeBody(PApplet parent, ManmadeBody body) {
        mParent = parent;
        mManmadeBody = body;
    }

    @Override
    public void display() {
        mParent.noStroke();
        mParent.translate((float) mManmadeBody.getPosition().getX(),
                          (float) mManmadeBody.getPosition().getY(),
                          (float) mManmadeBody.getPosition().getZ());
        mParent.sphere(10); // TODO
    }
}
