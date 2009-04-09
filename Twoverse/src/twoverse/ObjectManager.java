package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.util.User;

public abstract class ObjectManager extends TimerTask {
    protected HashMap<Integer, CelestialBody> mCelestialBodies;
    protected HashMap<Integer, Link> mLinks;
    protected ReentrantReadWriteLock mLock;
    protected Properties mConfigFile;
    protected static Logger sLogger =
            Logger.getLogger(ObjectManager.class.getName());

    public ObjectManager() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/ObjectManager.properties"));
        } catch(IOException e) {

        }

        mLock = new ReentrantReadWriteLock();

        mCelestialBodies = new HashMap<Integer, CelestialBody>();
        mLinks = new HashMap<Integer, Link>();

        Timer feedPushTimer = new Timer();
        feedPushTimer.scheduleAtFixedRate(this,
                1000,
                Long.valueOf(mConfigFile.getProperty("FEED_DELAY")));
    }

    public ArrayList<CelestialBody> getAllBodies() {
        mLock.readLock().lock();
        ArrayList<CelestialBody> allBodies = new ArrayList<CelestialBody>();
        allBodies.addAll(mCelestialBodies.values());
        mLock.readLock().unlock();
        return allBodies;
    }

    public ArrayList<Link> getAllLinks() {
        mLock.readLock().lock();
        ArrayList<Link> allLinks = new ArrayList<Link>();
        allLinks.addAll(mLinks.values());
        mLock.readLock().unlock();
        return allLinks;
    }

    public CelestialBody getCelestialBody(int objectId)
            throws UnhandledCelestialBodyException {
        CelestialBody result = null;
        mLock.readLock().lock();
        try {
            if(mCelestialBodies.containsKey(objectId)) {
                result = mCelestialBodies.get(objectId);
            } else {
                throw new UnhandledCelestialBodyException("No such object ID: "
                        + objectId);
            }
        } finally {
            mLock.readLock().unlock();
        }
        return result;
    }

    public ArrayList<CelestialBody> getOwnedBodies(User user) {
        mLock.readLock().lock();
        // TODO write getOwned bodies if we need it
        ArrayList<CelestialBody> result = new ArrayList<CelestialBody>();
        mLock.readLock().unlock();
        return result;
    }

    public void add(CelestialBody body) throws UnhandledCelestialBodyException {
        if(body.getParentId() == 0) {
            throw new UnhandledCelestialBodyException("Parent is required");
        }
        mLock.writeLock().lock();
        mCelestialBodies.put(body.getId(), body);
        mCelestialBodies.get(body.getParentId()).addChild(body.getId());
        mLock.writeLock().unlock();
    }

    public void add(Link link) {
        mLock.writeLock().lock();
        mLinks.put(link.getId(), link);
        mLock.writeLock().unlock();
    }

    /**
     * 
     * So, if this is a new object coming in over an XML feed, i need to match
     * it with its ID. Okay. Careful - for now, this overwrites any data we
     * already have!
     */
    public void update(CelestialBody body) {
        mLock.writeLock().lock();
        if(mCelestialBodies.containsKey(body.getId())) {
            mCelestialBodies.get(body.getId()).update(body);
        } else {
            mCelestialBodies.put(body.getId(), body);
        }
        mLock.writeLock().unlock();
    }

    public void update(Link link) {
        mLock.writeLock().lock();
        if(!mLinks.containsKey(link.getId())) {
            mLinks.put(link.getId(), link);
        }
        mLock.writeLock().unlock();
    }

    public class UnhandledCelestialBodyException extends Exception {
        private static final long serialVersionUID = -341317408431555160L;

        public UnhandledCelestialBodyException(String msg) {
            super(msg);
        }
    }

}
