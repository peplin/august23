//package wiremap;

import processing.core.*;

public class WiremapGlowingSphere extends WiremapSphere {
    private color mCoreColor;
    private final int COLOR_STEPS = 16;

    public WiremapGlowingSphere(Wiremap map, int x, int y, int z,
            color baseColor, int radius, color coreColor) {
        super(map, x, y, z, baseColor, radius);
        setCoreColor(coreColor);
    }

    public void display() {
        pushMatrix();
        final int baseRadius = mRadius;
        final color baseColor = mBaseColor;
        float deltaR = red(mCoreColor) - red(mBaseColor);
        float deltaG = green(mCoreColor) - green(mBaseColor);
        float deltaB = blue(mCoreColor) - blue(mBaseColor);

        for(int i = 0; i < COLOR_STEPS; i++) {
            mRadius = baseRadius - (i * baseRadius / COLOR_STEPS);
            mBaseColor = color(red(baseColor) + i * deltaR / COLOR_STEPS,
                    green(baseColor) + i * deltaG / COLOR_STEPS,
                    blue(baseColor) + i * deltaB / COLOR_STEPS);
            displayCenter();
        }
        mRadius = baseRadius;
        mBaseColor = baseColor;
        popMatrix();
    }

    public void setCoreColor(color coreColor) {
        mCoreColor = coreColor;
    }
}
