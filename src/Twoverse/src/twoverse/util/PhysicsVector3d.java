/**
 * Twoverse 3D Physical Vector
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

public class PhysicsVector3d implements Serializable {
    private static final long serialVersionUID = -7483027109043816672L;
    private static Properties sConfigFile;
    private static Logger sLogger =
            Logger.getLogger(PhysicsVector3d.class.getName());
    private Point mUnitVectorPoint;
    private double mMagnitude;

    /**
     * @param x
     * @param y
     * @param z
     * @param magnitude
     */
    public PhysicsVector3d(double x, double y, double z, double magnitude) {
        loadConfig();
        initialize(new Point(x, y, z), magnitude);
    }

    /**
     * @param direction
     * @param magnitude
     */
    public PhysicsVector3d(Point direction, double magnitude) {
        loadConfig();
        initialize(direction, magnitude);
    }

    /**
     * @param element
     */
    public PhysicsVector3d(Element element) {
        loadConfig();
        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("VECTOR_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a vector");
        }

        Element directionElement =
                element.getFirstChildElement(sConfigFile.getProperty("POINT_TAG"));
        Point direction = new Point(directionElement);
        if(!directionElement.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                .getValue()
                .equals(sConfigFile.getProperty("DIRECTION_ATTRIBUTE_VALUE"))
                || direction == null) {
            throw new UnexpectedXmlElementException("Unexpected point (or invalid point) for name: "
                    + directionElement.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG")));
        }
        double magnitude =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MAGNITUDE_ATTRIBUTE_TAG"))
                        .getValue());
        initialize(direction, magnitude);
    }

    private void initialize(Point direction, double magnitude) {
        setDirection(direction);
        setMagnitude(magnitude);
    }

    private void loadConfig() {
        try {
            if(sConfigFile == null) {
                sConfigFile = new Properties();
                sConfigFile.load(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("twoverse/conf/PhysicsVector3d.properties"));
            }
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                    + e.getMessage(), e);
        }
    }

    /**
     * @param newDirection
     */
    public void setDirection(Point newDirection) {
        mUnitVectorPoint = newDirection;
    }

    /**
     * @param magnitude
     */
    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }

    /**
     * @return
     */
    public Point getUnitDirection() {
        return mUnitVectorPoint;
    }

    /**
     * @return
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    @Override
    public String toString() {
        return "[direction: " + getUnitDirection() + ", " + "magnitude: "
                + getMagnitude() + "]";
    }

    /**
     * @return
     */
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("VECTOR_TAG"));
        Element point = mUnitVectorPoint.toXmlElement();
        point.addAttribute(new Attribute("name",
                sConfigFile.getProperty("DIRECTION_ATTRIBUTE_VALUE")));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MAGNITUDE_ATTRIBUTE_TAG"),
                String.valueOf(mMagnitude)));
        root.appendChild(point);
        return root;
    }
}
