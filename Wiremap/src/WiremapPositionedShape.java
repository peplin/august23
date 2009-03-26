package wiremap;

import processing.core.*;

public abstract class WiremapPositionedShape extends WiremapShape {
    protected int mX;
    protected int mY;
    protected int mZ;
    protected int mRadius;

    /**
     * z >= 0, z <= mDepthThickness (inches)
     */
    public WiremapPositionedShape(Wiremap map, int x, int y, int z,
            int baseColor) {
        super(map, baseColor);
        setPosition(x, y, z);
    }

    public void setPosition(int x, int y, int z) {
        mX = translateProcessingXToWiremapX(x);
        mY = translateProcessingYToWiremapY(y);
        mZ = translateProcessingZToWiremapZ(z);
    }
}
