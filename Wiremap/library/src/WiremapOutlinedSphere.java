//package wiremap;

import processing.core.*;

public class WiremapOutlinedSphere extends WiremapSphere {
    private int mOutlineThickness;
    private color mOutlineColor;

    public WiremapOutlinedSphere(Wiremap map, int x, int y, int z,
            color baseColor, int radius, int outlineThickness,
            color outlineColor) {
        super(map, x, y, z, baseColor, radius);
        setOutlineThickness(outlineThickness);
        setOutlineColor(outlineColor);
    }

    public void display() {
        pushMatrix();
        displayCenter();
        displayOutline();
        popMatrix();
    }

    private void displayOutline() {
        for(int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mRadius))
                    && (mMap.getWireX(i) <= (mX + mRadius))) {  
                // find the distance from the wire to the globe's center
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
                            mOutlineColor, mOutlineThickness, 0, 0);
                    sliver.display();

                    sliver.setStartingHeight((int)(yMinProjection
                            * mMap.getPixelsPerInch()));
                    sliver.display();
                }
            }
        }
    }

    public void setOutlineThickness(int thickness) {
        mOutlineThickness = thickness;
    }

    public void setOutlineColor(color outlineColor) {
        mOutlineColor = outlineColor;
    }
}
