package twoverse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulationRunner extends Thread {
    private static Logger sLogger = Logger.getLogger(SimulationRunner.class
            .getName());
    private ObjectManager mObjectManager;

    public SimulationRunner(ObjectManager objectManager) {
        mObjectManager = objectManager;
    }
    
    public void run() {
        sLogger.fine("Simulation is running");
        while(true) {
            //loop over all objects
        }
    }
}
