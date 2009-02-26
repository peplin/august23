package twoverse.object.applet;

import processing.core.PApplet;

//TODO can I change this to an interface that the three applet types implement?
public abstract class AbstractAppletCelestialBody {
    protected PApplet mParent;

    public abstract void display();
}
