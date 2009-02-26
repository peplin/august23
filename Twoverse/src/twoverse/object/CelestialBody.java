package twoverse.object;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.MissingXmlElementException;
import twoverse.util.XmlExceptions.UnexpectedXmlAttributeException;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class CelestialBody implements Serializable {
    private static Properties sConfigFile;
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
    private static final long serialVersionUID = -6341175711814973441L;
    private boolean mDirty = true; // dirty if different than version in
                                   // database

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
        initialize(-1,
                ownerId,
                name,
                null,
                null,
                parentId,
                position,
                velocity,
                acceleration);

    }

    public CelestialBody(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        loadConfig();
        initialize(id,
                ownerId,
                name,
                birthTime,
                deathTime,
                parentId,
                position,
                velocity,
                acceleration);
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
                body.getAcceleration());
    }

    public CelestialBody(Element root) throws UnexpectedXmlElementException {
        loadConfig();

        if(!root.getLocalName()
                .equals(sConfigFile.getProperty("CELESTIAL_BODY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a celestial body");
        }

        Elements positionElements =
                root.getChildElements(sConfigFile.getProperty("POINT_TAG"));
        Point position = null;
        for(int i = 0; i < positionElements.size() && position == null; i++) {
            Element element = positionElements.get(i);
            if(element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sConfigFile.getProperty("POSITION_ATTRIBUTE_VALUE"))) {
                position = new Point(element);
            } else {
                throw new UnexpectedXmlElementException("Unknown point element with name: "
                        + element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG")));
            }
        }

        if(position == null) {
            throw new MissingXmlElementException("Expected point object for position");
        }

        Elements vectorElements =
                root.getChildElements(sConfigFile.getProperty("VECTOR_TAG"));
        PhysicsVector3d velocityVector = null;
        PhysicsVector3d accelerationVector = null;
        for(int i = 0; i < vectorElements.size()
                && (velocityVector == null || accelerationVector == null); i++) {
            Element element = vectorElements.get(i);
            if(element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sConfigFile.getProperty("VELOCITY_ATTRIBUTE_VALUE"))) {
                velocityVector = new PhysicsVector3d(element);
            } else if(element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                    .getValue()
                    .equals(sConfigFile.getProperty("ACCELERATION_ATTRIBUTE_VALUE"))) {
                accelerationVector = new PhysicsVector3d(element);
            } else {
                throw new UnexpectedXmlAttributeException("Unexpected attribute: "
                        + element.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                                .getValue());
            }
        }

        Timestamp deathTime = null;
        if(root.getAttribute(sConfigFile.getProperty("DEATH_ATTRIBUTE_TAG")) != null) {
            deathTime =
                    new Timestamp(Long.valueOf(root.getAttribute(sConfigFile.getProperty("DEATH_ATTRIBUTE_TAG"))
                            .getValue()));
        }
        initialize(Integer.valueOf(root.getAttribute(sConfigFile.getProperty("ID_ATTRIBUTE_TAG"))
                .getValue()),
                Integer.valueOf(root.getAttribute(sConfigFile.getProperty("OWNER_ATTRIBUTE_TAG"))
                        .getValue()),
                root.getAttribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"))
                        .getValue(),
                new Timestamp(Long.valueOf(root.getAttribute(sConfigFile.getProperty("BIRTH_ATTRIBUTE_TAG"))
                        .getValue())),
                deathTime,
                Integer.valueOf(root.getAttribute(sConfigFile.getProperty("PARENT_ID_ATTRIBUTE_TAG"))
                        .getValue()),
                position,
                velocityVector,
                accelerationVector);
    }

    private void initialize(int id, int ownerId, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration) {
        setId(id);
        setOwnerId(ownerId);
        setName(name);
        setBirthTime(birthTime);
        setDeathTime(deathTime);
        setParentId(parentId);
        setPosition(position);
        setVelocity(velocity);
        setAcceleration(acceleration);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("CelestialBody");
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

    public Element toXmlElement() {
        loadConfig();
        Element element =
                new Element(sConfigFile.getProperty("CELESTIAL_BODY_TAG"));

        element.addAttribute(new Attribute(sConfigFile.getProperty("ID_ATTRIBUTE_TAG"),
                String.valueOf(mId)));
        element.addAttribute(new Attribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                mName));
        element.addAttribute(new Attribute(sConfigFile.getProperty("OWNER_ATTRIBUTE_TAG"),
                String.valueOf(mOwnerId)));
        element.addAttribute(new Attribute(sConfigFile.getProperty("BIRTH_ATTRIBUTE_TAG"),
                String.valueOf(mBirthTime.getTime())));
        if(mDeathTime != null) {
            element.addAttribute(new Attribute(sConfigFile.getProperty("DEATH_ATTRIBUTE_TAG"),
                    String.valueOf(mBirthTime.getTime())));
        }
        element.addAttribute(new Attribute(sConfigFile.getProperty("PARENT_ID_ATTRIBUTE_TAG"),
                String.valueOf(mParentId)));

        Element velocityElement = mVelocity.toXmlElement();
        velocityElement.addAttribute(new Attribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sConfigFile.getProperty("VELOCITY_ATTRIBUTE_VALUE")));
        element.appendChild(velocityElement);

        Element accelerationElement = mAcceleration.toXmlElement();
        accelerationElement.addAttribute(new Attribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sConfigFile.getProperty("ACCELERATION_ATTRIBUTE_VALUE")));
        element.appendChild(accelerationElement);

        Element positionElement = mPosition.toXmlElement();
        positionElement.addAttribute(new Attribute(sConfigFile.getProperty("NAME_ATTRIBUTE_TAG"),
                sConfigFile.getProperty("POSITION_ATTRIBUTE_VALUE")));
        element.appendChild(positionElement);
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
                + getPosition() + ", " + "dirty: " + isDirty() + "]";
    }
}
