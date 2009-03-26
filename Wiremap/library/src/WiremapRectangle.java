//package wiremap;

import processing.core.*;

public class WiremapRectangle extends WiremapPositionedShape {
    protected int mWidth;
    protected int mHeight;
    protected int mDepth;
    protected int mBorderHeight;
    protected color mBorderColor;

    private int[][] mCornerPositions = new int[8][3];

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapRectangle(Wiremap map, int x, int y, int z, color baseColor,
            int width, int height, int depth, int borderHeight,
            color borderColor) {
        super(map, x, y, z, baseColor);
        setSize(width, height, depth);
        setBorderHeight(borderHeight);
        setBorderColor(borderColor);
    }

    public void display() {
        pushMatrix();
        for(int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mWidth / 2))
                    && (mMap.getWireX(i) <= (mX + mWidth / 2))) {  
                float distanceToCenter = sqrt(sq(mMap.getWireX(i) - mX)
                        + sq(mMap.getWireZ(i) - mZ));           
                if(distanceToCenter <= mDepth / 2) {                                                        
                    float yMinProjection = (mY + mHeight / 2) * mMap.getDepth()
                            / mMap.getWireZ(i);                  
                    float yMaxProjection = (mY - mHeight / 2) * mMap.getDepth()
                            / mMap.getWireZ(i);

                    WiremapSliver sliver = new WiremapSliver(
                            mMap, i, (int)(yMaxProjection
                                * mMap.getPixelsPerInch()),
                            mBaseColor, (int)(yMinProjection - yMaxProjection)
                                * mMap.getPixelsPerInch(),
                            0, 0);
                    sliver.display();
                }
            }
        }
        popMatrix();
    }

    public void setSize(int width, int height, int depth) {
        mWidth = width;
        mHeight = height;
        mDepth = depth;
    }

    public void setBorderHeight(int height) {
        mBorderHeight = height;
    }

    public void setBorderColor(color borderColor) {
        mBorderColor = borderColor;
    }
}
