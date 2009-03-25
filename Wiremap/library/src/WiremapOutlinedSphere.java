//package wiremap;

import processing.core.*;

public class WiremapOutlinedSphere extends WiremapSphere {
    private int mOutlineThickness;
    private color mOutlineColor;

    public WiremapOutlinedSphere(Wiremap map, int x, int y, int z, int radius,
            int outlineThickness, color outlineColor) {
        super(map, x, y, z, radius);
        setOutlineThickness(outlineThickness);
        setOutlineColor(outlineColor);
    }

    public void display() {

    }

    public void setOutlineThickness(int thickness) {
        mOutlineThickness = thickness;
    }

    public void setOutlineColor(color outlineColor) {
        mOutlineColor = outlineColor;
    }
}
