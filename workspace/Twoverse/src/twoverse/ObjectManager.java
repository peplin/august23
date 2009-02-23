package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;

public abstract class ObjectManager extends Thread {
    protected HashMap<Integer, Galaxy> mGalaxies;
    protected HashMap<Integer, PlanetarySystem> mPlanetarySystems;
    protected HashMap<Integer, ManmadeBody> mManmadeBodies;
    protected Properties mConfigFile;
    protected static Logger sLogger = Logger.getLogger(ObjectManager.class
            .getName());

    public ObjectManager() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                            "twoverse/conf/ObjectManager.properties"));
        } catch (IOException e) {

        }

        mGalaxies = new HashMap<Integer, Galaxy>();
        mPlanetarySystems = new HashMap<Integer, PlanetarySystem>();
        mManmadeBodies = new HashMap<Integer, ManmadeBody>();
    }

    public ArrayList<CelestialBody> getAllBodies() {
        ArrayList<CelestialBody> allBodies = new ArrayList<CelestialBody>();
        allBodies.addAll(mGalaxies.values());
        allBodies.addAll(mPlanetarySystems.values());
        allBodies.addAll(mManmadeBodies.values());
        return allBodies;
    }

    public ArrayList<Galaxy> getGalaxies() {
        return new ArrayList<Galaxy>(mGalaxies.values());
    }

    public ArrayList<PlanetarySystem> getPlanetarySystems() {
        return new ArrayList<PlanetarySystem>(mPlanetarySystems.values());
    }

    public ArrayList<ManmadeBody> getManmadeBodies() {
        return new ArrayList<ManmadeBody>(mManmadeBodies.values());
    }

    public CelestialBody getCelestialBody(int objectId)
            throws UnhandledCelestialBodyException {
        if (mGalaxies.containsKey(objectId)) {
            return mGalaxies.get(objectId);
        } else if (mPlanetarySystems.containsKey(objectId)) {
            return mPlanetarySystems.get(objectId);
        } else if (mManmadeBodies.containsKey(objectId)) {
            return mManmadeBodies.get(objectId);
        } else {
            throw new UnhandledCelestialBodyException("No such object ID");
        }
    }

    public void getOwnedBodies(User user) {

    }

    public Galaxy getGalaxy(int id) {
        return mGalaxies.get(id);
    }

    public PlanetarySystem getPlanetarySystem(int id) {
        return mPlanetarySystems.get(id);
    }

    public ManmadeBody getManmadeBody(int id) {
        return mManmadeBodies.get(id);
    }

    public synchronized void add(Galaxy galaxy) {
        mGalaxies.put(galaxy.getId(), galaxy);
    }

    public synchronized void add(PlanetarySystem system) {
        mPlanetarySystems.put(system.getId(), system);
    }

    public synchronized void add(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
    }

    /**
     * 
     * So, if this is a new object coming in over an XML feed, i need to match
     * it with its ID. Okay.
     * 
     * TODO This overwrites - is that okay? No...need ID for objects sent from
     * client, so need to insert into database first.
     */
    public synchronized void update(Galaxy galaxy) {
        mGalaxies.put(galaxy.getId(), galaxy);
    }

    public synchronized void update(PlanetarySystem system) {
        mPlanetarySystems.put(system.getId(), system);
    }

    public synchronized void update(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
    }

    @SuppressWarnings("serial")
    public class UnhandledCelestialBodyException extends Exception {
        public UnhandledCelestialBodyException(String msg) {
            super(msg);
        }
    }

}
