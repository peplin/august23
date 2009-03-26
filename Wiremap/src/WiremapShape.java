package wiremap;

import processing.core.*;

public abstract class WiremapShape {
    protected Wiremap mMap;
    protected int mBaseColor;

    public WiremapShape(Wiremap map, int baseColor) {
        mMap = map;
        setBaseColor(baseColor);
    }

    protected int translateProcessingXToWiremapX(int x) {
        return (int)(x / (float)mMap.getParent().width * mMap.getMaplineLength() 
                - (mMap.getMaplineLength() / 2));
    }

    protected int translateProcessingYToWiremapY(int y) {
        return (int)(y / (float)mMap.getParent().height * mMap.getHeight());
    }

    protected int translateProcessingZToWiremapZ(int z) {
        return mMap.getDepth() - z;
    }

    public abstract void display();

    public void setBaseColor(int baseColor) {
        mBaseColor = baseColor;
    }
}
