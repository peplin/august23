package twoverse.util;

import nu.xom.Attribute;
import nu.xom.Element;

// TODO What does this class need to have for our simulation? 
public class PhysicsVector3d {
    public PhysicsVector3d(double x, double y, double z, double magnitude) {
        setDirection(new Point(x, y, z));
        setMagnitude(magnitude);
    }

    public PhysicsVector3d(Point unitDirection, double magnitude) {
        setDirection(unitDirection);
        setMagnitude(magnitude);
    }

    public void setDirection(Point newDirection) {
        mUnitVectorPoint = newDirection;
    }

    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }

    public Point getUnitDirection() {
        return mUnitVectorPoint;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public Element toXmlElement() {
        Element root = new Element("vector");
        Element point = mUnitVectorPoint.toXmlElement();
        point.addAttribute(new Attribute("name", "direction"));
        root
                .addAttribute(new Attribute("magnitude", String
                        .valueOf(mMagnitude)));
        root.appendChild(point);
        return root;
    }

    private Point mUnitVectorPoint;
    private double mMagnitude;
}
