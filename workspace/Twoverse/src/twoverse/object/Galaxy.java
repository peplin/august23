package twoverse.object;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class Galaxy extends CelestialBody implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 4163663398347532933L;

    public Galaxy(int id, User owner, Timestamp birthTime, Timestamp deathTime,
            int parentId, Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, GalaxyShape shape,
            double mass, double density) {
        super(id, owner, birthTime, deathTime, parentId, position, velocity,
                acceleration);
    }

    public Galaxy(CelestialBody body, GalaxyShape shape, double mass,
            double density) {
        super(body);
        setShape(shape);
        setMass(mass);
        setDensity(density);

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
