package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.ManmadeBody;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletManmadeBody extends ManmadeBody implements AppletBodyInterface {
    private PApplet mParent;

    public AppletManmadeBody(PApplet parent, ManmadeBody body) {
        super(body);
        mParent = parent;
    }

    public void display() throws TwoDimensionalException {
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) getPosition().getX(),
                          (float) getPosition().getY(),
                          (float) getPosition().getZ());
        mParent.sphere(10); 
        mParent.popMatrix();
    }
}
