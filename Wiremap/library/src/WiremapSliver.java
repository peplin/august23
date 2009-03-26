//package wiremap;

import processing.core.*;

public class WiremapSliver extends WiremapShape {
    private int mHeight;
    private int mCapHeight;
    private color mCapColor;

    public WiremapSliver(Wiremap map, int x, int y, int z, color baseColor, 
            int height, int capHeight, color capColor) {
        super(map, x, y, z, baseColor);
        setHeight(height);
        setCapHeight(capHeight);
        setCapColor(capColor);
    }

    public void display() {
        pushMatrix();
        println("in display");
        for(int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mMap.getPixelsPerWire()))
                    && (mMap.getWireX(i) <= (mX + mMap.getPixelsPerWire()))) {  
                fill(mCapColor);
                noStroke();
                // top dot for sliver
                // TODO what is this y coord exactly? seems to work okay
                float y = (mMap.getParent().height / mMap.getPixelsPerInch()
                        - mY) * mMap.getPixelsPerInch();
                float left = i * mMap.getParent().width
                        / mMap.getWireCount();
                float top = y;
                float wide = 2;
                float tall = mCapHeight;
                rect(left, top, wide, tall);

                // bottom dot for sliver
                top += mHeight;
                rect(left, top, wide, tall);

                // filler for sliver
                fill(mBaseColor);
                top = y + mCapHeight;
                tall = mHeight - mCapHeight;
                rect(left, top, wide, tall);
                break; // only draws the first found
            }
        }

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
}
