//package wiremap;

import processing.core.*;

public class WiremapSphere extends WiremapPositionedShape {
    protected int mRadius;

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapSphere(Wiremap map, int x, int y, int z, color baseColor, 
            int radius) {
        super(map, x, y, z, baseColor);
        setRadius(radius);
    }

    public void display() {
        pushMatrix();
        displayCenter();
        popMatrix();
    }

    protected void displayCenter() {
        for(int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mRadius))
                    && (mMap.getWireX(i) <= (mX + mRadius))) {  
                // find the distance from the wire to the globe's center
                // TODO this uses two coordinate systems...BAD
                float local_hyp = sqrt(sq(mMap.getWireX(i) - mX)
                        + sq(mMap.getWireZ(i) - mZ));           
                // if the wire's xz coord is close enough to the globe's center
                if(local_hyp <= mRadius) {                                                        
                    // find the height of the globe at that point
                    float centerY = sqrt(sq(mRadius) - sq(local_hyp));                      
                    float yMinProjection = (mY + centerY) * mMap.getDepth()
                            / mMap.getWireZ(i);                  
                    float yMaxProjection = (mY - centerY) * mMap.getDepth()
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

    }

    public void setRadius(int radius) {
        mRadius = radius;
    }
}
