import processing.net.*;

public class CreationMode extends GalaxyMode {
    private final String WIREMAP_SERVER_IP = "127.0.0.1";
    private Star mNewStar = null;
    private boolean mSimulationRunning = false;
    private ActiveColorGrabber mColorGrabber;
    private Client mClient;
    private PFont mFont;

    public CreationMode(PApplet parent, ObjectManagerClient objectManager,
            Camera camera) {
        super(parent, objectManager, camera);
        mColorGrabber = new ActiveColorGrabber(mParent);
        mFont = loadFont("promptFont.vlw");
        textFont(mFont, 48);
    }

    public void display() {
        if(mNewStar == null) {
            mCamera.resetScale();
            super.display();
        } else {
            if(mSimulationRunning) {
                //TODO display simulation
            } else {
                text("Please enter the airlock", 0, 0);
                //TODO display static particle field
            }
            //TODO display gauge cluster
        }
    } 

    private void saveStar() {
        mObjectManager.add(mNewStar);
        mNewStar = null;
    }

    public void disconnectEvent() {
        saveStar();
        mSimulationRunning = false;
        setMode(0);
    }

    public void clientEvent() {
        String data[] = mClient.readString().split(" ");
        //TODO confirm these packets aren't split up very often if ever
        if(data.length > 1 && data[0].equals("beat")) {
            mNewStar.setHeartbeat(Integer.parseInt(data[1]));
        }
        if(data[0].equals("start")) {
            mSimulationRunning = true;
        }
        if(data.length > 1 && data[0].equals("state")) {
            mNewStar.setState(Integer.parseInt(data[1]));
        }
    }

    public void cursorPressed(Point cursor) {
        pushMatrix();
        //TODO this may be too dark - perhaps just grab average
        color activeColor = mColorGrabber.getActiveColor();
        mNewStar = new Star(0,
                "Your Star",
                MASTER_PARENT_ID,
                new Point(
                    modelX(mouseX - mCamera.getCenterX(), 
                        mouseY - mCamera.getCenterY(), 0),
                    modelY(mouseX - mCamera.getCenterX(),
                        mouseY - mCamera.getCenterY(), 0), 0),
                new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8),
                10,
                10,
                red(activeColor), green(activeColor) blue(activeColor),
                255,
                1);
        mClient = new Client(mParent, WIREMAP_SERVER_IP, 1966);
        mClient.write("color " + activeColor);
        popMatrix();
    }

    public void disable() {
        //TODO block disabling while client is connected
        mNewStar = null;
    }
}
