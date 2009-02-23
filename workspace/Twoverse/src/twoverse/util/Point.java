package twoverse.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.RequestHandlerClient;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Point implements Serializable {
    private static final long serialVersionUID = 6712603407109374020L;
    private Properties mConfigFile;
    private static Logger sLogger = Logger.getLogger(RequestHandlerClient.class
            .getName());
    private double mX;
    private double mY;
    private double mZ;

    public Point(double x, double y, double z) {
        loadConfig();
        intitialize(x, y, z);
    }

    public Point(double x, double y) {
        // TODO this is dangerous, must be careful when
        // using in 2D
        loadConfig();
        intitialize(x, y, 0);
    }

    public Point(Element element) {
        loadConfig();
        if (!element.getLocalName()
                .equals(mConfigFile.getProperty("POINT_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a point");
        }
        double x = Double.valueOf(element.getAttribute(
                mConfigFile.getProperty("X_ATTRIBUTE_TAG")).getValue());
        double y = Double.valueOf(element.getAttribute(
                mConfigFile.getProperty("Y_ATTRIBUTE_TAG")).getValue());
        double z = Double.valueOf(element.getAttribute(
                mConfigFile.getProperty("Z_ATTRIBUTE_TAG")).getValue());
        intitialize(x, y, z);
    }

    private void loadConfig() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("twoverse/conf/Point.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: " + e.getMessage(), e);
        }
    }

    private void intitialize(double x, double y, double z) {
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
        Element root = new Element(mConfigFile.getProperty("POINT_TAG"));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("X_ATTRIBUTE_TAG"), String.valueOf(mX)));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("Y_ATTRIBUTE_TAG"), String.valueOf(mY)));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("Z_ATTRIBUTE_TAG"), String.valueOf(mZ)));
        return root;
    }
}
