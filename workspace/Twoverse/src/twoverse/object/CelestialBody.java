package twoverse.object;

import java.sql.Timestamp;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.XMLException;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class CelestialBody {
    // TODO need simpler constructors for most cases
    public CelestialBody(int id, int ownerId, String name, Timestamp birthTime,
                         Timestamp deathTime, int parentId, Point position,
                         PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        initialize(id, ownerId, name, birthTime, deathTime, parentId, position,
            velocity, acceleration);
    }

    public CelestialBody(CelestialBody body) {
        initialize(body.getId(), body.getOwnerId(), body.getName(), body
                .getBirthTime(), body.getDeathTime(), body.getParentId(), body
                .getPosition(), body.getVelocity(), body.getAcceleration());
    }

    public CelestialBody(Element root) throws XMLException {
        if (!root.getLocalName().equals("celestial_body")) {
            throw new XMLException("Element is not a celestial body");
        }
        Elements positionElements = root.getChildElements("point");
        Elements vectorElements = root.getChildElements("vector");

        Point position = null;
        for (int i = 0; i < positionElements.size(); i++) {
            Element element = positionElements.get(i);
            if (element.getAttribute("name").equals("position")) {
                position =
                        new Point(Double.valueOf(element.getAttribute("x")
                                .getValue()), Double.valueOf(element
                                .getAttribute("y").getValue()), Double
                                .valueOf(element.getAttribute("z").getValue()));
                break; // only looking for one point at the moment
            }
        }

        if (position == null) {
            throw new XMLException("Expected point object for position");
        }

        PhysicsVector3d velocityVector = null;
        PhysicsVector3d accelerationVector = null;
        for (int i = 0; i < vectorElements.size()
            && (velocityVector == null || accelerationVector == null); i++) {
            Element element = vectorElements.get(i);
            Elements directionElements = element.getChildElements("point");
            if (directionElements.size() != 1) {
                throw new XMLException("Multiple points defined for vector");
            }
            Element directionElement = directionElements.get(0);
            if (!directionElement.getAttribute("name").equals("direction")) {
                throw new XMLException("Unexpected point for name: "
                    + directionElement.getAttribute("name"));
            }
            Point direction =
                    new Point(Double.valueOf(directionElement.getAttribute("x")
                            .getValue()), Double.valueOf(directionElement
                            .getAttribute("y").getValue()), Double
                            .valueOf(directionElement.getAttribute("z")
                                    .getValue()));
            double magnitude =
                    Double
                            .valueOf(element.getAttribute("magnitude")
                                    .getValue());
            if (element.getAttribute("name").equals("velocity")) {
                velocityVector = new PhysicsVector3d(direction, magnitude);

            } else if (element.getAttribute("name").equals("acceleration")) {
                velocityVector = new PhysicsVector3d(direction, magnitude);
            } else {
                throw new XMLException("Unexpected attribute: "
                    + element.getAttribute("name").getValue());
            }
        }

        initialize(Integer.valueOf(root.getAttribute("id").getValue()), Integer
                .valueOf(root.getAttribute("owner").getValue()), root
                .getAttribute("name").getValue(), Timestamp.valueOf(root
                .getAttribute("birth").getValue()), Timestamp.valueOf(root
                .getAttribute("death").getValue()), Integer.valueOf(root
                .getAttribute("parentId").getValue()), position,
            velocityVector, accelerationVector);
    }

    private void initialize(int id, int ownerId, String name,
                            Timestamp birthTime, Timestamp deathTime,
                            int parentId, Point position,
                            PhysicsVector3d velocity,
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

    private void setOwnerId(User owner) {
        mOwnerId = owner.getId();
    }

    private void setOwnerId(int ownerId) {
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

    public void appendXmlAttributes(Element element) {
        element.addAttribute(new Attribute("id", String.valueOf(mId)));
        element.addAttribute(new Attribute("name", mName));
        element
                .addAttribute(new Attribute("ownerId", String.valueOf(mOwnerId)));
        element.addAttribute(new Attribute("birth", String.valueOf(mBirthTime
                .getTime())));
        element.addAttribute(new Attribute("death", String.valueOf(mDeathTime
                .getTime())));
        element.addAttribute(new Attribute("parentId", String
                .valueOf(mParentId)));

        // TODO is this the best way to do this? think about how parsing will
        // work, that will point in the right direction
        Element velocityElement = mVelocity.toXmlElement();
        velocityElement.addAttribute(new Attribute("name", "velocity"));
        element.appendChild(velocityElement);

        Element accelerationElement = mAcceleration.toXmlElement();
        accelerationElement.addAttribute(new Attribute("name", "acceleration"));
        element.appendChild(accelerationElement);

        Element positionElement = mPosition.toXmlElement();
        positionElement.addAttribute(new Attribute("name", "position"));
        element.appendChild(positionElement);
    }

    public Element toXmlElement() {
        Element root = new Element("celestial_body");
        appendXmlAttributes(root);
        return root;
    }

    public class UnhandledXmlAttributeException extends Exception {
        UnhandledXmlAttributeException(String e) {
            super(e);
        }
    }

    public class MissingXmlAttributeException extends Exception {
        MissingXmlAttributeException(String e) {
            super(e);
        }
    }

    private int mId;
    private int mOwnerId;
    private Timestamp mBirthTime;
    private Timestamp mDeathTime;
    private int mParentId;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    private Point mPosition;
    private String mName;

}
