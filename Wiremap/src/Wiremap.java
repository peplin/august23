package wiremap;

import processing.core.*;

/** 
 * Wiremap
 *
 * All measurements are in inches
 */
public class Wiremap {
    private PApplet mParent;
    private int mWireCount;
    private int mDepthThickness;
    private int mDepth;
    private int mMaplineLength;
    private int mHeight;
    private double mDepthUnit;
    private double mMaplineUnit;
    private int mPixelsPerWire;
    private int mPixelsPerInch;

    private int[] mWireDepths;
    private int[] mWireX;
    private int[] mWireZ;

    private void loadDepths(String wireDepthsFile) {
        String lines[] = mParent.loadStrings(wireDepthsFile);
        for(int i = 0; i < mWireCount; i++) {
            mWireDepths[i] = Integer.valueOf(lines[i]);
            float base = (float)(-mMaplineLength / 2 + i * mMaplineUnit);
            double hyp = Math.sqrt(Math.pow(base, 2) + Math.pow(mDepth, 2));
            mWireZ[i] = (int)(mDepth - (mWireDepths[i] * mDepthUnit));
            mWireX[i] = (int)(base - (base * mWireDepths[i] / hyp * mDepthUnit));
        }
    }

    public Wiremap(PApplet parent, int wireCount, int depth, int depthThickness,
            int height, int mapline, double depthUnit, double maplineUnit,
            int pixelsPerWire, String wireDepthsFile) {
        mParent = parent;
        mWireCount = wireCount;
        mDepthThickness = depthThickness;
        mDepth = depth;
        mHeight = height;
        mMaplineLength = mapline;
        mDepthUnit = depthUnit;
        mMaplineUnit = maplineUnit;
        mPixelsPerWire = pixelsPerWire;
        mPixelsPerInch = mParent.width / mMaplineLength;
        mWireDepths = new int[mWireCount];
        mWireX = new int[mWireCount];
        mWireZ = new int[mWireCount];
        loadDepths(wireDepthsFile);
    }

    public PApplet getParent() {
        return mParent;
    }

    public int getPixelsPerInch() {
        return mPixelsPerInch;
    }

    public int getPixelsPerWire() {
        return mPixelsPerWire;
    }

    public int getMaplineLength() {
        return mMaplineLength;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getDepth() {
        return mDepth;
    }

    public int getWireCount() {
        return mWireCount;
    }

    public int getWireX(int wire) {
        return mWireX[wire];
    }

    public int getWireZ(int wire) {
        return mWireZ[wire];
    }

    public void rect(int x, int y, int z, int width, int height, int depth) {

    }

    public void line(int x1, int y1, int z1, int x2, int y2, int z2) {

    }
}
