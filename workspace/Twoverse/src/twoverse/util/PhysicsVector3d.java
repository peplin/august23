package twoverse.util;

// TODO What does this class need to have for our simulation? 
public class PhysicsVector3d {
    public PhysicsVector3d(double x, double x, double z, double magnitude) {
        setDirection(new Point(x, y, z));
        setMagnitude(magnitude);
    }

    public PhysicsVector3d(Point unitDirection, double magnitude) {
        setDirection(unitDirection);
        setMagnitude(magnitude);
    }

    public void setDirection(Point newDirection) {
        mUnitVectorPoint = newDirection;
    }

    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }

    public Point getUnitDirection() {
        return mUnitVectorPoint;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    private Point mUnitVectorPoint;
    private double mMagnitude;
}
