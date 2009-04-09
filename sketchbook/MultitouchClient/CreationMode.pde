public class CreationMode extends GalaxyMode {
    private Star mNewStar = null;

    public CreationMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
    }

    public void display() {
        mCamera.resetScale();
        if(mNewStar == null) {
            super.display();
        } else {
            //TODO creation animation, send data to wiremap client
            super.display();
        }
    } 

    private void saveStar() {
        mObjectManager.add(mNewStar);
    }

    public void cursorPressed(Point cursor) {
        //TODO modify these values after getting user input
        mNewStar = new Star(0,
                "Your Star",
                MASTER_PARENT_ID,
                new Point(
                    width / 2 - mCamera.getCenterX() + mouseX,
                    height/2 - mCamera.getCenterY() + mouseY, 0),
                new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8),
                10,
                10);
    }

    public void disable() {
        mNewStar = null;
    }
}

