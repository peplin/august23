/**
 * Twoverse Galaxy Object
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
import twoverse.object.applet.AppletGalaxy;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Galaxy extends CelestialBody implements Serializable {
    private GalaxyShape mShape;
    private double mMass;
    private double mDensity;
    private static final long serialVersionUID = 4163663398347532933L;
    private static Properties sConfigFile;

    private static PreparedStatement sSelectAllGalaxiesStatement;
    private static PreparedStatement sInsertGalaxyStatement;
    private static PreparedStatement sUpdateGalaxyStatement;
    private static Connection sConnection;

    /**
     * A new client side galaxy, ID and birth are set and returned by the server
     * 
     * @param ownerId
     * @param name
     * @param parentId
     * @param position
     * @param velocity
     * @param acceleration
     * @param shape
     * @param mass
     * @param density
     */
    public Galaxy(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            GalaxyShape shape, double mass, double density) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(shape, mass, density);
    }

    public Galaxy(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children, GalaxyShape shape, double mass,
            double density) {
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
        initialize(shape, mass, density);
    }

    public Galaxy(CelestialBody body, GalaxyShape shape, double mass,
            double density) {
        super(body);
        loadConfig();
        initialize(shape, mass, density);
    }

    public Galaxy(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("GALAXY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a galaxy");
        }

        Element shapeElement =
                element.getFirstChildElement(sConfigFile.getProperty("GALAXY_SHAPE_TAG"));
        GalaxyShape shape = new GalaxyShape(shapeElement);

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        double density =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("DENSITY_ATTRIBUTE_TAG"))
                        .getValue());

        initialize(shape, mass, density);
    }

    public Galaxy(Galaxy galaxy) {
        super(galaxy);
        initialize(galaxy.getShape(), galaxy.getMass(), galaxy.getDensity());
    }

    private void initialize(GalaxyShape shape, double mass, double density) {
        setShape(shape);
        setMass(mass);
        setDensity(density);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("Galaxy");
        }
    }

    @Override
    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletGalaxy(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllGalaxiesStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN galaxy " + "LEFT JOIN (galaxy_shape) "
                        + "ON (galaxy.shape = galaxy_shape.id) "
                        + "LEFT JOIN (user) " + "ON (object.owner = user.id)");
        sInsertGalaxyStatement =
                sConnection.prepareStatement("INSERT INTO galaxy (id, shape, mass, density) "
                        + "VALUES (?, ?, ?, ?)");
        sUpdateGalaxyStatement =
                sConnection.prepareStatement("UPDATE galaxy "
                        + "SET shape = ?," + "mass = ?, " + "density = ? "
                        + "WHERE id = ?");
    }

    public void setShape(GalaxyShape shape) {
        mShape = shape;
    }

    public GalaxyShape getShape() {
        return mShape;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    public void setDensity(double density) {
        mDensity = density;
    }

    public double getDensity() {
        return mDensity;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("GALAXY_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(mMass)));
        root.addAttribute(new Attribute(sConfigFile.getProperty("DENSITY_ATTRIBUTE_TAG"),
                String.valueOf(mDensity)));
        root.appendChild(mShape.toXmlElement());
        return root;
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> galaxies =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet = sSelectAllGalaxiesStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between galaxies and celestial bodies");
                }

                Galaxy galaxy =
                        new Galaxy(body,
                                new GalaxyShape(resultSet.getInt("galaxy_shape.id"),
                                        resultSet.getString("galaxy_shape.name"),
                                        resultSet.getString("galaxy_shape.texture")),
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("density"));
                galaxy.setDirty(false);
                galaxies.put(galaxy.getId(), galaxy);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get galaxies", e);
        }
        return galaxies;
    }

    @Override
    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add galaxy: " + this);
        try {
            super.insertInDatabase();

            sInsertGalaxyStatement.setInt(1, getId());
            sInsertGalaxyStatement.setInt(2, getShape().getId());
            sInsertGalaxyStatement.setDouble(3, getMass());
            sInsertGalaxyStatement.setDouble(4, getDensity());
            sInsertGalaxyStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add galaxy " + this, e);
        }
    }

    @Override
    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update galaxy: " + this);
        try {
            super.updateInDatabase();

            sUpdateGalaxyStatement.setInt(1, getShape().getId());
            sUpdateGalaxyStatement.setDouble(2, getMass());
            sUpdateGalaxyStatement.setDouble(3, getDensity());
            sUpdateGalaxyStatement.setInt(4, getId());
            sUpdateGalaxyStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not update galaxy " + this, e);
        }
    }
}
