/**
 * Twoverse 2D/3D Point
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

/**
 * 2D or 3D point in cartesian coordinates.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class Point implements Serializable {
    private static final long serialVersionUID = 6712603407109374020L;
    private static Properties sConfigFile;
    private static Logger sLogger = Logger.getLogger(Point.class.getName());
    private double mX;
    private double mY;
    private double mZ;
    private boolean mIsTwoDimensional = false;

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

    public class TwoDimensionalException extends Exception {
        private static final long serialVersionUID = -2462077367673447134L;

        public TwoDimensionalException(String msg) {
            super(msg);
        }
    }

    /**
     * Construct a new 3-dimensional point
     * 
     * @param x
     * @param y
     * @param z
     */
    public Point(double x, double y, double z) {
        loadConfig();
        intitialize(x, y, z);
    }

    /**
     * Construct a new 2-dimensional point
     * 
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        loadConfig();
        mIsTwoDimensional = true;
        intitialize(x, y, 0);
    }

    /**
     * Construct a new point from an XML element.
     * 
     * @param element
     *            an XML element that contains a 2D or 3D point
     */
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

    /**
     * Set the x value for this point.
     * 
     * @param x
     */
    public void setX(double x) {
        mX = x;
    }

    /**
     * Get the x value for this point.
     * 
     * @return x
     */
    public double getX() {
        return mX;
    }

    /**
     * Set the y value for this point.
     * 
     * @param y
     */
    public void setY(double y) {
        mY = y;
    }

    /**
     * Get the y value for this point.
     * 
     * @return y
     */
    public double getY() {
        return mY;
    }

    /**
     * Set the z value for this point. Only works for 3-dimensional points.
     * 
     * @param z
     * @throws TwoDimensionalException
     *             if point was constructed as 2D
     */
    public void setZ(double z) throws TwoDimensionalException {
        if(mIsTwoDimensional) {
            throw new TwoDimensionalException("Attempted to set Z value of 2D point");
        }
        mZ = z;
    }

    /**
     * Get the z value for this point. Only works for 3-dimensional points.
     * 
     * @return z value
     * @throws TwoDimensionalException
     *             if point was constructed as 2D
     */
    public double getZ() throws TwoDimensionalException {
        if(mIsTwoDimensional) {
            throw new TwoDimensionalException("Attempted to get Z value of 2D point");
        }
        return mZ;
    }

    @Override
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

    /**
     * Convert this point to an XML element.
     * 
     * @return this point as an XML element
     */
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
