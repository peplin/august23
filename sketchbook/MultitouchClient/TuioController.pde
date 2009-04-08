
public class TuioController {
    private PApplet mParent;
    private TuioClient mClient;
    private boolean fingerDown = false;
    private boolean fingerMoved = true;
    private float primaryFingerId = -1;
    private float previousFingerX = -1;
    private float previousFingerY = -1;
    private float fingerX = -1;
    private float fingerY = -1;

    TuioController(PApplet parent) {
        mParent = parent;
        mParent.registerDraw(this);
        mClient = new TuioClient(parent);
    }

    void draw() {
        if(DEBUG) {
            // Draw each cursor to the screen for debugging
            TuioCursor[] tuioCursorList = mClient.getTuioCursors();
            for(int i = 0; i < tuioCursorList.length; i++) {
                ellipse(tuioCursorList[i].getScreenX(width),
                        tuioCursorList[i].getScreenY(height),
                        10,
                        10);
            }
        }
    }

    // called when a cursor is added to the scene
    // Keep track of one primary finger and use that for movement
    void addTuioCursor(TuioCursor tcur) {
        if(!fingerDown) { // if we have no others, this the new primary
            primaryFingerId = tcur.getFingerID();
        
            fingerDown = true;
            fingerX = previousFingerX = tcur.getX() * screen.width;
            fingerY = previousFingerY = tcur.getY() * screen.width;
            mParent.cursorPressed(fingerX, fingerY);
        }
    }

    // called when a cursor is moved
    void updateTuioCursor (TuioCursor tcur) {
        if(tcur.getFingerID() == primaryFingerId) {
            fingerMoved = true;
            previousFingerX = fingerX;
            previousFingerY = fingerY;
            fingerX = tcur.getX() * screen.width;
            fingerY = tcur.getY() * screen.height;
            mParent.cursorDragged(fingerX, fingerY);
        }
    }

    // called when a cursor is removed from the scene
    void removeTuioCursor(TuioCursor tcur) {
        fingerDown = false;
        fingerMoved = false;
    }
}
