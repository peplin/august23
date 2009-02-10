package twoverse.object;

import java.sql.Timestamp;

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

    //TODO currently, no error checking for missing attributes
    public CelestialBody(Element root) {
        assert(root.getLocalName().equals("celestial_body"));
        Elements positionElements = root.getChildElements("point");
        Element vectorElements = root.getChildElements("vector");

        Point position;
        for(int i = 0; i < positionElements.size(); i++) {
            Element element = pointElements.get(i);
            if(element.getAtttribute("name").equals("position")) {
                position = new Point(element.getAttribute("x"),
                                    element.getAttribute("y"),
                                    element.getAttribute("z"));
                break; // only looking for one point at the moment
            }
        }

        PhysicsVector3d velocityVector = null;
        PhysicsVector3d accelerationVector = null;
        for(int i = 0; i < vectorElements.size() 
                            && (velocityVector == null 
                                || accelerationVector == null); i++) {
            Element element = vectorElements.get(i);
            Elements directionElements = element.getChildElements("point");
            assert(direction.size() == 1);
            Element directionElement = directionElements.get(0);
            assert(directionElement.getAttribute("name").equals("direction"));
            Point direction = new Point(directionElement.getAttribute("x"),
                                    directionElement.getAttribute("y"),
                                    directionElement.getAttribute("z"));
            if(element.getAttribute("name").equals("velocity")) {
                velocityVector = new PhysicsVector3d(direction, 
                                            element.getAttribute("magnitude"));
            } else if(element.getAttribute("name").equals("acceleration")) {
                velocityVector = new PhysicsVector3d(direction, 
                                            element.getAttribute("magnitude"));
            } else {
                throw new UnhandledXmlAttribute();
            }
        }

        initialize(root.getAttribute("id"),
                    root.getAttribute("owner"),
                    root.getAttribute("name"),
                    root.getAttribute("birth"),
                    root.getAttribute("death"),
                    root.getAttribute("parentId"),
                    position,
                    velocityVector,
                    accelerationVector);
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
