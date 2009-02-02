package twoverse.object;

import java.awt.Color;
import java.sql.Time;
//import javax.vecmath.GVector;

import twoverse.util.PhysicsVector3d;
import twoverse.util.User;

public abstract class CelestialBody {
	public CelestialBody(int id, 
						User owner, 
						Time birthTime, 
						Time deathTime, 
						CelestialBody parent, 
						PhysicsVector3d velocity,
						PhysicsVector3d acceleration,
						Color color) {
		
	}
	

    int getId() {
        return mId;
    }

    User getOwner() {
        return mOwner;
    }

    Time getBirthTime() {
        return mBirthTime;
    }

    Time getDeathTime() {
        return mBirthTime;
    }

    CelestialBody getParent() {
        return mParent;
    }

    PhysicsVector3d getVelocity() {
        return mVelocity;
    }

    PhysicsVector3d getAcceleration() {
        return mAcceleration;
    }

    Color getColor() {
        return mColor;
    }

    
    private int mId;
    private User mOwner;
    private Time mBirthTime;
    private Time mDeathTime;
    private CelestialBody mParent;
    private PhysicsVector3d mVelocity;
    private PhysicsVector3d mAcceleration;
    private Color mColor;
}
