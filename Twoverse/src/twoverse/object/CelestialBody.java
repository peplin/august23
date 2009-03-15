package twoverse.object;

import java.io.IOException;
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
import java.util.logging.Logger;

import processing.core.PApplet;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.Point.TwoDimensionalException;
import twoverse.util.XmlExceptions.MissingXmlElementException;
import twoverse.util.XmlExceptions.UnexpectedXmlAttributeException;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class CelestialBody implements Serializable {
    private static Properties sCelestialBodyConfigFile;
    protected static Logger sLogger =
            Logger.getLogger(CelestialBody.class.getName());
    private int mId;
    private int mOwnerId;
    private Timestamp mBirthTime;
    private Timestamp mDeathTime;
    private int mParentId;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    private Point mPosition;
    private String mName;
    private Vector<Integer> mChildren;
    private static final long serialVersionUID = -6341175711814973441L;
    private boolean mDirty = true; // dirty if different than version in
    // database
    private static PreparedStatement sSelectStatement;
    private static PreparedStatement sInsertStatement;
    private static PreparedStatement sDeleteStatement;
    private static PreparedStatement sUpdateStatement;
    private static Connection sConnection;

    // can't pull out to config file, as we need it before the constructor in
    // derived classes
    protected final static String XML_TAG = "CelestialBody";

    /**
     * Don't call this - needs to be here so its children are serializable.
     */
    public CelestialBody() {
    }

    public CelestialBody(int ownerId, String name, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration) {
        loadConfig();
        initialize(0,
                ownerId,
                name,
                null,
                null,
                parentId,
                position,
                velocity,
                acceleration,
                new Vector<Integer>());

    }

    public CelestialBody(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children) {
        loadConfig();
        initialize(id,
                ownerId,
                name,
                birthTime,
                deathTime,
                parentId,
                position,
                velocity,
                acceleration,
                children);
    }

    public CelestialBody(CelestialBody body) {
        loadConfig();
        initialize(body.getId(),
                body.getOwnerId(),
                body.getName(),
                body.getBirthTime(),
                body.getDeathTime(),
                body.getParentId(),
                body.getPosition(),
                body.getVelocity(),
                body.getAcceleration(),
                body.getChildren());
    }

    public CelestialBody(Element root) throws UnexpectedXmlElementException {
        loadConfig();

        if(!root.getLocalName()
                .equals(sCelestialBodyConfigFile.getProperty("CELESTIAL_BODY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a celestial body");
        }

        Elements positionElements =
                root.getChildElements(sCelestialBodyConfigFile.getProperty("POINT_TAG"));
        Point position = null;
        for(int i = 0; i < positionElements.size() && position == null; i++) {
            Element element = positionElements.get(i);
            if(element.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sCelestialBodyConfigFile.getProperty("POSITION_ATTRIBUTE_VALUE"))) {
                position = new Point(element);
            } else {
                throw new UnexpectedXmlElementException("Unknown point element with name: "
                        + element.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG")));
            }
        }

        if(position == null) {
            throw new MissingXmlElementException("Expected point object for position");
        }

        Elements vectorElements =
                root.getChildElements(sCelestialBodyConfigFile.getProperty("VECTOR_TAG"));
        PhysicsVector3d velocityVector = null;
        PhysicsVector3d accelerationVector = null;
        for(int i = 0; i < vectorElements.size()
                && (velocityVector == null || accelerationVector == null); i++) {
            Element element = vectorElements.get(i);
            if(element.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sCelestialBodyConfigFile.getProperty("VELOCITY_ATTRIBUTE_VALUE"))) {
                velocityVector = new PhysicsVector3d(element);
            } else if(element.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sCelestialBodyConfigFile.getProperty("ACCELERATION_ATTRIBUTE_VALUE"))) {
                accelerationVector = new PhysicsVector3d(element);
            } else {
                throw new UnexpectedXmlAttributeException("Unexpected attribute: "
                        + element.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                                .getValue());
            }
        }

        Timestamp deathTime = null;
        if(root.getAttribute(sCelestialBodyConfigFile.getProperty("DEATH_ATTRIBUTE_TAG")) != null) {
            deathTime =
                    new Timestamp(Long.valueOf(root.getAttribute(sCelestialBodyConfigFile.getProperty("DEATH_ATTRIBUTE_TAG"))
                            .getValue()));
        }

        Vector<Integer> children = new Vector<Integer>();

        Element childrenVectorElement =
                root.getFirstChildElement(sCelestialBodyConfigFile.getProperty("CHILDREN_ELEMENT_TAG"));
        Elements childValueElements =
                childrenVectorElement.getChildElements(sCelestialBodyConfigFile.getProperty("CHILDREN_VALUE_ELEMENT_TAG"));
        for(int i = 0; i < childValueElements.size(); i++) {
            Element childElement = childValueElements.get(i);
            children.add(Integer.valueOf(childElement.getValue()));
        }

        initialize(Integer.valueOf(root.getAttribute(sCelestialBodyConfigFile.getProperty("ID_ATTRIBUTE_TAG"))
                .getValue()),
                Integer.valueOf(root.getAttribute(sCelestialBodyConfigFile.getProperty("OWNER_ATTRIBUTE_TAG"))
                        .getValue()),
                root.getAttribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                        .getValue(),
                new Timestamp(Long.valueOf(root.getAttribute(sCelestialBodyConfigFile.getProperty("BIRTH_ATTRIBUTE_TAG"))
                        .getValue())),
                deathTime,
                Integer.valueOf(root.getAttribute(sCelestialBodyConfigFile.getProperty("PARENT_ID_ATTRIBUTE_TAG"))
                        .getValue()),
                position,
                velocityVector,
                accelerationVector,
                children);
    }

    private void initialize(int id, int ownerId, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Vector<Integer> children) {
        setId(id);
        setOwnerId(ownerId);
        setName(name);
        setBirthTime(birthTime);
        setDeathTime(deathTime);
        setParentId(parentId);
        setPosition(position);
        setVelocity(velocity);
        setAcceleration(acceleration);
        mChildren = new Vector<Integer>(children);
    }

    private synchronized void loadConfig() {
        if(sCelestialBodyConfigFile == null) {
            sCelestialBodyConfigFile = loadConfigFile("CelestialBody");
        }
    }

    protected synchronized Properties loadConfigFile(String className) {
        Properties configFile = null;
        try {
            configFile = new Properties();
            configFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/" + className
                            + ".properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, "Unable to load config: "
                    + e.getMessage(), e);
        }
        return configFile;
    }

    protected static Logger getLogger() {
        return sLogger;
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "WHERE object.id = ?");
        sInsertStatement =
                sConnection.prepareStatement("INSERT INTO object (name, owner, parent, x, y, z, velocity_magnitude, "
                        + "velocity_vector_x, velocity_vector_y, velocity_vector_z, "
                        + "accel_magnitude, accel_vector_x, accel_vector_y, "
                        + "accel_vector_z) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        sDeleteStatement =
                sConnection.prepareStatement("DELETE FROM object "
                        + "WHERE id = ?");
        sUpdateStatement =
                sConnection.prepareStatement("UPDATE object "
                        + "SET name = ?, owner = ?, parent = ?, x = ?, "
                        + "y = ?, z = ?, velocity_magnitude = ?, "
                        + "velocity_vector_x = ?, velocity_vector_y = ?,"
                        + "velocity_vector_z = ?, accel_magnitude = ?,"
                        + "accel_vector_x = ?, accel_vector_y = ?, "
                        + "accel_vector_z = ? WHERE id = ?");
    }

    public void deleteFromDatabase() throws SQLException {
        if(sConnection == null) {
            throw new SQLException("Unset database connection");
        }
        try {
            sDeleteStatement.setInt(1, getId());
            sDeleteStatement.executeUpdate();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not delete body " + this, e);
        }
    }

    public synchronized void insertInDatabase() throws SQLException {
        if(sConnection == null) {
            throw new SQLException("Unset database connection");
        }
        try {
            sInsertStatement.setString(1, getName());

            if(getOwnerId() != 0) {
                sInsertStatement.setInt(2, getOwnerId());
            } else {
                sInsertStatement.setNull(2, Types.INTEGER);
            }

            if(getParentId() != 0) {
                sInsertStatement.setInt(3, getParentId());
            } else {
                sInsertStatement.setNull(3, Types.INTEGER);
            }
            sInsertStatement.setDouble(4, getPosition().getX());
            sInsertStatement.setDouble(5, getPosition().getY());
            sInsertStatement.setDouble(6, getPosition().getZ());
            sInsertStatement.setDouble(7, getVelocity().getMagnitude());
            sInsertStatement.setDouble(8, getVelocity().getUnitDirection()
                    .getX());
            sInsertStatement.setDouble(9, getVelocity().getUnitDirection()
                    .getY());
            sInsertStatement.setDouble(10, getVelocity().getUnitDirection()
                    .getZ());
            sInsertStatement.setDouble(11, getAcceleration().getMagnitude());
            sInsertStatement.setDouble(12, getAcceleration().getUnitDirection()
                    .getX());
            sInsertStatement.setDouble(13, getAcceleration().getUnitDirection()
                    .getY());
            sInsertStatement.setDouble(14, getAcceleration().getUnitDirection()
                    .getZ());
            sInsertStatement.executeUpdate();
            ResultSet keySet = sInsertStatement.getGeneratedKeys();
            if(!keySet.next()) {
                throw new SQLException("Couldn't find key of object we just added");
            }
            setId(keySet.getInt(1));
            keySet.close();
            sSelectStatement.setInt(1, getId());
            ResultSet resultSet = sSelectStatement.executeQuery();
            if(!resultSet.next()) {
                throw new SQLException("Couldn't find object we just added");
            }
            setBirthTime(resultSet.getTimestamp("birth"));
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Add celestial body query failed for body: " + this,
                    e);
            throw new SQLException("Add celestial body query failed for body: "
                    + this);
        } catch(TwoDimensionalException e) {
            sLogger.log(Level.WARNING,
                    "Expected 3D point but was 2D: " + this,
                    e);
        }
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        return null;
    }

    public static synchronized CelestialBody selectFromDatabase(int id)
            throws SQLException {
        CelestialBody body = null;
        if(sConnection == null) {
            throw new SQLException("Unset database connection");
        }
        try {
            sSelectStatement.setInt(1, id);
            ResultSet resultSet = sSelectStatement.executeQuery();
            if(!resultSet.next()) {
                throw new SQLException("Couldn't find object for id " + id);
            }
            body = parse(resultSet);
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Select celestial body query failed for id: " + id,
                    e);
        }
        return body;
    }

    public synchronized void updateInDatabase() throws SQLException {
        if(sConnection == null) {
            throw new SQLException("Unset database connection");
        }
        try {
            sUpdateStatement.setString(1, getName());

            if(getOwnerId() != 0) {
                sUpdateStatement.setInt(2, getOwnerId());
            } else {
                sUpdateStatement.setNull(2, Types.INTEGER);
            }

            if(getParentId() != 0) {
                sUpdateStatement.setInt(3, getParentId());
            } else {
                sUpdateStatement.setNull(3, Types.INTEGER);
            }

            sUpdateStatement.setDouble(4, getPosition().getX());
            sUpdateStatement.setDouble(5, getPosition().getY());
            sUpdateStatement.setDouble(6, getPosition().getZ());
            sUpdateStatement.setDouble(7, getVelocity().getMagnitude());
            sUpdateStatement.setDouble(8, getVelocity().getUnitDirection()
                    .getX());
            sUpdateStatement.setDouble(9, getVelocity().getUnitDirection()
                    .getY());
            sUpdateStatement.setDouble(10, getVelocity().getUnitDirection()
                    .getZ());
            sUpdateStatement.setDouble(11, getAcceleration().getMagnitude());
            sUpdateStatement.setDouble(12, getAcceleration().getUnitDirection()
                    .getX());
            sUpdateStatement.setDouble(13, getAcceleration().getUnitDirection()
                    .getY());
            sUpdateStatement.setDouble(14, getAcceleration().getUnitDirection()
                    .getZ());
            sUpdateStatement.setInt(15, getId());
            sUpdateStatement.executeUpdate();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Update celestial body query failed for body: " + this,
                    e);
        } catch(TwoDimensionalException e) {
            sLogger.log(Level.WARNING,
                    "Expected 3D point but was 2D: " + this,
                    e);
        }
    }

    static protected ArrayList<CelestialBody> parseAll(ResultSet resultSet) {
        ArrayList<CelestialBody> bodies = new ArrayList<CelestialBody>();
        try {
            while(resultSet.next()) {
                bodies.add(parse(resultSet));
            }
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Unable to parse celestial bodies from set: " + resultSet,
                    e);
        }

        return bodies;
    }

    static protected CelestialBody parse(ResultSet resultSet) {
        CelestialBody body = null;
        try {
            body =
                    new CelestialBody(resultSet.getInt("object.id"),
                            resultSet.getInt("object.owner"),
                            resultSet.getString("object.name"),
                            resultSet.getTimestamp("birth"),
                            resultSet.getTimestamp("death"),
                            resultSet.getInt("parent"),
                            new Point(resultSet.getInt("x"),
                                    resultSet.getInt("y"),
                                    resultSet.getInt("z")),
                            new PhysicsVector3d(resultSet.getDouble("velocity_vector_x"),
                                    resultSet.getDouble("velocity_vector_y"),
                                    resultSet.getDouble("velocity_vector_z"),
                                    resultSet.getDouble("velocity_magnitude")),
                            new PhysicsVector3d(resultSet.getDouble("accel_vector_x"),
                                    resultSet.getDouble("accel_vector_y"),
                                    resultSet.getDouble("accel_vector_z"),
                                    resultSet.getDouble("accel_magnitude")),
                            new Vector<Integer>());
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Unable to parse celestial bodies from set: " + resultSet,
                    e);
        }
        return body;
    }

    public Element toXmlElement() {
        loadConfig();
        Element element =
                new Element(sCelestialBodyConfigFile.getProperty("CELESTIAL_BODY_TAG"));

        element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("ID_ATTRIBUTE_TAG"),
                String.valueOf(mId)));
        element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                mName));
        element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("OWNER_ATTRIBUTE_TAG"),
                String.valueOf(mOwnerId)));
        element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("BIRTH_ATTRIBUTE_TAG"),
                String.valueOf(mBirthTime.getTime())));
        if(mDeathTime != null) {
            element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("DEATH_ATTRIBUTE_TAG"),
                    String.valueOf(mBirthTime.getTime())));
        }
        element.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("PARENT_ID_ATTRIBUTE_TAG"),
                String.valueOf(mParentId)));

        Element velocityElement = mVelocity.toXmlElement();
        velocityElement.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sCelestialBodyConfigFile.getProperty("VELOCITY_ATTRIBUTE_VALUE")));
        element.appendChild(velocityElement);

        Element accelerationElement = mAcceleration.toXmlElement();
        accelerationElement.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sCelestialBodyConfigFile.getProperty("ACCELERATION_ATTRIBUTE_VALUE")));
        element.appendChild(accelerationElement);

        Element positionElement = mPosition.toXmlElement();
        positionElement.addAttribute(new Attribute(sCelestialBodyConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sCelestialBodyConfigFile.getProperty("POSITION_ATTRIBUTE_VALUE")));
        element.appendChild(positionElement);

        Element vectorElement =
                new Element(sCelestialBodyConfigFile.getProperty("CHILDREN_ELEMENT_TAG"));
        for(int i : mChildren) {
            Element vectorValueElement =
                    new Element(sCelestialBodyConfigFile.getProperty("CHILDREN_VALUE_ELEMENT_TAG"));
            vectorValueElement.appendChild(String.valueOf(i));
            vectorElement.appendChild(vectorValueElement);
        }
        element.appendChild(vectorElement);
        return element;
    }

    private void setAcceleration(PhysicsVector3d acceleration) {
        mAcceleration = acceleration;
    }

    private void setVelocity(PhysicsVector3d velocity) {
        mVelocity = velocity;
    }

    private void setParentId(int parentId) {
        mParentId = parentId;
    }

    public void setBirthTime(Timestamp birthTime) {
        mBirthTime = birthTime;
    }

    public void setOwnerId(int ownerId) {
        mOwnerId = ownerId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public Timestamp getBirthTime() {
        return mBirthTime;
    }

    public Timestamp getDeathTime() {
        return mDeathTime;
    }

    public void setDeathTime(Timestamp timestamp) {
        mDeathTime = timestamp;
    }

    public int getParentId() {
        return mParentId;
    }

    public PhysicsVector3d getVelocity() {
        return mVelocity;
    }

    public PhysicsVector3d getAcceleration() {
        return mAcceleration;
    }

    public void setPosition(Point position) {
        mPosition = position;
    }

    public Point getPosition() {
        return mPosition;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public boolean isDirty() {
        return mDirty;
    }

    public void setDirty(boolean dirty) {
        mDirty = dirty;
    }

    public String toString() {
        return "[id: " + getId() + ", " + "owner id: " + getOwnerId() + ", "
                + "name: " + getName() + ", " + "birth: " + getBirthTime()
                + ", " + "death: " + getDeathTime() + ", " + "parent id: "
                + getParentId() + ", " + "velocity: " + getVelocity() + ", "
                + "acceleration: " + getAcceleration() + ", " + "position: "
                + getPosition() + ", " + "dirty: " + isDirty() + ", "
                + getChildren() + "]";
    }

    public AppletBodyInterface getAsApplet(PApplet parent) {
        return null;
    }

    public void addChild(int id) {
        mChildren.add(id);
    }

    public Vector<Integer> getChildren() {
        return mChildren;
    }

    public void update(CelestialBody body) {
        // TODO Auto-generated method stub

    }
}
