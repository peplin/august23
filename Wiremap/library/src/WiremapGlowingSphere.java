//package wiremap;

import processing.core.*;

public class WiremapGlowingSphere extends WiremapSphere {
    private color mCoreColor;
    private int mCoreRadius;
    private final int COLOR_STEPS = 4;


    public WiremapGlowingSphere(Wiremap map, int x, int y, int z, int radius,
            color baseColor, int coreRadius, color coreColor) {
        super(map, x, y, z, radius, baseColor);
        setCoreColor(coreColor);
        setCoreRadius(coreRadius);
    }

    public void display() {
        pushMatrix();
        final int baseRadius = mRadius;
        final int baseColor = mBaseColor;
        float deltaR = red(mCoreColor) - red(mBaseColor);
        float deltaG = green(mCoreColor) - green(mBaseColor);
        float deltaB = blue(mCoreColor) - blue(mBaseColor);

        for(int i = 0; i < COLOR_STEPS; i++) {
            mRadius = baseRadius - (i * baseRadius / COLOR_STEPS);
            mBaseColor = color(red(mBaseColor) + i * deltaR / COLOR_STEPS,
                    green(mBaseColor) + i * deltaG / COLOR_STEPS,
                    blue(mBaseColor) + i * deltaB / COLOR_STEPS);
            displayCenter();
        }
        mRadius = baseRadius;
        mBaseColor = baseColor;
        popMatrix();
    }

    public void setCoreColor(color coreColor) {
        mCoreColor = coreColor;
    }

    public void setCoreRadius(int radius) {
        mCoreRadius = radius;
    }

}
