package twoverse;

import java.util.logging.Logger;

public class SimulationRunner extends TimerTask {
    private static Logger sLogger = Logger.getLogger(SimulationRunner.class
            .getName());
    private ObjectManager mObjectManager;

    public SimulationRunner(ObjectManager objectManager) {
        mObjectManager = objectManager;

        Timer timestepTimer = new Timer();
        timestepTimer.scheduleAtFixedRate(this,
                                          1000,
                                          Long.valueOf(mConfigFile.getProperty("TIMESTEP")));
    }

    @Override
    public void run() {
        ArrayList<CelestialBody> allBodies = mObjectManager.getAllBodies();
        for(CelestialBody firstBody : bodies) {
            for(CelestialBody secondBody : bodies) {
                // interact
                //TODO check scale...maybe instead of doing all, do a nested
                //once for each scale - n^2...ouch...but unavoidable
            }
        }
        mObjectManager.flushToDatabase();
    }
}
