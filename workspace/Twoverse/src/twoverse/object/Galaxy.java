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
	public Galaxy(int id, 
			User owner, 
			Time birthTime, 
			Time deathTime, 
			CelestialBody parent, 
			PhysicsVector3d velocity,
			PhysicsVector3d acceleration,
			Color color,
			GalaxyShape shape, 
			double mas, 
			double density) {
		super(id, owner, birthTime, deathTime, parent, 
				velocity, acceleration, color);
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

