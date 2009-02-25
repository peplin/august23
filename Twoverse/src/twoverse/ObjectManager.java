package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;

public abstract class ObjectManager extends TimerTask {
    protected HashMap<Integer, Galaxy> mGalaxies;
    protected HashMap<Integer, PlanetarySystem> mPlanetarySystems;
    protected HashMap<Integer, ManmadeBody> mManmadeBodies;
    protected ReentrantReadWriteLock mLock;
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

        mLock = new ReentrantReadWriteLock();

        mGalaxies = new HashMap<Integer, Galaxy>();
        mPlanetarySystems = new HashMap<Integer, PlanetarySystem>();
        mManmadeBodies = new HashMap<Integer, ManmadeBody>();

        Timer feedPushTimer = new Timer();
        feedPushTimer.scheduleAtFixedRate(this, 0, this,
                Long.valueOf(mConfigFile.getProperty("FEED_DELAY")));
    }

    public ArrayList<CelestialBody> getAllBodies() {
        mLock.readLock().lock();
        ArrayList<CelestialBody> allBodies = new ArrayList<CelestialBody>();
        allBodies.addAll(mGalaxies.values());
        allBodies.addAll(mPlanetarySystems.values());
        allBodies.addAll(mManmadeBodies.values());
        mLock.readLock().unlock();
        return allBodies;
    }

    public ArrayList<Galaxy> getGalaxies() {
        mLock.readLock().lock();
        ArrayList<Galaxy> result = new ArrayList<Galaxy>(mGalaxies.values());
        mLock.readLock().unlock();
        return result;
    }

    public ArrayList<PlanetarySystem> getPlanetarySystems() {
        mLock.readLock().lock();
        ArrayList<PlanetarySystem> result = new ArrayList<PlanetarySystem>(
                mPlanetarySystems.values());
        mLock.readLock().unlock();
        return result;
    }

    public ArrayList<ManmadeBody> getManmadeBodies() {
        mLock.readLock().lock();
        ArrayList<ManmadeBody> result = new ArrayList<ManmadeBody>(
                mManmadeBodies.values());
        mLock.readLock().unlock();
        return result;
    }

    public CelestialBody getCelestialBody(int objectId)
            throws UnhandledCelestialBodyException {
        CelestialBody result = null;
        mLock.readLock().lock();
        if (mGalaxies.containsKey(objectId)) {
            result = mGalaxies.get(objectId);
        } else if (mPlanetarySystems.containsKey(objectId)) {
            result = mPlanetarySystems.get(objectId);
        } else if (mManmadeBodies.containsKey(objectId)) {
            result = mManmadeBodies.get(objectId);
        } else {
            mLock.readLock().unlock();
            throw new UnhandledCelestialBodyException("No such object ID");
        }
        mLock.readLock().unlock();
        return result;
    }

    public ArrayList<CelestialBody> getOwnedBodies(User user) {
        mLock.readLock().lock();
        //TODO write getOwned bodies if we need it
        ArrayList<CelestialBody> result = new ArrayList<CelestialBody>();
        mLock.readLock().unlock();
        return result;

    }

    public Galaxy getGalaxy(int id) {
        mLock.readLock().lock();
        Galaxy result = mGalaxies.get(id);
        mLock.readLock().unlock();
        return result;
    }

    public PlanetarySystem getPlanetarySystem(int id) {
        mLock.readLock().lock();
        PlanetarySystem result = mPlanetarySystems.get(id);
        mLock.readLock().unlock();
        return result;
    }

    public ManmadeBody getManmadeBody(int id) {
        mLock.readLock().lock();
        ManmadeBody result = mManmadeBodies.get(id);
        mLock.readLock().unlock();
        return result;
    }

    public void add(Galaxy galaxy) {
        mGalaxies.put(galaxy.getId(), galaxy);
    }

    public void add(PlanetarySystem system) {
        mPlanetarySystems.put(system.getId(), system);
    }

    public void add(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
    }

    /**
     * 
     * So, if this is a new object coming in over an XML feed, i need to match
     * it with its ID. Okay.
     * 
     * TODO This overwrites - is that okay? No...need ID for objects sent from
     * client, so need to insert into database first.
     * 
     * TODO do we need upate for arrays of objects to match the DB?
     */
    public void update(Galaxy galaxy) {
        mGalaxies.put(galaxy.getId(), galaxy);
    }

    public void update(PlanetarySystem system) {
        mPlanetarySystems.put(system.getId(), system);
    }

    public void update(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
    }

    public class UnhandledCelestialBodyException extends Exception {
        public UnhandledCelestialBodyException(String msg) {
            super(msg);
        }
    }

}
