package twoverse.object;

import java.awt.Color;
import java.sql.Time; //import javax.vecmath.GVector;

import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class CelestialBody {
    public CelestialBody(int id, User owner, Timestamp birthTime, Time deathTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color) {
        initialize(id, owner, birthTime, parent, center, velocity, acceleration, color);
        setDeathTime(deathTime);

    }

    public CelestialBody(int id, User owner, Timestamp birthTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color) {
        initialize(id, owner, birthTime, parent, center, velocity, acceleration, color);
    }

    private void initialize(int id, User owner, Timestamp birthTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color) {
        setId(id);
        setOwner(owner);
        setBirthTime(birthTime);
        setParent(parent);
        setCenter(center);
        setVelocity(velocity);
        setAcceleration(acceleration);
        setColor(color);
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
        return mBirthTime;
    }

    public CelestialBody getParentId() {
        return mParentId;
    }

    public PhysicsVector3d getVelocity() {
        return mVelocity;
    }

    public PhysicsVector3d getAcceleration() {
        return mAcceleration;
    }

    public Color getColor() {
        return mColor;
    }

    private int mId;
    private User mOwner;
    private Timestamp mBirthTime;
    private Timestamp mDeathTime;
    private int mParent;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    private Color mColor;
}
