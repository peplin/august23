public class InfoMode implements MultitouchModeInterface {
    private Star mSelectedStar = null;
    private PApplet mParent;

    public InfoMode(PApplet parent) {
        mParent = parent;
    }

    public void display() {
        if(mSelectedStar == null) {
            setMode(0);
        } else {
            //TODO what to show here...

        }
    } 

    public void setSelectedStar(Star star) {
        mSelectedStar = star;
    }

    public void cursorPressed(Point cursor) {

    }

    public void cursorDragged(Point cursor) {

    }

    public void disable() {

    }
}
