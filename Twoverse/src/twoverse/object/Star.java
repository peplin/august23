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
import twoverse.object.applet.AppletStar;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Star extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private double mRadius;
    private double mMass;
    private static PreparedStatement sSelectAllStarsStatement;
    private static PreparedStatement sInsertStarStatement;
    private static PreparedStatement sUpdateStarStatement;
    private static Connection sConnection;

    public Star(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            double mass, double radius) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(radius, mass);
    }

    public Star(int id, int ownerId, String name, Timestamp birthTime,
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

    public Star(CelestialBody body, double mass, double radius) {
        super(body);
        loadConfig();
        initialize(radius, mass);
    }

    public Star(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("STAR_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a star");
        }

        double radius =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        initialize(radius, mass);
    }

    public Star(Star star) {
        super(star);
        initialize(star.getRadius(), star.getMass());
    }

    private void initialize(double radius, double mass) {
        setRadius(radius);
        setMass(mass);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("Star");
        }
    }

    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletStar(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllStarsStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN star " + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)");
        sInsertStarStatement =
                sConnection.prepareStatement("INSERT INTO star (id, mass, radius) "
                        + "VALUES (?, ?, ?)");
        sUpdateStarStatement =
                sConnection.prepareStatement("UPDATE star " + "SET mass = ?,"
                        + " radius = ? " + "WHERE id = ?");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> stars =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet = sSelectAllStarsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between stars and celestial bodies");
                }
                Star star =
                        new Star(body,
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("radius"));
                star.setDirty(false);
                stars.put(star.getId(), star);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get stars", e);
        }
        return stars;
    }

    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add star: " + this);
        try {
            super.insertInDatabase();

            sInsertStarStatement.setInt(1, getId());
            sInsertStarStatement.setDouble(2, getMass());
            sInsertStarStatement.setDouble(3, getRadius());
            sInsertStarStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + this, e);
        }
    }

    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update star: " + this);
        try {
            super.updateInDatabase();

            sUpdateStarStatement.setDouble(1, getMass());
            sUpdateStarStatement.setDouble(2, getRadius());
            sUpdateStarStatement.setDouble(3, getId());
            sUpdateStarStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not update starary system "
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
        Element root = new Element(sConfigFile.getProperty("STAR_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"),
                String.valueOf(getRadius())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(getMass())));
        return root;
    }
}
