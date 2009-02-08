package twoverse.util;

public class Point {
    public Point(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setX(double x) {
        mX = x;
    }
    public double getX() {
        return mX;
    }

    public void setY(double y) {
        mY = y;
    }

    public double getY() {
        return mY;
    }

    public void setZ(double z) {
        mZ = z;
    }

    public double getZ() {
        return mZ;
    }

    private double mX;
    private double mY;
    private double mZ;
}
