package wiremap;

import processing.core.*;

public class WiremapGlowingSphere extends WiremapSphere {
    private int mCoreColor;
    private final int COLOR_STEPS = 16;

    public WiremapGlowingSphere(Wiremap map, int x, int y, int z,
            int baseColor, int radius, int coreColor) {
        super(map, x, y, z, baseColor, radius);
        setCoreColor(coreColor);
    }

    public void display() {
        mMap.getParent().pushMatrix();
        final int baseRadius = mRadius;
        final int baseColor = mBaseColor;
        float deltaR = mMap.getParent().red(mCoreColor) - mMap.getParent().red(mBaseColor);
        float deltaG = mMap.getParent().green(mCoreColor) - mMap.getParent().green(mBaseColor);
        float deltaB = mMap.getParent().blue(mCoreColor) - mMap.getParent().blue(mBaseColor);

        for(int i = 0; i < COLOR_STEPS; i++) {
            mRadius = baseRadius - (i * baseRadius / COLOR_STEPS);
            mBaseColor = mMap.getParent().color(mMap.getParent().red(baseColor) + i * deltaR / COLOR_STEPS,
                    mMap.getParent().green(baseColor) + i * deltaG / COLOR_STEPS,
                    mMap.getParent().blue(baseColor) + i * deltaB / COLOR_STEPS);
            displayCenter();
        }
        mRadius = baseRadius;
        mBaseColor = baseColor;
        mMap.getParent().popMatrix();
    }

    public void setCoreColor(int coreColor) {
        mCoreColor = coreColor;
    }
}
