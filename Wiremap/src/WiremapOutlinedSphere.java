package wiremap;

import processing.core.*;

public class WiremapOutlinedSphere extends WiremapSphere {
    private int mOutlineThickness;
    private int mOutlineColor;

    public WiremapOutlinedSphere(Wiremap map, int x, int y, int z,
            int baseColor, int radius, int outlineThickness, int outlineColor) {
        super(map, x, y, z, baseColor, radius);
        setOutlineThickness(outlineThickness);
        setOutlineColor(outlineColor);
    }

    public void display() {
        mMap.getParent().pushMatrix();
        displayCenter();
        displayOutline();
        mMap.getParent().popMatrix();
    }

    private void displayOutline() {
        for (int i = 0; i < mMap.getWireCount(); i++) {
            // if a wire's x coord is close enough to the globe's center
            if((mMap.getWireX(i) >= (mX - mRadius))
                    && (mMap.getWireX(i) <= (mX + mRadius))) {
                // find the distance from the wire to the globe's center
                double local_hyp =
                        Math.sqrt(Math.pow(mMap.getWireX(i) - mX, 2)
                                + Math.pow(mMap.getWireZ(i) - mZ, 2));
                // if the wire's xz coord is close enough to the globe's center
                if(local_hyp <= mRadius) {
                    // find the height of the globe at that point
                    double centerY =
                            Math.sqrt(Math.pow(mRadius, 2)
                                    - Math.pow(local_hyp, 2));
                    double yMinProjection =
                            (mY + centerY) * mMap.getDepth() / mMap.getWireZ(i);
                    double yMaxProjection =
                            (mY - centerY) * mMap.getDepth() / mMap.getWireZ(i);

                    WiremapSliver sliver =
                            new WiremapSliver(mMap,
                                    i,
                                    (int) (yMaxProjection * mMap.getPixelsPerInch()),
                                    mOutlineColor,
                                    mOutlineThickness,
                                    0,
                                    0);
                    sliver.display();

                    sliver.setStartingHeight((int) (yMinProjection * mMap.getPixelsPerInch()));
                    sliver.display();
                }
            }
        }
    }

    public void setOutlineThickness(int thickness) {
        mOutlineThickness = thickness;
    }

    public void setOutlineColor(int outlineColor) {
        mOutlineColor = outlineColor;
    }
}
