package twoverse.util;

import nu.xom.Attribute;
import nu.xom.Element;

public class Point {
    public Point(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setX(double x) {
        mX = x;
    }
    public double getX() {
        return mX;
    }

    public void setY(double y) {
        mY = y;
    }

    public double getY() {
        return mY;
    }

    public void setZ(double z) {
        mZ = z;
    }

    public double getZ() {
        return mZ;
    }

    public Element toXmlElement() {
        Element root = new Element("point");
        root.addAttribute(new Attribute("x", String.valueOf(mX)));
        root.addAttribute(new Attribute("y", String.valueOf(mY)));
        root.addAttribute(new Attribute("z", String.valueOf(mZ)));
        return root;
    }

    private double mX;
    private double mY;
    private double mZ;
}
