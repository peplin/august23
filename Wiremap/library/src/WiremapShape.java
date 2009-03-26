//package wiremap;

import processing.core.*;

public abstract class WiremapShape {
    protected Wiremap mMap;
    protected int mX;
    protected int mY;
    protected int mZ;
    protected int mRadius;
    protected color mBaseColor;

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapShape(Wiremap map, int x, int y, int z,
            color baseColor) {
        mMap = map;
        setPosition(x, y, z);
        setBaseColor(baseColor);
    }

    protected int translateProcessingXToWiremapX(int x) {
        return (int)(x / (float)mMap.getParent().width * map.getMaplineLength() 
                - (mMap.getMaplineLength() / 2));
    }

    protected int translateProcessingYToWiremapY(int y) {
        return (int)((mMap.getParent().height - y)
                / (float)mMap.getParent().height * mMap.getHeight());
    }

    protected int translateProcessingZToWiremapZ(int z) {
        return mMap.getDepth() - z;
    }

    public abstract void display();

    public void setBaseColor(color baseColor) {
        mBaseColor = baseColor;
    }

    public void setPosition(int x, int y, int z) {
        mX = translateProcessingXToWiremapX(x);
        mY = translateProcessingYToWiremapY(y);
        mZ = translateProcessingZToWiremapZ(z);
    }
}
