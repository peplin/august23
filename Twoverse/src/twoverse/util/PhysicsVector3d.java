package twoverse.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class PhysicsVector3d implements Serializable {
    private static final long serialVersionUID = -7483027109043816672L;
    private Properties mConfigFile;
    private static Logger sLogger = Logger.getLogger(PhysicsVector3d.class
            .getName());
    private Point mUnitVectorPoint;
    private double mMagnitude;

    public PhysicsVector3d(double x, double y, double z, double magnitude) {
        loadConfig();
        initialize(new Point(x, y, z), magnitude);
    }

    public PhysicsVector3d(Point direction, double magnitude) {
        loadConfig();
        initialize(direction, magnitude);
    }

    public PhysicsVector3d(Element element) {
        loadConfig();
        if (!element.getLocalName().equals(
                mConfigFile.getProperty("VECTOR_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a vector");
        }

        Element directionElement = element.getFirstChildElement(mConfigFile
                .getProperty("POINT_TAG"));
        Point direction = new Point(directionElement);
        if (!directionElement.getAttribute(
                mConfigFile.getProperty("NAME_ATTRIBUTE_TAG")).getValue().equals(
                mConfigFile.getProperty("DIRECTION_ATTRIBUTE_VALUE"))
                || direction == null) {
            throw new UnexpectedXmlElementException(
                    "Unexpected point (or invalid point) for name: "
                            + directionElement.getAttribute(mConfigFile
                                    .getProperty("NAME_ATTRIBUTE_TAG")));
        }
        double magnitude = Double.valueOf(element.getAttribute(
                mConfigFile.getProperty("MAGNITUDE_ATTRIBUTE_TAG")).getValue());
        initialize(direction, magnitude);
    }

    private void initialize(Point direction, double magnitude) {
        setDirection(direction);
        setMagnitude(magnitude);
    }

    private void loadConfig() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                            "twoverse/conf/PhysicsVector3d.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                    + e.getMessage(), e);
        }
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
        Element root = new Element(mConfigFile.getProperty("VECTOR_TAG"));
        Element point = mUnitVectorPoint.toXmlElement();
        point.addAttribute(new Attribute("name", mConfigFile
                .getProperty("DIRECTION_ATTRIBUTE_VALUE")));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("MAGNITUDE_ATTRIBUTE_TAG"), String
                .valueOf(mMagnitude)));
        root.appendChild(point);
        return root;
    }
}
