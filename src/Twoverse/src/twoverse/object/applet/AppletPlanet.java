package twoverse.object.applet;

import processing.core.PApplet;
import twoverse.object.Planet;
import twoverse.util.Point.TwoDimensionalException;

@SuppressWarnings("serial")
public class AppletPlanet extends Planet implements AppletBodyInterface {
	private PApplet mParent;
	
	public AppletPlanet(PApplet parent, Planet planet) {
        super(planet);
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
