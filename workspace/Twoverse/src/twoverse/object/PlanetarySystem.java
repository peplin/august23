package twoverse.object;

import java.sql.Timestamp;

import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class PlanetarySystem extends CelestialBody {
    public PlanetarySystem(int id, User owner, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            int centerStarId, double mass) {
        super(id, owner, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }

    public PlanetarySystem(CelestialBody body, int centerStarId, double mass) {
        // TODO Auto-generated constructor stub
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
    private Star mCenter;
    private double mMass;

}
