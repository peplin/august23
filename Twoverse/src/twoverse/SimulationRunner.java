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
	private static Logger sLogger = Logger.getLogger(SimulationRunner.class
			.getName());
	protected Properties mConfigFile;
	private ObjectManagerServer mObjectManager;

	public SimulationRunner(ObjectManagerServer objectManager) {
		try {
			mConfigFile = new Properties();
			mConfigFile.load(this.getClass().getClassLoader()
					.getResourceAsStream(
							"twoverse/conf/SimulationRunner.properties"));
		} catch (IOException e) {
			sLogger.log(Level.SEVERE, e.getMessage(), e);
		}
		mObjectManager = objectManager;

		Timer timestepTimer = new Timer();
		timestepTimer.scheduleAtFixedRate(this, 1000, Long.valueOf(mConfigFile
				.getProperty("TIMESTEP")));
	}

	public void run() {
		ArrayList<CelestialBody> allBodies = mObjectManager.getAllBodies();
		for (CelestialBody firstBody : allBodies) {
			for (CelestialBody secondBody : allBodies) {
				// interact
				// TODO check scale...maybe instead of doing all, do a nested
				// once for each scale - n^2...ouch...but unavoidable
			}
		}
		mObjectManager.flushToDatabase();
	}
}
