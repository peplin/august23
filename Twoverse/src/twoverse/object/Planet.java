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

import processing.core.PApplet;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.object.applet.AppletGalaxy;
import twoverse.object.applet.AppletPlanet;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Planet extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private double mRadius;
    private double mMass;
    private static PreparedStatement sSelectAllPlanetsStatement;
    private static PreparedStatement sInsertPlanetStatement;
    private static PreparedStatement sUpdatePlanetStatement;
    private static Connection sConnection;

    public Planet(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            double mass, double radius) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(radius, mass);
    }

    public Planet(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children, double mass, double radius) {
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
        initialize(radius, mass);
    }

    public Planet(CelestialBody body, double mass, double radius) {
        super(body);
        loadConfig();
        initialize(radius, mass);
    }

    public Planet(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("PLANET_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a planet");
        }

        double radius =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        initialize(radius, mass);
    }

    public Planet(Planet planet) {
        super(planet);
        initialize(planet.getRadius(), planet.getMass());
    }

    private void initialize(double radius, double mass) {
        setRadius(radius);
        setMass(mass);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("Planet");
        }
    }

    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletPlanet(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllPlanetsStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN planet " + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)");
        sInsertPlanetStatement =
                sConnection.prepareStatement("INSERT INTO planet (id, mass, radius) "
                        + "VALUES (?, ?, ?)");
        sUpdatePlanetStatement =
                sConnection.prepareStatement("UPDATE planet " + "SET mass = ?,"
                        + " radius = ? " + "WHERE id = ?");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> planets =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet = sSelectAllPlanetsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between planets and celestial bodies");
                }
                Planet planet =
                        new Planet(body,
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("radius"));
                planet.setDirty(false);
                planets.put(planet.getId(), planet);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get planets", e);
        }
        return planets;
    }

    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add planet: " + this);
        try {
            super.insertInDatabase();

            sInsertPlanetStatement.setInt(1, getId());
            sInsertPlanetStatement.setDouble(2, getMass());
            sInsertPlanetStatement.setDouble(3, getRadius());
            sInsertPlanetStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + this, e);
        }
    }

    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update planet: " + this);
        try {
            super.updateInDatabase();

            sUpdatePlanetStatement.setDouble(1, getMass());
            sUpdatePlanetStatement.setDouble(2, getRadius());
            sUpdatePlanetStatement.setDouble(3, getId());
            sUpdatePlanetStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not update planetary system "
                    + this, e);
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

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("PLANET_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"),
                String.valueOf(getRadius())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(getMass())));
        return root;
    }
}
