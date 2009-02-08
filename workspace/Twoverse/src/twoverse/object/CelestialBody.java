package twoverse.object;

import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class CelestialBody {
    public CelestialBody(int id, User owner, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        initialize(id, owner, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        setDeathTime(deathTime);

    }

    public CelestialBody(CelestialBody body) {
        initialize(body.getId(), body.getOwner(), body.getBirthTime(), body
                .getDeathTime(), body.getParentId(), body.getPosition(), body
                .getVelocity(), body.getAcceleration());
    }

    private void initialize(int id, User owner, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        setId(id);
        setOwner(owner);
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
        mBirthTime = birthTime;
    }

    private void setOwner(User owner) {
        mOwner = owner;
    }

    private void setId(int id) {
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

    private int mId;
    private User mOwner;
    private Timestamp mBirthTime;
    private Timestamp mDeathTime;
    private int mParentId;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    // private Color mColor;
    private Point mPosition;

}
