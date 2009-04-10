import processing.net.*;
import ddf.minim.*;

Server mServer;
Client mCurrentClient;

boolean mBusy = false;
boolean mHeartbeatSet = false;
boolean mColorSet = false;

float mHeartbeatFrequency;
color mColor;

Minim mMinim;
AudioPlayer mAmbientPlayers[];
AudioPlayer mVoiceOverPlayers[];
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
    //TODO figure out order of voiceovers
    if(mBusy) {
        if(!mColorSet) {
            //TODO get color from client - MT has camera
            if(!mColorVoiceOver.isPlaying()) {

            }
        } else if(!mHeartbeatSet) {
            if(!mHeartbeatVoiceOver.isLooping()) {
                mHeartbeatVoiceOver.loop();
            }
        } else {
            if(mHeartbeatVoiceOver.isLooping()) {
                mHeartbeatVoiceOver.play();
            }
            //TODO star formation
        }
    } else {
        // do interesting light show stuff, or stay silent
        //disconnect from client when finished with message of final state
    }

    if(!mCurrentAmbientPlayer.isPlaying()) {
        mCurrentAmbientPlayer = mAmbientPlayers[random(mAmbientPlayers.length)];
        mCurrentAmbientPlayer.play();
    }
}

void serverEvent(Server server, Client client) {
    mBusy = true;
    mCurrentClient = client;
    mColorSet = false;
    mHeartbeatSet = false;
}

void initializeAudio() {
    mAudioOutput = mMinim.getLineOut(Minimum.MONO);
    mSineWave = new SineWave(440, .5, mAudioOutput.sampleRate());
    mSineWave.portamento(200);
    mAudioOutput.addSignal(mSineWave);
    mAudioOutput.disableSignal(mSineWave);

    mAmbientPlayers = new AudioPlayer[7];
    for(int i = 0; i < 7; i++) {
        mAmbientPlayers[i] = mMinim.loadFile("ambient" + i + ".wav", 2048);
    }
    mCurrentAmbientPlayer = mAmbientPlayers[i];

    //TODO get exact count
    mVoiceOverPlayers = new AudioPlayer[40];
    for(int i = 0; i < 40; i++) {
        mVoiceOverPlayers[i] = mMinim.loadFile("vo" + i + ".wav", 2048);
    }
}

void stop() {
    for(int i = 0; i < mAmbientPlayers.length; i++) {
        if(mAmbientPlayers[i] != null) {
            mAmbientPlayers.close();
        }
    }

    for(int i = 0; i < mVoiceOverPlayers.length; i++) {
        if(mVoiceOverPlayers[i] != null) {
            mVoiceOverPlayers.close();
        }
    }
    mMinim.stop();
    super.stop();
}
