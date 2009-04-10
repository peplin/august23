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

void setup() {
    size(1024, 768, P3D);
    mServer = new Server(this, 1966);
    mMinim = new Minim(this);

    initializeAudio();
}

void draw() {
    if(mActivated) {
        if(!mHeartbeatSet) {
        } else {
            //TODO star formation
        }
    } else {
        // do interesting light show stuff, or stay silent
        //disconnect from client when finished with message of final state
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
            //TODO if we get heartbeat, play lifelineconnected
        }
    }
}

void serverEvent(Server server, Client client) {
    mActivated = true;
    mCurrentClient = client;
    mHeartbeatSet = false;
    mSimulationRunning = false;
}

void initializeAudio() {
    mAudioOutput = mMinim.getLineOut(Minim.MONO);
    mSineWave = new SineWave(440, .5, mAudioOutput.sampleRate());
    mSineWave.portamento(200);
    mAudioOutput.addSignal(mSineWave);
    mAudioOutput.disableSignal(mSineWave);

    mAmbientPlayers = new AudioPlayer[7];
    for(int i = 0; i < 1; i++) {
        mAmbientPlayers[i] = mMinim.loadFile("ambient" + (i + 1) + ".wav", 2048);
    }
    mCurrentAmbientPlayer = mAmbientPlayers[0];

    //TODO this number goes up when we record the rest tomorrow
    mSequenceVoiceOverPlayers = new AudioPlayer[8];
    /*for(int i = 0; i < mSequenceVoiceOverPlayers.length; i++) {
        mSequenceVoiceOverPlayers[i]
            = mMinim.loadFile("sequenceVo" + i + ".mp3", 2048);
    }
    mSequenceVoiceOverPlayers[2] = mMinim.loadFile("sequenceVo3.wav", 2048);
    mSequenceVoiceOverPlayers[3] = mMinim.loadFile("sequenceVo4.wav", 2048);
    mSequenceVoiceOverPlayers[5] = mMinim.loadFile("sequenceVo6.wav", 2048);
    mSequenceVoiceOverPlayers[6] = mMinim.loadFile("sequenceVo7.wav", 2048);
    mSequenceVoiceOverPlayers[7] = mMinim.loadFile("sequenceVo8.wav", 2048);
    */
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
