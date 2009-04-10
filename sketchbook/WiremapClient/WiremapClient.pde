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
StarSimulation mStarSimulation;
SineWave mSineWave;
Wiremap mWiremap;
AudioOutput mAudioOutput;
HeartbeatDetector mHeartbeatDetector;
WiremapGlowingSphere mGlowingSphere;
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
    mWiremap = new Wiremap(this, 256, 90, 36, 48, 36.0/9.0, .1875, 2, 
            "depths.txt");
    mStarSimulation = new StarSimulation(mWiremap, this);  
    mHeartbeatDetector = new HeartbeatDetector(this);
    
    mGlowingSphere = new WiremapGlowingSphere(
    mWiremap, 500, 300, 20, color(255, 255, 0), 8, 
    color(255, 0, 0)); 

    initializeAudio();
}

void draw() {
    background(0);
    if(mActivated) {
        if(!mHeartbeatSet) {
            float currentRate = mHeartbeatDetector.getCurrentRate();
            if(currentRate > .5 && currentRate< 3) {
                mHeartbeatSet = true;
                mStarSimulation.setFrequency(currentRate);
                mCurrentClient.write("beat " + currentRate);
            }
        } else {
            if(mStarted) {
                mSequenceVoiceOverPlayers[0].pause();
                mSequenceVoiceOverPlayers[1].play();
                mCurrentClient.write("play seq 2");
                delay(3000);
                mSequenceVoiceOverPlayers[2].play();
                mCurrentClient.write("play seq 3");
                delay(3000);
                mSequenceVoiceOverPlayers[3].play();
                mCurrentClient.write("play seq 4");
                delay(2000);
                mSequenceVoiceOverPlayers[4].play();
                mCurrentClient.write("play seq 5");
                mStarted = true;
            } else {
                pushMatrix();
                scale(1.5);
                mStarSimulation.display();
                popMatrix();
            }
        }

        if(mStarSimulation.isEnded()) {
            client.write("done");
            mActivated = false;
            mHeartbeatSet = false;
            mStarted = false;
        }
    } else {
        // do interesting light show stuff, or stay silent
        background(0);
        if(goingBack) {
            z += 1;
            if(z >= 35) {
            goingBack = false; 
            }
        } 
        else {
            z -= 1;
            if(z <= 1) {
            goingBack = true; 
            }
        }

        if(goingUp) {
            y -= 10;
            if(y <= 20) {
            goingUp = false; 
            }
        } 
        else {
            y += 10;
            if(y >= height - 40) {
            goingUp = true; 
            }
        }

        if(goingLeft) {
            x -= 10;
            if(x <= 20) {
            goingLeft = false; 
            }
        } 
        else {
            x += 10;
            if(x >= width - 60) {
                goingLeft = true;
            }
        }    
        glowingSphere.setPosition((int)x, (int)y, (int)z);
        glowingSphere.display();
    }

    listen();

    if(!mCurrentAmbientPlayer.isPlaying()) {
        mCurrentAmbientPlayer = mAmbientPlayers[(int)random(mAmbientPlayers.length)];
        mCurrentAmbientPlayer.play();
    }
}

void listen() {
    Client client = mServer.available();
    if(client != null) {
        String data = client.readString();
        if(data != null) {
            println("client sent: " + data);
        }
    }
}

void serverEvent(Server server, Client client) {
    mActivated = true;
    mCurrentClient = client;
    mHeartbeatSet = false;
    mSimulationRunning = false;
    client.write("start");
    mSequenceVoiceOverPlayers[0].loop(2);
    mStarSimulation.initialie();
    mHeartbeatDetector.resetAverages();
}

void initializeAudio() {
    mAudioOutput = mMinim.getLineOut(Minim.MONO);
    mSineWave = new SineWave(440, .5, mAudioOutput.sampleRate());
    mSineWave.portamento(200);
    mAudioOutput.addSignal(mSineWave);
    mAudioOutput.disableSignal(mSineWave);

    mAmbientPlayers = new AudioPlayer[7];
    for(int i = 0; i < 7; i++) {
        mAmbientPlayers[i] = mMinim.loadFile("ambient" + (i + 1) + ".mp3");
    }
    mCurrentAmbientPlayer = mAmbientPlayers[0];

    mSequenceVoiceOverPlayers = new AudioPlayer[5];
    for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
        mSequenceVoiceOverPlayers[i]
            = mMinim.loadFile("sequence" + (i + 1) + ".mp3");
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
