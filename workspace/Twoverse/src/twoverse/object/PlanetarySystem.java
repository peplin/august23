package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class PlanetarySystem extends CelestialBody implements Serializable {
    public PlanetarySystem(int id, User owner, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            int centerStarId, double mass) {
        super(id, owner, name, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }

    public PlanetarySystem(CelestialBody body, int centerStarId, double mass) {
        super(body);
        setCenter(centerStarId);
        setMass(mass);
    }

    public void setCenter(int center) {
        mCenterId = center;
    }

    public int getCenter() {
        return mCenterId;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    private int mCenterId;
    //TODO pointless to serialize a Star pointer....
    private Star mCenter;
    private double mMass;

}
