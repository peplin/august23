/**
 ** Multitouch Interface for Twoverse Multitouch Client
 **
 ** by Christopher Peplin (chris.peplin@rhubarbtech.com)
 ** for August 23, 1966 (GROCS Project Group)
 ** University of Michigan, 2009
 **
 ** http://august231966.com
 ** http://www.dc.umich.edu/grocs
 **
 ** Copyright 2009 Christopher Peplin 
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at 
 ** http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 ** See the License for the specific language governing permissions and
 ** limitations under the License. 
 */

import processing.core.*;
import twoverse.gui.RectButton;

/**
 * The MultitouchInterface class manages the GUI elements on the screen for the
 * Twoverse Multitouch Client. It stores all of the buttons, draws them to the
 * screen and checks for button presses. <br>
 * <br>
 * 
 * The buttons are draw down the right hand side of the screen, and are sized
 * equally based on a few constants defined in the class.<br>
 * <br>
 * 
 * Buttons: <br>
 * Zoom In<br>
 * Zoom Out <br>
 * Create <br>
 * Connect <br>
 * Galaxy View<br>
 * <br>
 * 
 * The interface also supports: zooming in/out with the mouse wheel. moving the
 * viewpoint when the mouse is dragged drawing titles to the screen.<br>
 * <br>
 * 
 * At the moment, this interface provides the most functionality among the
 * clients because it includes the "Create" button.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class MultitouchInterface {
    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 75;
    private final int BUTTON_PADDING = 25;
    private final float ZOOM_STEP = .1;
    private final color BUTTON_BASE_COLOR = color(110, 166, 217);
    private final color BUTTON_HIGHLIGHT_COLOR = color(129, 194, 255);
    private RectButton mZoomInButton;
    private RectButton mZoomOutButton;
    private RectButton mCreateButton;
    private RectButton mConnectButton;
    private RectButton mGalaxyButton;
    private PApplet mParent;
    private Point mPreviousCursor;
    private PFont mFont;

    public MultitouchInterface(PApplet parent) {
        mParent = parent;
        mParent.registerMouseEvent(this);
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                mouseWheel(evt.getWheelRotation());
            }
        });

        mFont = loadFont("buttonFont.vlw");

        // TODO make buttons pretty
        mZoomInButton =
                new RectButton(parent,
                        new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                                BUTTON_PADDING + BUTTON_HEIGHT,
                                0),
                        BUTTON_BASE_COLOR,
                        BUTTON_HIGHLIGHT_COLOR,
                        "Zoom In",
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        mFont);

        mZoomOutButton =
                new RectButton(parent,
                        new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                                2 * (BUTTON_PADDING + BUTTON_HEIGHT),
                                0),
                        BUTTON_BASE_COLOR,
                        BUTTON_HIGHLIGHT_COLOR,
                        "Zoom Out",
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        mFont);

        mCreateButton =
                new RectButton(parent,
                        new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                                3 * (BUTTON_PADDING + BUTTON_HEIGHT),
                                0),
                        BUTTON_BASE_COLOR,
                        BUTTON_HIGHLIGHT_COLOR,
                        "Create",
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        mFont);

        mConnectButton =
                new RectButton(parent,
                        new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                                4 * (BUTTON_PADDING + BUTTON_HEIGHT),
                                0),
                        BUTTON_BASE_COLOR,
                        BUTTON_HIGHLIGHT_COLOR,
                        "Connect",
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        mFont);

        mGalaxyButton =
                new RectButton(parent,
                        new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                                5 * (BUTTON_PADDING + BUTTON_HEIGHT),
                                0),
                        BUTTON_BASE_COLOR,
                        BUTTON_HIGHLIGHT_COLOR,
                        "Galaxy",
                        BUTTON_WIDTH,
                        BUTTON_HEIGHT,
                        mFont);
        mGalaxyButton.setLocked(true);
    }

    void mouseWheel(int delta) {
        mCamera.zoom(-.05 * delta);
    }

    public void mouseEvent(MouseEvent e) {
        Point cursor = new Point(e.getX(), e.getY(), 0);

        switch (e.getID()) {
        case MouseEvent.MOUSE_CLICKED:
            if(checkButtons(cursor)) {
                break;
            }
            getMode().cursorPressed(cursor);
            break;
        case MouseEvent.MOUSE_DRAGGED:
            getMode().cursorDragged(cursor);
            break;
        }
        mPreviousCursor = cursor;
    }

    /** returns true if a button was pressed and this event was handled */
    private boolean checkButtons(Point cursor) {
        if(getMode().canDisable()) {
            if(mZoomInButton.isPressed(cursor)) {
                mCamera.zoom(ZOOM_STEP);
            } else if(mZoomOutButton.isPressed(cursor)) {
                mCamera.zoom(-ZOOM_STEP);
            } else if(mCreateButton.isPressed(cursor)) {
                if(mCreateButton.isLocked()) {
                    mCreateButton.setLocked(false);
                    mGalaxyButton.setLocked(true);
                    setMode(0);
                } else {
                    mCreateButton.setLocked(true);
                    mConnectButton.setLocked(false);
                    mGalaxyButton.setLocked(false);
                    setMode(2);
                    mCamera.resetScale();
                }
            } else if(mConnectButton.isPressed(cursor)) {
                if(mConnectButton.isLocked()) {
                    mConnectButton.setLocked(false);
                    mGalaxyButton.setLocked(true);
                    setMode(0);
                } else {
                    mConnectButton.setLocked(true);
                    mCreateButton.setLocked(false);
                    mGalaxyButton.setLocked(false);
                    setMode(3);
                }
            } else if(mGalaxyButton.isPressed(cursor)) {
                mConnectButton.setLocked(false);
                mCreateButton.setLocked(false);
                mGalaxyButton.setLocked(true);
                setMode(0);
            } else {
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    private void drawTitle() {
        pushMatrix();
        fill(255, 255, 255);
        textMode(SCREEN);
        textAlign(RIGHT);
        textFont(mFont);
        text("August 23, 1966", width - 10, height - 10);
        textAlign(LEFT);
        popMatrix();
    }

    public void display() {
        pushMatrix();
        hint(DISABLE_DEPTH_TEST);
        Point cursor = new Point(mouseX, mouseY, 0);
        mZoomInButton.display(cursor);
        mZoomOutButton.display(cursor);
        mCreateButton.display(cursor);
        mConnectButton.display(cursor);
        mGalaxyButton.display(cursor);
        hint(ENABLE_DEPTH_TEST);
        popMatrix();

        drawTitle();
    }
}
