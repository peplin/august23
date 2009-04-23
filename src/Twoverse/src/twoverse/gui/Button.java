/**
 * Twoverse Abstract Button
 * 
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.gui;

import processing.core.PApplet;
import twoverse.util.Point;

/**
 * Abstract parent for all GUI buttons in Twoverse clients.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public abstract class Button {
    private Point mCorner;
    private int mBaseColor;
    private int mHighlightColor;
    private boolean mVisible = false;
    private boolean mLocked = false;
    private String mName;
    protected PApplet mParentApplet;

    /**
     * Construct a new button for the parent applet.
     * 
     * @param parentApplet
     *            applet this button will be displayed in
     * @param center
     *            center coordinate of button
     * @param baseColor
     *            non-highlighted, non-clicked color
     * @param highlightColor
     *            rollover or clicked (& locked) color
     * @param name
     *            textual name of the button (often used as display text)
     */
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

    /**
     * Abstract method to check if a button is pressed based on the provided
     * cursor location.
     * 
     * Not checking mouseX and mouseY directly on purpose - by abstracting away,
     * we can use TUIO cursor if we want.
     * 
     * @param cursor
     *            position of cursor
     * @return true if cursor over button
     */
    public abstract boolean isPressed(Point cursor);

    /**
     * General method to check if a cursor is over a rectangle.
     * 
     * @param cursor
     *            position to evaluate
     * @param corner
     *            top left corner of rectangle
     * @param width
     *            width of rectangle
     * @param height
     *            height of rectangle
     * @return true if cursor over the rectangle
     */
    boolean overRect(Point cursor, Point corner, int width, int height) {
        if(cursor.getX() >= corner.getX()
                && cursor.getX() <= corner.getX() + width
                && cursor.getY() >= corner.getY()
                && cursor.getY() <= corner.getY() + height) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * General method to check if a cursor is over a circle.
     * 
     * @param cursor
     *            position to evaluate
     * @param center
     *            of circle
     * @param diameter
     *            diameter of circle
     * @return true if cursor is over the circle
     */
    boolean overCircle(Point cursor, Point center, int diameter) {
        double disX = center.getX() - cursor.getX();
        double disY = center.getY() - cursor.getY();
        if(java.lang.Math.sqrt(java.lang.Math.pow(disX, 2.0)
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

    /**
     * Set the button locked, meaning it will remain its highlight color until
     * unlocked.
     * 
     * @param locked true to lock the button
     */
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
