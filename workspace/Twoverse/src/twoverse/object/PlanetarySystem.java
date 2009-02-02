package twoverse.object;

import java.awt.Color;
import java.sql.Time;

import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public class PlanetarySystem extends CelestialBody {
	public PlanetarySystem(int id, 
			User owner, 
			Time birthTime, 
			Time deathTime, 
			CelestialBody parent, 
			PhysicsVector3d velocity,
			PhysicsVector3d acceleration,
			Color color,
			ManmadeBody center,
			double mass) {
		super(id, owner, birthTime, deathTime, parent, 
				velocity, acceleration, color);
	}
	
	public void setCenter(ManmadeBody center) {
		mCenter = center;
	}
	public ManmadeBody getCenter() {
		return mCenter;
	}

	public void setMass(double mass) {
		mMass = mass;
	}

	public double getMass() {
		return mMass;
	}

	private ManmadeBody mCenter;
	private double mMass;
	
}

