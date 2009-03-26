package wiremap;

import processing.core.*;

public class WiremapRectangle extends WiremapPositionedShape {
    protected int mWidth;
    protected int mHeight;
    protected int mDepth;
    protected int mBorderHeight;
    protected int mBorderColor;

    private int[][] mCornerPositions = new int[8][3];

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapRectangle(Wiremap map, int x, int y, int z, int baseColor,
            int width, int height, int depth, int borderHeight, int borderColor) {
        super(map, x, y, z, baseColor);
        setSize(width, height, depth);
        setBorderHeight(borderHeight);
        setBorderColor(borderColor);
    }

    public void display() {
        mMap.getParent().pushMatrix();
        for (int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mWidth / 2))
                    && (mMap.getWireX(i) <= (mX + mWidth / 2))) {
                double distanceToCenter =
                        Math.sqrt(Math.pow(mMap.getWireX(i) - mX, 2)
                                + Math.pow(mMap.getWireZ(i) - mZ, 2));
                if(distanceToCenter <= mDepth / 2) {
                    double yMinProjection =
                            (mY + mHeight / 2) * mMap.getDepth()
                                    / mMap.getWireZ(i);
                    double yMaxProjection =
                            (mY - mHeight / 2) * mMap.getDepth()
                                    / mMap.getWireZ(i);

                    WiremapSliver sliver =
                            new WiremapSliver(mMap,
                                    i,
                                    (int) (yMaxProjection * mMap.getPixelsPerInch()),
                                    mBaseColor,
                                    (int) (yMinProjection - yMaxProjection)
                                            * mMap.getPixelsPerInch(),
                                    0,
                                    0);
                    sliver.display();
                }
            }
        }
        mMap.getParent().popMatrix();
    }

    public void setSize(int width, int height, int depth) {
        mWidth = width;
        mHeight = height;
        mDepth = depth;
    }

    public void setBorderHeight(int height) {
        mBorderHeight = height;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }
}
