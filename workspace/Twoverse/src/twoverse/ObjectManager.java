package twoverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import twoverse.Database;
import twoverse.util.User;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

@SuppressWarnings("serial")
class UnhandledCelestialBodyException extends Exception {
    public UnhandledCelestialBodyException(String msg) {
        super(msg);
    }
}

public abstract class ObjectManager extends Thread {
    protected static Logger sLogger = Logger.getLogger(ObjectManager.class
            .getName());

    public ObjectManager(Database database) {
        mDatabase = database;
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
    
    public synchronized int add(Galaxy galaxy) {
        mGalaxies.put(galaxy.getId(), galaxy);
        return 0;
    }

    public synchronized int add(PlanetarySystem system) {
        mPlanetarySystems.put(system.getId(), system);
        return 0;
    }

    public synchronized int add(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
        return 0;
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

    HashMap<Integer, Galaxy> mGalaxies;
    HashMap<Integer, PlanetarySystem> mPlanetarySystems;
    HashMap<Integer, ManmadeBody> mManmadeBodies;

    Database mDatabase;
}
