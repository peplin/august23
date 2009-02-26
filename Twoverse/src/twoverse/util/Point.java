package twoverse.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Point implements Serializable {
    private static final long serialVersionUID = 6712603407109374020L;
    private static Properties sConfigFile;
    private static Logger sLogger = Logger.getLogger(Point.class.getName());
    private double mX;
    private double mY;
    private double mZ;
    private boolean mIsTwoDimensional = false;

    public class TwoDimensionalException extends Exception {
        private static final long serialVersionUID = -2462077367673447134L;

        public TwoDimensionalException(String msg) {
            super(msg);
        }
    }

    public Point(double x, double y, double z) {
        loadConfig();
        intitialize(x, y, z);
    }

    public Point(double x, double y) {
        loadConfig();
        mIsTwoDimensional = true;
        intitialize(x, y, 0);
    }

    public Point(Element element) {
        loadConfig();
        if(!element.getLocalName().equals(sConfigFile.getProperty("POINT_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a point");
        }
        double x =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("X_ATTRIBUTE_TAG"))
                        .getValue());
        double y =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("Y_ATTRIBUTE_TAG"))
                        .getValue());
        double z = 0;
        Attribute zAttr =
                element.getAttribute(sConfigFile.getProperty("Z_ATTRIBUTE_TAG"));
        if(zAttr != null) {
            z = Double.valueOf(zAttr.getValue());
        } else {
            mIsTwoDimensional = true;
        }
        intitialize(x, y, z);
    }

    private void loadConfig() {
        try {
            if(sConfigFile == null) {
                sConfigFile = new Properties();
                sConfigFile.load(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("twoverse/conf/Point.properties"));
            }
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                    + e.getMessage(), e);
        }
    }

    private void intitialize(double x, double y, double z) {
        setX(x);
        setY(y);
        try {
            setZ(z);
        } catch (TwoDimensionalException e) {
            sLogger.log(Level.SEVERE,
                    "Is point 2D or 3D? Called wrong initialize function if it is 2D"
                            + e.getMessage(),
                    e);
        }
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

    public void setZ(double z) throws TwoDimensionalException {
        if(mIsTwoDimensional) {
            throw new TwoDimensionalException("Attempted to set Z value of 2D point");
        }
        mZ = z;
    }

    public double getZ() throws TwoDimensionalException {
        if(mIsTwoDimensional) {
            throw new TwoDimensionalException("Attempted to get Z value of 2D point");
        }
        return mZ;
    }

    public String toString() {
        String s = "[x: " + getX() + ", " + "y: " + getY();
        if(!mIsTwoDimensional) {
            try {
                s += ", " + "z: " + getZ();
            } catch (TwoDimensionalException e) {
                sLogger.log(Level.SEVERE,
                        "Is point 2D or 3D?" + e.getMessage(),
                        e);
            }
        }
        s += "]";
        return s;
    }

    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("POINT_TAG"));
        root.addAttribute(new Attribute(sConfigFile.getProperty("X_ATTRIBUTE_TAG"),
                String.valueOf(mX)));
        root.addAttribute(new Attribute(sConfigFile.getProperty("Y_ATTRIBUTE_TAG"),
                String.valueOf(mY)));
        if(!mIsTwoDimensional) {
            root.addAttribute(new Attribute(sConfigFile.getProperty("Z_ATTRIBUTE_TAG"),
                    String.valueOf(mZ)));
        }
        return root;
    }
}
