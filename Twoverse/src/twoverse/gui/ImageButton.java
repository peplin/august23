package twoverse.gui;

import processing.core.PApplet;
import processing.core.PImage;
import twoverse.util.Point;

public class ImageButton extends Button {
    PImage mImage;
    int mRadius;

    public ImageButton(PApplet parentApplet, Point center, int baseColor,
            int highlightColor, String name, PImage image, int radius) {
        super(parentApplet, center, baseColor, highlightColor, name);
        setImage(image);
        setRadius(radius);
    }

    @Override
    public boolean isPressed(Point cursor) {
        return overCircle(cursor, getCorner(), mRadius);
    }

    void display() {
        if(isVisible()) {
            mParentApplet.pushMatrix();
            mParentApplet.noStroke();
            mParentApplet.noFill();
            mParentApplet.tint(255, 100);
            mParentApplet.image(getImage(),
                    (float) (getCorner().getX() - getRadius() / 2),
                    (float) (getCorner().getY() - getRadius() / 2));
            mParentApplet.popMatrix();
        }
    }

    void highlight() {
        if(isVisible()) {
            mParentApplet.pushMatrix();
            mParentApplet.noStroke();
            mParentApplet.noFill();
            mParentApplet.tint(255, 40000);
            mParentApplet.image(getImage(),
                    (float) (getCorner().getX() - getRadius() / 2),
                    (float) (getCorner().getY() - getRadius() / 2));
            mParentApplet.tint(255, 100);
            mParentApplet.popMatrix();
        }
    }

    public void setImage(PImage image) {
        mImage = image;
    }

    public PImage getImage() {
        return mImage;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public int getRadius() {
        return mRadius;
    }

}
