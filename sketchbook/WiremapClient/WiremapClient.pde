import processing.net.*;
import ddf.minim.*;
import ddf.minim.signals.SineWave;

Server mServer;
Client mCurrentClient;

boolean mActivated = false;
boolean mSimulationRunning = false;
boolean mHeartbeatSet = false;

float mHeartbeatFrequency;
color mColor; //TODO set to some default

Minim mMinim;
AudioPlayer mAmbientPlayers[];
AudioPlayer mSequenceVoiceOverPlayers[];
AudioPlayer mCurrentAmbientPlayer;
SineWave mSineWave;
AudioOutput mAudioOutput;
HeartbeatDetector mHeartbeatDetector;

void setup() {
    size(1024, 768, P3D);
    mServer = new Server(this, 1966);
    mMinim = new Minim(this);
//    mHeartbeatDetector = new HeartbeatDetector(this);

    initializeAudio();
}

void draw() {
    background(0);
    if(mActivated) {
        if(!mHeartbeatSet) {
            float currentRate = 1; //mHeartbeatDetector.getCurrentRate();
            if(currentRate > .5 && currentRate< 3) {
                mHeartbeatSet = true;
                //TODO send heartbeat, also store it
                //play lifeline connceted audio
            }
        } else {
            //TODO star formation
        }
        //disconnect from client when finished with message of final state
    } else {
        // do interesting light show stuff, or stay silent
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
    client.write("done");
    //mHeartbeatDetector.resetAverages();
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

    mSequenceVoiceOverPlayers = new AudioPlayer[8];
       mSequenceVoiceOverPlayers[1]
            = mMinim.loadFile("sequenceVo2.mp3");
    mSequenceVoiceOverPlayers[2]
            = mMinim.loadFile("sequenceVo3.mp3");
    mSequenceVoiceOverPlayers[4]
            = mMinim.loadFile("sequenceVo5.mp3");
    mSequenceVoiceOverPlayers[5]
            = mMinim.loadFile("sequenceVo6.mp3");
    mSequenceVoiceOverPlayers[6]
            = mMinim.loadFile("sequenceVo7.mp3");
    
    
    
    /*for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
        mSequenceVoiceOverPlayers[i]
            = mMinim.loadFile("sequenceVo" + i + ".wav");
    }*/
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
