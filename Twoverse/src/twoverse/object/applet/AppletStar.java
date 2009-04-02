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
        mParent.pushMatrix();
        mParent.noStroke();
        mParent.translate((float) getPosition().getX(),
                (float) getPosition().getY(),
                (float) getPosition().getZ());
        //mParent.sphere((float) getRadius());
        mParent.ellipse(0, 0, (float) getRadius(), (float) getRadius());
        mParent.popMatrix();
    }

}
