package twoverse.object;

import java.awt.Color;
import java.io.Serializable;
import java.sql.Time;

import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class Galaxy extends CelestialBody implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 4163663398347532933L;

    // TODO point class
    public Galaxy(int id, User owner, Timestamp birthTime, Time deathTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color, GalaxyShape shape,
            double mass, double density) {
        super(id, owner, birthTime, deathTime, parent, velocity, acceleration,
                color);
    }

    public Galaxy(int id, User owner, Timestamp birthTime,
            CelestialBody parent, Point center, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, Color color, GalaxyShape shape,
            double mass, double density) {
        super(id, owner, birthTime, parent, velocity, acceleration,
                color);
    }

    public void setShape(GalaxyShape shape) {
        mShape = shape;
    }

    public GalaxyShape getShape() {
        return mShape;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    public void setDensity(double density) {
        mDensity = density;
    }

    public double getDensity() {
        return mDensity;
    }

    private GalaxyShape mShape;
    private double mMass;
    private double mDensity;
}
