/**
** Twoverse Wiremap Client
**
** by Christopher Peplin (chris.peplin@rhubarbtech.com)
** for August 23, 1966 (GROCS Project Group)
** University of Michigan, 2009
**
** http://august231966.com
** http://www.dc.umich.edu/grocs
**
** Copyright 2009 Christopher Peplin 
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at 
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and
** limitations under the License. 
*/

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

/**
The Wiremap Client is not a direct Twoverse client. It requires a connection to
a running instance of a MultitouchClient. This client is inteded for use in the
August 23, 1966 gallery installation.

This client waits for a signal from the Multitouch Client that a new star should
be created. The star formation sequence then begins as follows:
   Ambient spaceship sounds are played through the local sound card.

   A voice over is played requesting the user to enter the airlock and plate
   their finger on the heartbeat detector to connec their lifeline.
   
   If the heartbeat is a reasonable value it is set, and after at most 3 plays
   of the audio the actual heartbeat value or a reasonable default is stored,
   sent to the Multitouch Client and saved into the star simulation.

   The user is instructed via voice over that the airlock atmosphere is
   stabilizing, and that now they can enter "space". The user is told that star
   formation is now commencing, and they are expected to walk into the darkened
   environment with the helmet on in order to view the wiremap. This voice over
   sounds are also requested over the network to be played on the Multitouch
   Client computer - this is for pumping out over the radio headset.

   At the end of the initial voice over sequence, the star formation animation
   begins displaying on the wiremap. Periodically a voice over is requested to
   be played by the Multitouch Client (requested over the network). The voice
   overs correspond to the current state of the star formation. The end state of
   the star is predetermined at this point, and is sent over the network to the
   multitouch client so that the animations match.

   When the animation is completed, a "done" command is sent over the network
   and the multitouch client should save the new star into the database and
   return the the default galaxy view. This client (Wiremap) goes into a "sleep"
   state and waits for the next star formation request.

@author Christopher Peplin (chris.peplin@rhubarbtech.com)
@version 1.0, Copyright 2009 under Apache License
*/
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
