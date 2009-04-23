package twoverse.gui;

import processing.core.PApplet;
import processing.core.PImage;
import twoverse.util.Point;

/**
 * Image-based implementation of Button. <br><br>
 * 
 * Not currently used in Twoverse, so mostly untested.<br><br>
 * 
 * TODO this is based on a circle right now, but all images are going to be
 *       rectangles.<br><br>
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class ImageButton extends Button {
    PImage mImage;
    int mRadius;

    /**
     * Construct an instance of ImageButton.
     * 
     * @param image
     *            image to use for the button
     * @param radius
     *            radius of the circle to check over the image
     */
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

    /**
     * Display the button on the parent's screen if it is visible with normal
     * colors.
     */
    public void display() {
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

    /**
     * Display the button on the parent's screen if it is visible with highlight
     * colors.
     */
    public void highlight() {
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
