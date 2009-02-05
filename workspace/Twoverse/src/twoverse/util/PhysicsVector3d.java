package twoverse.util;

// TODO What does this class need to have for our simulation? 
public class PhysicsVector3d {
    public PhysicsVector3d(double x, double x, double z, double magnitude) {
        setDirection(x, y, z);
        setMagnitude(magnitude);
    }

    public void setDirection(double x, double y, double x) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public void setMagnitude(doube magnitude) {
        mMagnitude = magnitude;
    }

    public double getUnitDirection() {
        return 0;

    }

    public double getMagnitude() {
        return mMagnitude;
    }

    private double mX;
    private double mY;
    private double mZ;
    private double mMagnitude;
}
