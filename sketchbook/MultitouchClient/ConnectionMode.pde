public class ConnectionMode extends GalaxyMode {
    private ArrayList mStarLinks;
    private StarLink mOpenLink = null;
    public ConnectionMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
        mStarLinks = new ArrayList();
    }

    public void display() {
        super.display();
        pushMatrix();
        stroke(255);
        noFill();
        translate(-width/2, -height/2);
        for(int i = 0; i < mStarLinks.size(); i++) {
            StarLink link = (StarLink) mStarLinks.get(i);
            beginShape(LINES);
            vertex((float)link.getFirst().getPosition().getX(),
                    (float)link.getFirst().getPosition().getY());
            vertex((float)link.getSecond().getPosition().getX(),
                    (float)link.getSecond().getPosition().getY());
            endShape();
        }
        popMatrix();
    }

    public void cursorPressed(Point cursor) {
        //TODO figure out where to store these in DB - mod obj manager
        Star selectedStar = checkStars(cursor);
        if(selectedStar != null) {
            if(mOpenLink != null && selectedStar != mOpenLink.getFirst()) {
                mOpenLink.setSecond(selectedStar);
                mStarLinks.add(mOpenLink);
                mOpenLink = null;
            } else {
                mOpenLink = new StarLink(selectedStar);
            }
        }
    }
}
