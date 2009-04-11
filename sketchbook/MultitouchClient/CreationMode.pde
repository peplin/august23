import processing.net.*;
import ddf.minim.*;
import ddf.minim.signals.SineWave;

public class CreationMode extends GalaxyMode {
  private final String WIREMAP_SERVER_IP = "localhost"; //"141.211.4.193";
  private Star mNewStar = null;
  private boolean mSimulationRunning = false;
  private ActiveColorGrabber mColorGrabber;
  private StarSimulation mStarSimulation;
  private Client mClient;
  private PFont mFont;

  private Minim mMinim;
  private AudioPlayer mSequenceVoiceOverPlayers[];
  private AudioPlayer mGrabBagVoiceOverPlayers[];
  private AudioPlayer mNarrationVoiceOverPlayers[];
  private AudioPlayer mCurrentPlayer;
  private float mNextPlayTime;

  public CreationMode(PApplet parent, ObjectManagerClient objectManager,
  Camera camera) {
    super(parent, objectManager, camera);
    mMinim = new Minim(mParent);
    //mColorGrabber = new ActiveColorGrabber(mParent);
    mStarSimulation = new StarSimulation(null, this);
    mFont = loadFont("promptFont.vlw");
    initializeAudio();
    textFont(mFont, 48);
  }

  public void display() {
    if(mNewStar == null) {
      mCamera.resetScale();
      super.display();
    } 
    else {
      if(mSimulationRunning) {
        pushMatrix();
        translate(-mCamera.getCenterX(), -mCamera.getCenterY());
        mStarSimulation.display();
        popMatrix();
        //TODO also select randomly from narrations
        if(mCurrentPlayer == null || !mCurrentPlayer.isPlaying() && mNextPlayTime <= millis()) {
          mCurrentPlayer
            = mGrabBagVoiceOverPlayers[
            (int)random(mGrabBagVoiceOverPlayers.length)];
          mCurrentPlayer.play();
        }
      } 
      else {
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

 /* public void clientEvent() {
    if(mClient != null) {
      String data = mClient.readString();
      if(data != null) {
        println("DEBUG: Received data from client " + data);
        String dataArray[] = data.split(" ");
        //TODO confirm these packets aren't split up very often if ever
        if(dataArray.length >= 1) {
          if(dataArray.length > 1 && dataArray[0].equals("beat")) {
            mNewStar.setFrequency(Integer.parseInt(dataArray[1]));
            if(mSimulationRunning) {
              // mStarSimulation.setFrequency(mNewStar.getFrequency()); 
            }
          }
          if(dataArray[0].equals("start")) {
            mStarSimulation.initialize();
            mSimulationRunning = true;
          }
          if(dataArray.length > 1 && dataArray[0].equals("state")) {
            mNewStar.setState(Integer.parseInt(dataArray[1]));
          }
          if(dataArray[0].equals("done")) {
            saveStar();
            mSimulationRunning = false;
            setMode(0);
          }
        }
      }
    }
  }*/  

  public void cursorPressed(Point cursor) {
    pushMatrix();
    color activeColor = color(255); //mColorGrabber.getActiveColor();
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
    (int)red(activeColor),
    (int)green(activeColor),
    (int)blue(activeColor),
    255,
    1);
    saveStar();
    //mClient = new Client(mParent, WIREMAP_SERVER_IP, 1966);
    //mClient.write("color " + activeColor);
    popMatrix();
  }

  public void disable() {
    //TODO block disabling while client is connected
    mNewStar = null;
    mSimulationRunning = false;
  }

  private void initializeAudio() {
    mSequenceVoiceOverPlayers = new AudioPlayer[5];
    mGrabBagVoiceOverPlayers = new AudioPlayer[6];
    mNarrationVoiceOverPlayers = new AudioPlayer[9];

    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
      mSequenceVoiceOverPlayers[i]
        = mMinim.loadFile("sequence" + (i + 1) + ".mp3");
    }

    for(int i = 0; i < mGrabBagVoiceOverPlayers.length; i++) {
      mGrabBagVoiceOverPlayers[i]
        = mMinim.loadFile("grabbag" + (i + 1) + ".mp3");
    }

    for(int i = 0; i < mNarrationVoiceOverPlayers.length; i++) {
      mNarrationVoiceOverPlayers[i]
        = mMinim.loadFile("narration" + (i + 1) + ".mp3");
    }
  }

  public void finalize() {
    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
      if(mSequenceVoiceOverPlayers[i] != null) {
        mSequenceVoiceOverPlayers[i].close();
      }
    }

    for(int i = 0; i < mGrabBagVoiceOverPlayers.length; i++) {
      if(mGrabBagVoiceOverPlayers[i] != null) {
        mGrabBagVoiceOverPlayers[i].close();
      }
    }

    for(int i = 0; i < mNarrationVoiceOverPlayers.length; i++) {
      if(mNarrationVoiceOverPlayers[i] != null) {
        mNarrationVoiceOverPlayers[i].close();
      }
    }
    mMinim.stop();
  }
}



