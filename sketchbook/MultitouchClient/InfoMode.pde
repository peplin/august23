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
            translate(-50, -50);
            mParent.noStroke();
            mParent.fill((float) mSelectedStar.getColorR(),
                    (float) mSelectedStar.getColorG(),
                    (float) mSelectedStar.getColorB());
            mParent.ellipse(0, 0, (float) mSelectedStar.getRadius() * 20,
                    (float) mSelectedStar.getRadius() * 20);

            for(int i = 2; i < 100; i++) {
                mParent.fill((float) mSelectedStar.getColorR(),
                        (float) mSelectedStar.getColorG(),
                        (float) mSelectedStar.getColorB(),
                        (float) (255.0 / i/2.0));
                mParent.ellipse(0, 0, (float) mSelectedStar.getRadius() * 20 + i,
                        (float) mSelectedStar.getRadius() * 20 + i);
            }
            displayStats();
            popMatrix();
        }
    } 

    private void displayStats() {
        fill(255, 255, 255);
        textMode(SCREEN);
        textFont(mFont);
        text("Born: " + mSelectedStar.getBirthTime(), 10, height - 100);
        String typeString = "Type: ";
        int endState = mSelectedStar.getState();
        if(endState == 0) {
            typeString += "Black Hole";
        } else if(endState == 1) {
            typeString += "Supernova";
        } else {
            typeString += "Pulsar";
        }
        text(typeString, 10, height - 70);
        text("Frequency: " + mSelectedStar.getFrequency(), 10, height - 40);
        String locationString = "Location: ";
        Point starPosition = mSelectedStar.getPosition();
        locationString += starPosition.getX() + ", " + starPosition.getY();
        text(locationString, 10, height - 10);
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
