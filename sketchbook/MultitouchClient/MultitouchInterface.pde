import processing.core.*;
import twoverse.gui.RectButton;

public class MultitouchInterface {
    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 75;
    private final int BUTTON_PADDING = 25;
    private final float ZOOM_STEP = .1;
    private RectButton mZoomInButton;
    private RectButton mZoomOutButton;
    private RectButton mCreateButton;
    private RectButton mConnectButton;
    private RectButton mGalaxyButton;
    private PApplet mParent;
    private Point mPreviousCursor;

    public MultitouchInterface(PApplet parent) {
        mParent = parent;
        mParent.registerMouseEvent(this);
        addMouseWheelListener(new java.awt.event.MouseWheelListener() { 
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) { 
            mouseWheel(evt.getWheelRotation());
        }}); 

        PFont font = loadFont("buttonFont.vlw");

        // TODO make buttons pretty
        mZoomInButton = new RectButton(parent,
                new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                    BUTTON_PADDING + BUTTON_HEIGHT, 0),
                color(255, 0, 0),
                color(255, 255, 255),
                "Zoom In",
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                font);

        mZoomOutButton = new RectButton(parent,
                new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                    2 * (BUTTON_PADDING + BUTTON_HEIGHT), 0),
                color(255, 0, 0),
                color(255, 255, 255),
                "Zoom Out",
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                font);

        mCreateButton = new RectButton(parent,
                new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                    3 * (BUTTON_PADDING + BUTTON_HEIGHT), 0),
                color(255, 0, 0),
                color(255, 255, 255),
                "Create",
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                font);

        mConnectButton = new RectButton(parent,
                new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                    4 * (BUTTON_PADDING + BUTTON_HEIGHT), 0),
                color(255, 0, 0),
                color(255, 255, 255),
                "Connect",
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                font);

        mGalaxyButton = new RectButton(parent,
                new Point(width - BUTTON_WIDTH - BUTTON_PADDING,
                    5 * (BUTTON_PADDING + BUTTON_HEIGHT), 0),
                color(255, 0, 0),
                color(255, 255, 255),
                "Galaxy View",
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                font);
        mGalaxyButton.setLocked(true);
    }

    void mouseWheel(int delta) {
        mCamera.zoom(-.05 * delta);
    }

    public void mouseEvent(MouseEvent e) {
        Point cursor = new Point(e.getX(), e.getY(), 0);

        switch(e.getID()) {
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
        return true;
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
    }
}
