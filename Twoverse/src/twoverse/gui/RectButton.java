package twoverse.gui;

import processing.core.PApplet;
import processing.core.PFont;
import twoverse.util.Point;

public class RectButton extends Button {
    private int mWidth;
    private int mHeight;
    private PFont mFont;

    public RectButton(PApplet parentApplet, Point center, int baseColor,
            int highlightColor, String name, int width, int height, PFont font) {
        super(parentApplet, center, baseColor, highlightColor, name);
        setWidth(width);
        setHeight(height);
        setFont(font);
    }

    private void setFont(PFont font) {
        mFont = font;
    }

    @Override
    public boolean isPressed(Point cursor) {
        return overRect(cursor, getCorner(), mWidth, mHeight) && isVisible();
    }

    public void display(Point cursor) {
        if(isVisible()) {
            if(isPressed(cursor) || isLocked()) {
                drawHighlight();
            } else {
                drawNormal();
            }
        }
    }
    
    private void drawNormal() {
        mParentApplet.pushMatrix();
        mParentApplet.translate(-mParentApplet.width / 2,
                -mParentApplet.height / 2);
        mParentApplet.stroke(0);
        mParentApplet.fill(getBaseColor());
        mParentApplet.rect((float) getCorner().getX(),
                (float) getCorner().getY(),
                getWidth(),
                getHeight());
        label();
        mParentApplet.popMatrix();
    }

    private void drawHighlight() {
        if(isVisible()) {
            mParentApplet.pushMatrix();
            mParentApplet.translate(-mParentApplet.width / 2,
                    -mParentApplet.height / 2);
            mParentApplet.stroke(0);
            mParentApplet.fill(getHighlightColor());
            mParentApplet.rect((float) getCorner().getX(),
                    (float) getCorner().getY(),
                    getWidth(),
                    getHeight());
            label();
            mParentApplet.popMatrix();
        }
    }
    
    private void label() {
        mParentApplet.textFont(mFont, 12);
        mParentApplet.fill(255);
        mParentApplet.text(getName(),
                (float) getCorner().getX(),
                (float) getCorner().getY());
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getHeight() {
        return mHeight;
    }
}
