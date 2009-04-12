import twoverse.object.Link;

public class ConnectionMode extends GalaxyMode {
    private Link mOpenLink = null;
    public ConnectionMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
    }

    public void display() {
        super.display();
        mCamera.resetScale();
        pushMatrix();
        stroke(255);
        noFill();
        translate(-width/2, -height/2);
        if(mOpenLink != null) {
            try {
                Star first
                    = (Star)mObjectManager.getCelestialBody(mOpenLink.getFirstId());
                beginShape(LINES);
                vertex((float) first.getPosition().getX(),
                        (float) first.getPosition().getY());
                vertex(-mCamera.getCenterX() + mouseX + width/2,
                        -mCamera.getCenterY() + mouseY + height/2);
                endShape();
            } catch(UnhandledCelestialBodyException e) {

            }
        }
        popMatrix();
    }

    public void cursorPressed(Point cursor) {
        Star selectedStar = checkStars(cursor);
        if(selectedStar != null) {
            if(mOpenLink != null && selectedStar.getId()
                    != mOpenLink.getFirstId()) {
                mOpenLink.setSecond(selectedStar);
                mObjectManager.add(mOpenLink);
                mOpenLink = null;
            } else {
                mOpenLink = new Link(selectedStar);
            }
        }
    }

    public void disable() {
        mOpenLink = null;
    }
}
