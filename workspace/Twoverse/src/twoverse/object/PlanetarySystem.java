package twoverse.object;

import java.awt.Color;
import java.sql.Time;

import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class PlanetarySystem extends CelestialBody {
    // TODO maybe deathtime could just be null for alive bodies?
    public PlanetarySystem(int id, User owner, Time birthTime, Time deathTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color, Star centerStarId,
            double mass) {
        super(id, owner, birthTime, deathTime, parent, center, velocity, acceleration,
                color);
    }

    public void setCenter(Star center) {
        mCenter = center;
    }

    public Star getCenter() {
        return mCenter;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    private Star mCenter;
    private double mMass;

}
