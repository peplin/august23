/**
 * Twoverse Star Object
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

package twoverse.object;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import nu.xom.Attribute;
import nu.xom.Element;
import processing.core.PApplet;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.object.applet.AppletStar;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

/**
 * Implementation of CelestialBody for a Star. <br><br>
 * 
 * Beyond position, velocity, acceleration, etc, a Star has a radius, mass,
 * color, luminosity, frequency and a state.<br><br>
 * 
 * This class was used extensively in the August 23, 1966 installation.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class Star extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private double mRadius;
    private double mMass;
    private int mColorR;
    private int mColorG;
    private int mColorB;
    private double mLuminosity;
    private double mFrequency;
    private int mState = 5;
    private static PreparedStatement sSelectAllStarsStatement;
    private static PreparedStatement sInsertStarStatement;
    private static PreparedStatement sUpdateStarStatement;
    private static Connection sConnection;

    private void initialize(double radius, double mass, int colorR, int colorB,
            int colorG, double luminosity, double frequency) {
        setRadius(radius);
        setMass(mass);
        setColorR(colorR);
        setColorG(colorG);
        setColorB(colorB);
        setLuminosity(luminosity);
        setFrequency(frequency);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("Star");
        }
    }

    /**
     * Constructs an instance of Star.
     * 
     * @param mass
     *            mass of the star
     * @param radius
     *            radius of the star
     * @param colorR
     *            red value of the star's color
     * @param colorB
     *            blue value of the star's color
     * @param colorG
     *            green value of the star's color
     * @param luminosity
     *            luminosity of the star
     * @param frequency
     *            frequency of oscillation of the star
     */
    public Star(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            double mass, double radius, int colorR, int colorB, int colorG,
            double luminosity, double frequency) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    /**
     * Constructs a more detailed instance of Star.
     * 
     * @param mass
     *            mass of the star
     * @param radius
     *            radius of the star
     * @param colorR
     *            red value of the star's color
     * @param colorB
     *            blue value of the star's color
     * @param colorG
     *            green value of the star's color
     * @param luminosity
     *            luminosity of the star
     * @param frequency
     *            frequency of oscillation of the star
     */
    public Star(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children, double mass, double radius, int colorR,
            int colorB, int colorG, double luminosity, double frequency) {
        super(id,
                ownerId,
                name,
                birthTime,
                deathTime,
                parentId,
                position,
                velocity,
                acceleration,
                children);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    /**
     * Construct an instance of Star based on an existing CelestialBody. Useful
     * for constructing from a database result set.
     * 
     * @param body
     *            base body to construct this star from
     * @param mass
     *            mass of the star
     * @param radius
     *            radius of the star
     * @param colorR
     *            red value of the star's color
     * @param colorB
     *            blue value of the star's color
     * @param colorG
     *            green value of the star's color
     * @param luminosity
     *            luminosity of the star
     * @param frequency
     *            frequency of oscillation of the star
     */
    public Star(CelestialBody body, double mass, double radius, int colorR,
            int colorB, int colorG, double luminosity, double frequency) {
        super(body);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    /**
     * Construct a Star from an XML element
     * 
     * @param element
     *            element from which to parse a Star
     * @throws UnexpectedXmlElementException
     *             if the element does not contain a Star
     */
    public Star(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName().equals(sConfigFile.getProperty("STAR_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a star");
        }

        double radius =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        int colorR =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_R_ATTRIBUTE_TAG"))
                        .getValue());
        int colorG =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_G_ATTRIBUTE_TAG"))
                        .getValue());
        int colorB =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_B_ATTRIBUTE_TAG"))
                        .getValue());

        double luminosity =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("LUMINOSITY_ATTRIBUTE_TAG"))
                        .getValue());

        double frequency =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("FREQUENCY_ATTRIBUTE_TAG"))
                        .getValue());

        setState(Integer.valueOf(element.getAttribute(sConfigFile.getProperty("STATE_ATTRIBUTE_TAG"))
                .getValue()));

        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    /**
     * Copy constructor for a Star.
     * 
     * @param star
     *            star to copy
     */
    public Star(Star star) {
        super(star);
        mState = star.getState();
        initialize(star.getRadius(),
                star.getMass(),
                star.getColorR(),
                star.getColorG(),
                star.getColorB(),
                star.getLuminosity(),
                star.getFrequency());
    }

    @Override
    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletStar(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllStarsStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN star " + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)" + " LEFT JOIN (state) "
                        + "ON (star.state = state.id)");
        sInsertStarStatement =
                sConnection.prepareStatement("INSERT INTO star (id, mass, radius, colorR, colorB, colorG, luminosity, frequency, state) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        sUpdateStarStatement =
                sConnection.prepareStatement("UPDATE star "
                        + "SET mass = ?,"
                        + " radius = ?, colorR = ?, colorG = ?, colorB = ?, luminosity = ?, frequency = ?, state = ? "
                        + "WHERE id = ?");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> stars =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet = sSelectAllStarsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for (CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between stars and celestial bodies");
                }
                Star star =
                        new Star(body,
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("radius"),
                                resultSet.getInt("colorR"),
                                resultSet.getInt("colorG"),
                                resultSet.getInt("colorB"),
                                resultSet.getDouble("luminosity"),
                                resultSet.getDouble("frequency"));
                star.setState(resultSet.getInt("state"));
                star.setDirty(false);
                stars.put(star.getId(), star);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get stars", e);
        }
        return stars;
    }

    @Override
    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add star: " + this);
        try {
            super.insertInDatabase();

            sInsertStarStatement.setInt(1, getId());
            sInsertStarStatement.setDouble(2, getMass());
            sInsertStarStatement.setDouble(3, getRadius());
            sInsertStarStatement.setInt(4, getColorR());
            sInsertStarStatement.setInt(5, getColorG());
            sInsertStarStatement.setInt(6, getColorB());
            sInsertStarStatement.setDouble(7, getLuminosity());
            sInsertStarStatement.setDouble(8, getFrequency());
            sInsertStarStatement.setInt(9, getState());

            sInsertStarStatement.executeUpdate();
            setDirty(false);
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + this, e);
        }
    }

    @Override
    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update star: " + this);
        try {
            super.updateInDatabase();

            sUpdateStarStatement.setDouble(1, getMass());
            sUpdateStarStatement.setDouble(2, getRadius());
            sUpdateStarStatement.setInt(3, getColorR());
            sUpdateStarStatement.setInt(4, getColorG());
            sUpdateStarStatement.setInt(5, getColorB());
            sUpdateStarStatement.setDouble(6, getLuminosity());
            sUpdateStarStatement.setDouble(7, getFrequency());
            sUpdateStarStatement.setInt(8, getState());
            sUpdateStarStatement.setDouble(9, getId());
            sUpdateStarStatement.executeUpdate();
            setDirty(false);
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not update star " + this, e);
        }
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    public void setRadius(double mRadius) {
        this.mRadius = mRadius;
    }

    public double getRadius() {
        return mRadius;
    }

    public void setColorR(int colorR) {
        mColorR = colorR;
    }

    public void setColorG(int colorG) {
        mColorG = colorG;
    }

    public void setColorB(int colorB) {
        mColorB = colorB;
    }

    public int getColorR() {
        return mColorR;
    }

    public int getColorG() {
        return mColorG;
    }

    public int getColorB() {
        return mColorB;
    }

    public void setLuminosity(double luminosity) {
        mLuminosity = luminosity;
    }

    public double getLuminosity() {
        return mLuminosity;
    }

    public void setFrequency(double frequency) {
        mFrequency = frequency;
    }

    public double getFrequency() {
        return mFrequency;
    }

    public void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("STAR_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"),
                String.valueOf(getRadius())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(getMass())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_R_ATTRIBUTE_TAG"),
                String.valueOf(getColorR())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_G_ATTRIBUTE_TAG"),
                String.valueOf(getColorG())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_B_ATTRIBUTE_TAG"),
                String.valueOf(getColorB())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("LUMINOSITY_ATTRIBUTE_TAG"),
                String.valueOf(getLuminosity())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("FREQUENCY_ATTRIBUTE_TAG"),
                String.valueOf(getFrequency())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("STATE_ATTRIBUTE_TAG"),
                String.valueOf(getState())));
        return root;
    }
}
