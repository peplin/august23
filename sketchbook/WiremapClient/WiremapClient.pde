import processing.net.*;
import ddf.minim.*;
import ddf.minim.signals.SineWave;

Server mServer;
Client mCurrentClient;

boolean mActivated = false;
boolean mSimulationRunning = false;
boolean mHeartbeatSet = false;

float mHeartbeatFrequency;
color mColor; 

Minim mMinim;
AudioPlayer mAmbientPlayers[];
AudioPlayer mSequenceVoiceOverPlayers[];
AudioPlayer mCurrentAmbientPlayer;
boolean mNarrationPlayStatus[];
StarSimulationWire mStarSimulation;
SineWave mSineWave;
Wiremap mWiremap;
AudioOutput mAudioOutput;
HeartbeatDetector mHeartbeatDetector;
WiremapGlowingSphere mGlowingSphere;
int mActivatedTime;
float mNextPlayTime = 0;
float mLastHeartbeatTime = 0;
final float HEARTBEAT_DURATION = 50;
boolean mStarted = false;

boolean goingUp = false;
boolean goingLeft = false;
boolean goingBack = false;

float x = 0;
float y = 0;
float z = 10;

void setup() {
    size(1024, 768, P3D);
    mServer = new Server(this, 1966);
    mMinim = new Minim(this);
    mWiremap = new Wiremap(this, 256, 90, 36, 36, 48, 36.0/27.0, .1875, 4,
            "/home/august/august/sketchbook/wiremap/ManualCalibrator/calibration-round2.txt");
    mStarSimulation = new StarSimulationWire(this, mWiremap);  
    mHeartbeatDetector = new HeartbeatDetector(this);
    
    mGlowingSphere = new WiremapGlowingSphere(
    mWiremap, 500, 300, 20, color(255, 255, 0), 8, 
    color(255, 0, 0)); 
    
    mActivatedTime = millis();

    initializeAudio();
    background(0);
}

void draw() {
    background(0);
    if(mActivated) {
        if(!mHeartbeatSet) {
            float currentRate = mHeartbeatDetector.getCurrentRate();
            if(currentRate > .5 && currentRate< 3) {
                mStarSimulation.setFrequency(currentRate);
                sendMessage("beat " + currentRate);
            }

            if(mActivatedTime + 20000 <= millis()) {
                mHeartbeatSet = true;
                sendMessage("beat " + currentRate);
            }
        } else {
            if(!mStarted) {
                if(mSequenceVoiceOverPlayers[0].isLooping()) {
                    mSequenceVoiceOverPlayers[0].play();
                } 
                while(mSequenceVoiceOverPlayers[0].isPlaying()) {
                    continue;
                }
                mSequenceVoiceOverPlayers[1].loop(0);
                sendMessage("play seq 1");
                delay(3000);
                mSequenceVoiceOverPlayers[2].loop(0);
                sendMessage("play seq 2");
                delay(3000);
                mSequenceVoiceOverPlayers[3].loop(0);
                sendMessage("play seq 3");
                delay(2000);
                mSequenceVoiceOverPlayers[4].loop(0);
                sendMessage("play seq 4");
                mStarted = true;
                mStarSimulation.initialize();
                sendMessage("start");
                sendMessage("state " + mStarSimulation.getEndState());
            } else {
                pushMatrix();
                mStarSimulation.display();
                popMatrix();
                if(mNextPlayTime <= millis()) {
                    int index = mStarSimulation.getStarState() - 1;
                    if(random(1) <= .8 && !mNarrationPlayStatus[index] && mStarSimulation.getStarState() > 0) {
                        sendMessage("play nar " + index);
                        mNarrationPlayStatus[index] = true;
                    }
                    mNextPlayTime = millis() + random(5000, 8000);
                }
		    if(!mCurrentAmbientPlayer.isLooping()) {
			mCurrentAmbientPlayer = mAmbientPlayers[(int)random(mAmbientPlayers.length)];
			mCurrentAmbientPlayer.loop(0);
		    }
		if(mAudioOutput.isEnabled(mSineWave)) {
			if(millis() >= mLastHeartbeatTime + HEARTBEAT_DURATION) {
				mAudioOutput.disableSignal(mSineWave);
			}
		} else {
			if(millis() >= mLastHeartbeatTime + (1 / mHeartbeatFrequency * 1000)) {
				mAudioOutput.enableSignal(mSineWave);
			}

		}
            }
        }

        if(mStarted && mStarSimulation.isEnded()) {
            sendMessage("done");
            mActivated = false;
            mHeartbeatSet = false;
            mStarted = false;
            mCurrentClient = null;
            resetPlayStatus();
            mCurrentAmbientPlayer.play();
        }
    }

    listen();
}

void listen() {
    Client client = mServer.available();
    if(client != null) {
        String message = client.readStringUntil('/');
        if(message != null) {
            message = message.substring(0, message.length() - 1);
            println("DEBUG: Received message from client " + message);
            processMessage(message);
        }
    }
}

void processMessage(String message) {
    try {
        String messageParts[] = message.split(" ");
        if(messageParts[0].equals("color")) {
            if(messageParts.length == 2) {
                mActivated = true;
                mActivatedTime = millis();
                mHeartbeatSet = false;
                mSimulationRunning = false;
                mHeartbeatDetector.resetAverages();
                mSequenceVoiceOverPlayers[0].loop(2);
		mStarSimulation.setColor(Integer.parseInt(messageParts[1]));
            } else {
                throw new Exception("Malformed message: " + message);
            }
        } else {
            throw new Exception("Unrecognized message: " + message);
        }
    } catch(Exception e) {
        println(e);
    }
}

void serverEvent(Server server, Client client) {
    if(mCurrentClient == null) {
        mCurrentClient = client;
    } else {
        client.write("no/");

    }
}

void sendMessage(String message) {
    if(mCurrentClient != null) {
        mCurrentClient.write(message + "/");
    } else {
        println("DEBUG: Unable to write to null server");
    }
}

void initializeAudio() {
    mAudioOutput = mMinim.getLineOut(Minim.MONO);
    mSineWave = new SineWave(440, .5, mAudioOutput.sampleRate());
    mSineWave.portamento(200);
    mAudioOutput.addSignal(mSineWave);
    mAudioOutput.disableSignal(mSineWave);

    mAmbientPlayers = new AudioPlayer[7];
    for(int i = 0; i < 7; i++) {
        mAmbientPlayers[i] =
            mMinim.loadFile("ambient" + (i + 1) + ".mp3", 2048);
    }
    mCurrentAmbientPlayer = mAmbientPlayers[0];

    mSequenceVoiceOverPlayers = new AudioPlayer[5];
    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
        mSequenceVoiceOverPlayers[i]
            = mMinim.loadFile("sequence" + (i + 1) + ".mp3", 2048);
    }

    //TODO watch this number....will have to change with new audio files
    mNarrationPlayStatus = new boolean[9];
    resetPlayStatus();
}

void resetPlayStatus() {
    for(int i =0; i < mNarrationPlayStatus.length; i++) {
        mNarrationPlayStatus[i] = false;
    }
}

void stop() {
    for(int i = 0; i < mAmbientPlayers.length; i++) {
        if(mAmbientPlayers[i] != null) {
            mAmbientPlayers[i].close();
        }
    }

    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
        if(mSequenceVoiceOverPlayers[i] != null) {
            mSequenceVoiceOverPlayers[i].close();
        }
    }
    mMinim.stop();
    super.stop();
}
