package twoverse.object;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
import twoverse.object.applet.AppletPlanetarySystem;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class PlanetarySystem extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private int mCenterId;
    private double mMass;
    private static PreparedStatement sSelectAllPlanetarySystemsStatement;
    private static PreparedStatement sInsertPlanetarySystemStatement;
    private static PreparedStatement sUpdatePlanetarySystemStatement;
    private static Connection sConnection;

    public PlanetarySystem(int ownerId, String name, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, int centerStarId, double mass) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(int id, int ownerId, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Vector<Integer> children,
            int centerStarId, double mass) {
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
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(CelestialBody body, int centerStarId, double mass) {
        super(body);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("PLANETARY_SYSTEM_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a planetary system");
        }

        int centerStarId =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("CENTER_ID_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        initialize(centerStarId, mass);
    }

    public PlanetarySystem(PlanetarySystem system) {
        super(system);
        initialize(system.getCenterId(), system.getMass());
    }

    private void initialize(int centerStarId, double mass) {
        setCenter(centerStarId);
        setMass(mass);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("PlanetarySystem");
        }
    }

    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletPlanetarySystem(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllPlanetarySystemsStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN planetary_system "
                        + "LEFT JOIN (user) " + "ON (object.owner = user.id)");
        sInsertPlanetarySystemStatement =
                sConnection.prepareStatement("INSERT INTO planetary_system (id, centerid, mass) "
                        + "VALUES (?, ?, ?)");
        sUpdatePlanetarySystemStatement =
                sConnection.prepareStatement("UPDATE planetary_system "
                        + "SET centerid = ?," + " mass = ? " + "WHERE id = ?");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> systems =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet =
                    sSelectAllPlanetarySystemsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between systems and celestial bodies");
                }
                PlanetarySystem system =
                        new PlanetarySystem(body,
                                resultSet.getInt("centerid"),
                                resultSet.getDouble("mass"));
                system.setDirty(false);
                systems.put(system.getId(), system);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get planetary systems", e);
        }
        return systems;
    }

    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add system: " + this);
        try {
            super.insertInDatabase();

            sInsertPlanetarySystemStatement.setInt(1, getId());

            if(getCenterId() != 0) {
                sInsertPlanetarySystemStatement.setInt(2, getCenterId());
            } else {
                sInsertPlanetarySystemStatement.setNull(2, Types.INTEGER);
            }

            sInsertPlanetarySystemStatement.setDouble(3, getMass());
            sInsertPlanetarySystemStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + this, e);
        }

    }

    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update system: " + this);
        try {
            super.updateInDatabase();

            sUpdatePlanetarySystemStatement.setInt(1, getCenterId());
            sUpdatePlanetarySystemStatement.setDouble(2, getMass());
            sUpdatePlanetarySystemStatement.setDouble(3, getId());
            sUpdatePlanetarySystemStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not update planetary system "
                    + this, e);
        }

    }

    public void setCenter(int center) {
        mCenterId = center;
    }

    public int getCenterId() {
        return mCenterId;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root =
                new Element(sConfigFile.getProperty("PLANETARY_SYSTEM_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("CENTER_ID_ATTRIBUTE_TAG"),
                String.valueOf(mCenterId)));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(mMass)));
        return root;
    }
}
