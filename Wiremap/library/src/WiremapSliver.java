//package wiremap;

import processing.core.*;

public class WiremapSliver extends WiremapShape {
    private int mWire;
    private int mStartingHeight;
    private int mHeight;
    private int mCapHeight;
    private color mCapColor;

    public WiremapSliver(Wiremap map, int wire, int startingHeight,
            color baseColor, int height, int capHeight, color capColor) {
        super(map, baseColor);
        setWire(wire);
        setStartingHeight(startingHeight);
        setHeight(height);
        setCapHeight(capHeight);
        setCapColor(capColor);
    }

    public void display() {
        pushMatrix();
        noStroke();

        fill(mCapColor);
        // top dot for sliver
        float left = mWire * mMap.getParent().width / mMap.getWireCount();
        rect(left, mStartingHeight, mMap.getPixelsPerWire(), mCapHeight);

        // bottom dot for sliver
        rect(left, mStartingHeight + mHeight, mMap.getPixelsPerWire(),
                mCapHeight);

        // filler for sliver
        fill(mBaseColor);
        rect(left, mStartingHeight + mCapHeight, mMap.getPixelsPerWire(),
                mHeight - mCapHeight);
        popMatrix();
    }

    public void setCapHeight(int height) {
        mCapHeight = height;
    }

    public void setCapColor(color capColor) {
        mCapColor = capColor;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setWire(int wire) {
        mWire = wire;
    }

    public void setStartingHeight(int height) {
        mStartingHeight = height;
    }
}
