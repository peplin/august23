//package wiremap;

import processing.core.*;

public class WiremapSphere {
    protected Wiremap mMap;
    protected int mX;
    protected int mY;
    protected int mZ;
    protected int mRadius;
    protected int mBaseColor;

    /**
     * z >= 0, z <= mDepthThickness
     */
    public WiremapSphere(Wiremap map, int x, int y, int z, int radius,
            int baseColor) {
        mMap = map;
        setPosition(x, y, z);
        setRadius(radius);
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

                    /* Top dot
                    ---------------------------------------------------------*/
                    // Fill the globe pixels this color
                    fill(mBaseColor);                                   
                    float left = i * mMap.getParent().width
                            / mMap.getWireCount();
                    float top = (mMap.getParent().height
                            / mMap.getPixelsPerInch() - yMaxProjection)
                            * mMap.getPixelsPerInch(); //  + dot_height;    
                    float height = (yMaxProjection - yMinProjection)
                            * mMap.getPixelsPerInch(); // - (dot_height * 2);
                    mMap.getParent().rect(left, top, mMap.getPixelsPerWire(),
                            height);

                }
            }
        }

    }

    public void setBaseColor(color baseColor) {
        mBaseColor = baseColor;
    }

    public void setPosition(int x, int y, int z) {
        mX = translateProcessingXToWiremapX(x);
        mY = translateProcessingYToWiremapY(y);
        mZ = translateProcessingZToWiremapZ(z);
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }
}
