import processing.net.*;
import ddf.minim.*;
import ddf.minim.signals.SineWave;

public class CreationMode extends GalaxyMode {
  private final String WIREMAP_SERVER_IP = "192.168.1.6"; //"141.211.4.193";
  private Star mNewStar = null;
  private boolean mSimulationRunning = false;
  private ActiveColorGrabber mColorGrabber;
  private StarSimulation mStarSimulation;
  private Client mClient;
  private String mPartialMessage;
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
    mStarSimulation = new StarSimulation(parent, null);
    mFont = loadFont("promptFont.vlw");
    mClient = new Client(mParent, WIREMAP_SERVER_IP, 1966);
    initializeAudio();
    textFont(mFont);
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
        if(mCurrentPlayer == null || !mCurrentPlayer.isPlaying()
                && mNextPlayTime <= millis()) {
          if(random(1) <= .3) {
            mCurrentPlayer
                = mGrabBagVoiceOverPlayers[
                (int)random(mGrabBagVoiceOverPlayers.length)];
          } else {
            mCurrentPlayer
                = mNarrationVoiceOverPlayers[
                (int)random(mNarrationVoiceOverPlayers.length)];
          }
          mCurrentPlayer.play();
          mNextPlayTime = millis() + random(3000, 6000);
        }
      } 
      else {
        stroke(255);
        fill(255);
        //textMode(SCREEN);
        textAlign(CENTER);
        text("Please enter the airlock", width/2, height/2);
        textAlign(LEFT);
      }
      //TODO display gauge cluster and heartbeat monitor
    }
    listen();
  } 

  private void saveStar() {
    mObjectManager.add(mNewStar);
    mNewStar = null;
  }

    private void listen() {
        if(mClient != null && mClient.available() > 0) {
            String message = mClient.readStringUntil('/');
            if(message != null) {
                message = message.substring(0, message.length() - 1);
                println("DEBUG: Received message from server " + message);
                processMessage(message);
            }
        }
    }  

    private void processMessage(String message) {
        try {
            if(message.equals("start")) {
                mStarSimulation.initialize();
                mSimulationRunning = true;
            } else if(message.equals("done")) {
                saveStar();
                mSimulationRunning = false;
                setMode(0);
            } else {
                String messageParts[] = message.split(" ");
                if(messageParts[0].equals("beat")) {
                    if(messageParts.length == 2) {
                        mNewStar.setFrequency(
                                (double)Float.parseFloat(messageParts[1]));
                        //TODO make sure this is being stored
                        mStarSimulation.setFrequency(
                                (float)mNewStar.getFrequency()); 
                    } else {
                        throw new Exception("Malformed message: " + message);
                    }
                } else if(messageParts[0].equals("state")) {
                    if(messageParts.length == 2) {
                        int endState = Integer.parseInt(messageParts[1]);
                        mNewStar.setState(endState);
                        //TODO make sure this is being stored in DB
                        mStarSimulation.setEndState(endState);
                    } else {
                        throw new Exception("Malformed message: " + message);
                    }
                } else if(messageParts[0].equals("play")) {
                    if(messageParts.length == 3) {
                        if(messageParts[1].equals("seq")) {
                            int player = Integer.parseInt(messageParts[2]);
                            if(player >= 0 && player < mSequenceVoiceOverPlayers.length) {
                                mCurrentPlayer =
                                    mSequenceVoiceOverPlayers[player];
                                mCurrentPlayer.play();
                            } else {
                                throw new Exception("Bad audio index requested: " + message);
                            }
                        }
                    } else {
                        throw new Exception("Malformed message: " + message);
                    }


                } else {
                    throw new Exception("Unrecognized message: " + message);
                }
            }
        } catch(Exception e) {
            println(e);
        }
    }

    private void sendMessage(String message) {
        mClient.write(message + "/");
    }

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
    sendMessage("color " + activeColor);
    popMatrix();
  }

  public void disable() {
    mNewStar = null;
    mSimulationRunning = false;
  }

  private void initializeAudio() {
    mSequenceVoiceOverPlayers = new AudioPlayer[5];
    mGrabBagVoiceOverPlayers = new AudioPlayer[6];
    mNarrationVoiceOverPlayers = new AudioPlayer[9];

    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
      mSequenceVoiceOverPlayers[i]
        = mMinim.loadFile("sequence" + (i + 1) + ".mp3", 2048);
    }

    for(int i = 0; i < mGrabBagVoiceOverPlayers.length; i++) {
      mGrabBagVoiceOverPlayers[i]
        = mMinim.loadFile("grabbag" + (i + 1) + ".mp3", 2048);
    }

    for(int i = 0; i < mNarrationVoiceOverPlayers.length; i++) {
      mNarrationVoiceOverPlayers[i]
        = mMinim.loadFile("narration" + (i + 1) + ".mp3", 2048);
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



