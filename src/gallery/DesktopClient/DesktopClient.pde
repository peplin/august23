/**
** Twoverse Desktop Client
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

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.ObjectManagerClient;
import twoverse.RequestHandlerClient;
import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.object.CelestialBody;
import twoverse.object.Star;
import twoverse.util.Camera;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

/** Constants for Configuration **/
private static final int WINDOW_WIDTH = 1024;
private static final int WINDOW_HEIGHT = 768;
private static final int FRAME_RATE = 30;

/** Control Members **/
private DesktopInterface mInterface;

/** Object & Server Members **/
private ObjectManagerClient mObjectManager;
private RequestHandlerClient mRequestHandler;

/** Camera Properties **/
private Camera mCamera;

/** View Modes - nasty numbers becase there is no enum in 1.4 
** 0 - galaxy view
** 1 - star info view
** 2 - create star view (same as galaxy but different click function) 
** 3 - connect mode
*/
private MultitouchModeInterface mModes[];
private int mCurrentMode = 0;

void setup() {
    frameRate(FRAME_RATE);
    size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);

    mCamera =
            new Camera(this,
                    (float) (width / 2.0),
                    (float) (height / 2.0),
                    (float) ((height / 2.0) / tan((float) (PI * 60.0 / 360.0))),
                    (float) (width / 2.0),
                    (float) (height / 2.0),
                    0, 1);

    mRequestHandler = new RequestHandlerClient("http://141.213.30.171:8080");
    //mRequestHandler = new RequestHandlerClient("http://localhost:8080");
    mInterface = new DesktopInterface(this);

    User user =
            new User(0, "august_client", null, null, 100);
    //TODO before releasing code, figure out how to hide this
    user.setPlaintextPassword("grocs1966");
    mRequestHandler.createAccount(user);
    mRequestHandler.login(user.getUsername(), "grocs1966");

    mObjectManager = new ObjectManagerClient(mRequestHandler, "http://141.213.30.171/gallery/feed.xml");
    //mObjectManager = new ObjectManagerClient(mRequestHandler, "http://localhost/feed.xml");

    mModes = new MultitouchModeInterface[4];
    mModes[0] = new GalaxyMode(this, mObjectManager, mCamera);
    mModes[1] = new InfoMode(this);
    mModes[3] = new ConnectionMode(this, mObjectManager, mCamera);

    Handler[] handlers = Logger.getLogger("").getHandlers();
    for(int i = 0; i < handlers.length; i++) {
        handlers[i].setLevel(Level.WARNING);
    }
    
    textMode(SCREEN);
    textAlign(LEFT);
}

void draw() {
    background(0);
    pushMatrix();
    mCamera.setCamera();
    getMode().display();
    popMatrix();
    translate(width/2, height/2);
    mInterface.display();
}

MultitouchModeInterface getMode() {
    return mModes[mCurrentMode];
}

void setMode(int mode) {
    getMode().disable();
    mCurrentMode = mode;
}

void mouseDragged() {
    mCamera.changeTranslateVelocity(mouseX - pmouseX, mouseY - pmouseY);
}
