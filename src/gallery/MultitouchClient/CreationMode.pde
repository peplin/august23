/**
** Creation Mode for Twoverse Client
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

public class CreationMode extends GalaxyMode {
    private final String WIREMAP_SERVER_IP = "141.213.39.155";
    //private final String WIREMAP_SERVER_IP = "localhost";
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
        mColorGrabber = new ActiveColorGrabber(mParent);
        mStarSimulation = new StarSimulation(parent, null);
        mFont = loadFont("promptFont.vlw");
        initializeAudio();
        textFont(mFont);
    }

    private void connectToServer() {
        mClient = new Client(mParent, WIREMAP_SERVER_IP, 1966);
    }

    public void display() {
        if(mNewStar == null) {
            mCamera.resetScale();
            super.display();
        } else {
            if(mSimulationRunning) {
                pushMatrix();
                translate(-mCamera.getCenterX(), -mCamera.getCenterY());
                mStarSimulation.display();
                popMatrix();
            } 
            else {
                stroke(255);
                fill(255);
                textMode(SCREEN);
                textAlign(CENTER);
                text("Please enter the airlock", width/2, height/2);
                textAlign(LEFT);
            }
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
                mClient.stop();
            } else if(message.equals("no")) {
                mSimulationRunning = false;
                mNewStar = null;
                mClient.stop();
            } else {
                String messageParts[] = message.split(" ");
                if(messageParts[0].equals("beat")) {
                    if(messageParts.length == 2) {
                        mNewStar.setFrequency(
                                (double)Float.parseFloat(messageParts[1]));
                        mStarSimulation.setFrequency(
                                (float)mNewStar.getFrequency()); 
                    } else {
                        throw new Exception("Malformed message: " + message);
                    }
                } else if(messageParts[0].equals("state")) {
                    if(messageParts.length == 2) {
                        int endState = Integer.parseInt(messageParts[1]);
                        mNewStar.setState(endState + 1);
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
                                mCurrentPlayer.loop(0);
                            } else {
                                throw new Exception("Bad audio index requested: " + message);
                            }
                        } else if(messageParts[1].equals("nar")) {
                            int player = Integer.parseInt(messageParts[2]);
                            if(player >= 0 && player < mNarrationVoiceOverPlayers.length) {
                                mCurrentPlayer =
                                    mNarrationVoiceOverPlayers[player];
                                mCurrentPlayer.loop(0);
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
        if(mClient != null) {
            mClient.write(message + "/");
        } else {
            println("DEBUG: Unable to write to null server");
        }
    }

    public void cursorPressed(Point cursor) {
        if(mNewStar == null) {
            pushMatrix();
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
                    (int)red(activeColor),
                    (int)green(activeColor),
                    (int)blue(activeColor),
                    255,
                    1);
            connectToServer();
            sendMessage("color " + activeColor);
            mStarSimulation.setColor(activeColor);
            popMatrix();
        }
    }

    public void disable() {
        mNewStar = null;
        mSimulationRunning = false;
    }

    public boolean canDisable() {
        return mNewStar == null && !mSimulationRunning;
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



