package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.ManmadeBody;

public class AppletManmadeBody extends ManmadeBody implements AppletObjectInterface {
    private PApplet mParent;

    public AppletManmadeBody(PApplet parent, ManmadeBody body) {
        super(body);
        mParent = parent;
    }

    public void display() {
        mParent.noStroke();
        mParent.translate((float) getPosition().getX(),
                          (float) getPosition().getY(),
                          (float) getPosition().getZ());
        mParent.sphere(10); 
    }
}
