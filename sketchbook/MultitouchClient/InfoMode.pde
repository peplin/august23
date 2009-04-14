public class InfoMode implements MultitouchModeInterface {
    protected static final int MASTER_PARENT_ID = 1;
    private Star mSelectedStar = null;
    private PApplet mParent;
    private PFont mFont;

    public InfoMode(PApplet parent) {
        mParent = parent;
        mFont = loadFont("buttonFont.vlw");
    }

    public void display() {
        if(mSelectedStar == null) {
            setMode(0);
        } else {
            pushMatrix();
            translate(-width/2 + 50, -height/2);
            try {
                mSelectedStar.getAsApplet(mParent).display();
            } catch(TwoDimensionalException e) {
                println(e);
            }
            displayStats();
            popMatrix();
        }
    } 

    private void displayStats() {
        fill(255, 255, 255);
        textMode(SCREEN);
        textFont(mFont);
        text("Born: " + mSelectedStar.getBirthTime(), 10, height - 10);
        String typeString = "Type: ";
        int endState = mSelectedStar.getState();
        if(endState == 0) {
            typeString += "Black Hole";
        } else if(endState == 1) {
            typeString += "Supernova";
        } else {
            typeString += "Pulsar";
        }
        text(typeString, 100, height - 10);
        text("Frequency: " + mSelectedStar.getFrequency(), 200, height - 10);
        String locationString = "Location: ";
        Point starPosition = mSelectedStar.getPosition();
        locationString += starPosition.getX() + ", " + starPosition.getY();
        text(locationString, 300, height - 10);
    }

    public void setSelectedStar(Star star) {
        mSelectedStar = star;
    }

    public void cursorPressed(Point cursor) {

    }

    public void cursorDragged(Point cursor) {

    }

    public void disable() {
        mSelectedStar = null;
    }
}
