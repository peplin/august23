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
        ArrayList starLinks = mObjectManager.getAllLinks();
        for(int i = 0; i < starLinks.size(); i++) {
            Link link = (Link) starLinks.get(i);
            try {
                Star first
                    = (Star)mObjectManager.getCelestialBody(link.getFirstId());
                Star second
                    = (Star)mObjectManager.getCelestialBody(link.getSecondId());
                beginShape(LINES);
                vertex((float) first.getPosition().getX(),
                        (float) first.getPosition().getY());
                vertex((float) second.getPosition().getX(),
                        (float) second.getPosition().getY());
                endShape();
            } catch(UnhandledCelestialBodyException e) {
                println(e);
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
}
