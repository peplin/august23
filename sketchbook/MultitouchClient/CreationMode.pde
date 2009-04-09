import processing.net.*;

public class CreationMode extends GalaxyMode {
    private Star mNewStar = null;
    private boolean mReceivedAllData = false;
    private Client mClient;

    public CreationMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
        //TODO replace this IP with that of the MT computer
    }

    public void display() {
        if(mNewStar == null) {
            mCamera.resetScale();
            super.display();
        } else {
            if(mReceivedAllData) {
                //TODO display simulation
            } else {
                //TODO display static particle field
                // share gauge cluster with InfoMode
            }
        }
    } 

    private void saveStar() {
        mObjectManager.add(mNewStar);
    }

    public void disconnectEvent() {
        int endState = mClient.read();
        //TODO update star w/ end state
        saveStar();
        mNewStar = null;
        setMode(0);
    }

    public void clientEvent() {
        String data[] = mClient.readString().split("/");
        //TODO confirm these packets aren't split up very often if ever
        if(data[0].equals("color")) {
            /*mNewStar.setColor(color(Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3])));
                        */
            if(data.length == 6) {
                if(data[4].equals("beat")) {
                    //mNewStar.setHeartbeat(Integer.parseInt(data[5]));
                }
            }
        } else if(data[0].equals("beat")) {
            //mNewStar.setHeartbeat(Integer.parseInt(data[1]));
        }
        //TODO if recv all, set flag to true - or can we check the star
        //directly?
    }

    public void cursorPressed(Point cursor) {
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
        mClient = new Client(mParent, "141.213.30.171", 1966);
    }

    public void disable() {
        //TODO block disabling while client is connected
        mNewStar = null;
    }
}
