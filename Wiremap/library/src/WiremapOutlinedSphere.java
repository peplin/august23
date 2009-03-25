//package wiremap;

import processing.core.*;

public class WiremapOutlinedSphere extends WiremapSphere {
    private int mOutlineThickness;
    private color mOutlineColor;

    public WiremapOutlinedSphere(Wiremap map, int x, int y, int z, int radius,
            color baseColor, int outlineThickness, color outlineColor) {
        super(map, x, y, z, radius, baseColor);
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
                    float yMax = mY + centerY;                                          
                    float yMin = mY - centerY;                                          
                    float yMaxProjection = yMax * mMap.getDepth()
                            / mMap.getWireZ(i);                  
                    float yMinProjection = yMin * mMap.getDepth()
                            / mMap.getWireZ(i);
                    float left = i * mMap.getParent().width
                            / mMap.getWireCount();
                    /* Top Surface
                    ---------------------------------------------------------*/
                    mMap.getParent().pushMatrix();
                    fill(255);
                    float top = (mMap.getParent().height / mMap.getPixelsPerInch()
                            - yMaxProjection) * mMap.getPixelsPerInch();
                    mMap.getParent().rect(left, top, mMap.getPixelsPerWire(),
                            mOutlineThickness);
                    mMap.getParent().popMatrix();

                    /* Bottom Surface
                    ---------------------------------------------------------*/
                    top = (mMap.getParent().height / mMap.getPixelsPerInch()
                            - yMinProjection) * mMap.getPixelsPerInch()
                            - mOutlineThickness;
                    mMap.getParent().rect(left, top, mMap.getPixelsPerWire(),
                            mOutlineThickness);
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
