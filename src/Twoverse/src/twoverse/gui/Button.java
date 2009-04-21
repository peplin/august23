package twoverse.gui;

import processing.core.PApplet;
import twoverse.util.Point;

public abstract class Button {
    private Point mCorner;
    private int mBaseColor;
    private int mHighlightColor;
    private boolean mVisible = false;
    private boolean mLocked = false;
    private String mName;
    protected PApplet mParentApplet;
    
    public Button(PApplet parentApplet, Point center, int baseColor,
            int highlightColor, String name) {
        setCorner(center);
        setParentApplet(parentApplet);
        setBaseColor(baseColor);
        setHighlightColor(highlightColor);
        setName(name);
        setVisible(true);
    }

    private void setParentApplet(PApplet parentApplet) {
        mParentApplet = parentApplet;
    }

    public abstract boolean isPressed(Point cursor);

    boolean overRect(Point cursor, Point corner, int width, int height) {
        if (cursor.getX() >= corner.getX()
                && cursor.getX() <= corner.getX() + width
                && cursor.getY() >= corner.getY()
                && cursor.getY() <= corner.getY() + height) {
            return true;
        } else {
            return false;
        }
    }

    boolean overCircle(Point cursor, Point center, int diameter) {
        double disX = center.getX() - cursor.getX();
        double disY = center.getY() - cursor.getY();
        if (java.lang.Math.sqrt(java.lang.Math.pow(disX, 2.0)
                + java.lang.Math.pow(disY, 2)) < diameter / 2) {
            return true;
        } else {
            return false;
        }
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setCorner(Point corner) {
        mCorner = corner;
    }

    public Point getCorner() {
        return mCorner;
    }

    public void setBaseColor(int baseColor) {
        mBaseColor = baseColor;
    }

    public int getBaseColor() {
        return mBaseColor;
    }

    public void setHighlightColor(int highlightColor) {
        mHighlightColor = highlightColor;
    }

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    public boolean isLocked() {
        return mLocked;
    }
    
    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
