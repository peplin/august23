/**
 * Simulation Thread
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;

public class SimulationRunner extends TimerTask {
    private static Logger sLogger =
            Logger.getLogger(SimulationRunner.class.getName());
    protected Properties mConfigFile;
    private ObjectManagerServer mObjectManager;

    public SimulationRunner(ObjectManagerServer objectManager) {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/SimulationRunner.properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }
        mObjectManager = objectManager;

        Timer timestepTimer = new Timer();
        timestepTimer.scheduleAtFixedRate(this,
                1000,
                Long.valueOf(mConfigFile.getProperty("TIMESTEP")));
    }

    @Override
    public void run() {
        ArrayList<CelestialBody> allBodies = mObjectManager.getAllBodies();
        for(CelestialBody firstBody : allBodies) {
            for(CelestialBody secondBody : allBodies) {
                // interact
                // TODO check scale...maybe instead of doing all, do a nested
                // once for each scale - n^2...ouch...but unavoidable
                // recursive simulate func?
            }
        }
        mObjectManager.flushToDatabase();
    }
}
