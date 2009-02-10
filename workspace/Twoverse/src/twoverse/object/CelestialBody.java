package twoverse.object;

import java.sql.Timestamp;

import nu.xom.Attribute;
import nu.xom.Element;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class CelestialBody {
    public CelestialBody(int id, User owner, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        initialize(id, owner, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        setDeathTime(deathTime);

    }

    public CelestialBody(CelestialBody body) {
        initialize(body.getId(), body.getOwner(), body.getName(), body
                .getBirthTime(), body.getDeathTime(), body.getParentId(), body
                .getPosition(), body.getVelocity(), body.getAcceleration());
    }

    private void initialize(int id, User owner, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration) {
        setId(id);
        setOwner(owner);
        setName(name);
        setBirthTime(birthTime);
        setDeathTime(deathTime);
        setParentId(parentId);
        setPosition(position);
        setVelocity(velocity);
        setAcceleration(acceleration);
        // setColor(color);
    }

    /*
     * private void setColor(Color color) { mColor = color; }
     */

    private void setAcceleration(PhysicsVector3d acceleration) {
        mAcceleration = acceleration;
    }

    private void setVelocity(PhysicsVector3d velocity) {
        mVelocity = velocity;
    }

    private void setParentId(int parentId) {
        mParentId = parentId;
    }

    private void setBirthTime(Timestamp birthTime) {
        if(birthTime == null) {
            //TODO get now time here
        }
        mBirthTime = birthTime;
    }

    private void setOwner(User owner) {
        mOwner = owner;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public User getOwner() {
        return mOwner;
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

    /*
     * public Color getColor() { return mColor; }
     */

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
        element.addAttribute(
                new Attribute("ownerId", String.valueOf(mOwner.getId())));
        element.addAttribute(
                new Attribute("birth", String.valueOf(mBirthTime.getTime())));
        element.addAttribute(
                new Attribute("death", String.valueOf(mDeathTime.getTime())));
        element.addAttribute(
                new Attribute("parentId", String.valueOf(mParentId)));


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

    private int mId;
    private User mOwner;
    private Timestamp mBirthTime;
    private Timestamp mDeathTime;
    private int mParentId;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    // private Color mColor;
    private Point mPosition;
    private String mName;

}
