/**
 * Twoverse Rectangular Button
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
        mParentApplet.textFont(mFont, 24);
        mParentApplet.fill(255);
        mParentApplet.text(getName(),
                (float) getCorner().getX(),
                (float) getCorner().getY() - 5);
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
