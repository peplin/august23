/**
 * Twoverse Abstract Object Manager
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
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Link;

/**
 * The Object Manager is a middle man between any Twoverse client or server and
 * wherever it is ultimately storing the information about the universe.
 * 
 * In the case of the server, the Object Manager will act as a frontend to the
 * database, since all objects are stored there.
 * 
 * In the case of a client, the Object Manager will act as a frontend to XML-RPC
 * calls (or as a frontend to an XML-RPC call handler), as its objects are
 * stored elsewhere.
 * 
 * In both cases, a local copy of the object must be kept. This abstract class
 * represents the common set of operations between client and server object
 * management.
 * 
 * This class is thread-safe.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public abstract class ObjectManager extends TimerTask {
    protected HashMap<Integer, CelestialBody> mCelestialBodies;
    protected HashMap<Integer, Link> mLinks;
    protected ReentrantReadWriteLock mLock;
    protected Properties mConfigFile;
    protected static Logger sLogger =
            Logger.getLogger(ObjectManager.class.getName());

    /**
     * Constructs a new Object Manager, using the configuration at
     * twoverse.conf.Objectmanager.properties.
     * 
     * Schedules the period at which to update or update from the XML feed.
     */
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

    /**
     * Get a list of all known objects as CelestialBody instances.
     * 
     * @return list of all objects know locally
     */
    public ArrayList<CelestialBody> getAllBodies() {
        mLock.readLock().lock();
        ArrayList<CelestialBody> allBodies = new ArrayList<CelestialBody>();
        allBodies.addAll(mCelestialBodies.values());
        mLock.readLock().unlock();
        return allBodies;
    }

    /**
     * Get a list of all know Links.
     * 
     * @return list of all links know locally
     */
    public ArrayList<Link> getAllLinks() {
        mLock.readLock().lock();
        ArrayList<Link> allLinks = new ArrayList<Link>();
        allLinks.addAll(mLinks.values());
        mLock.readLock().unlock();
        return allLinks;
    }

    /**
     * Get a specific object, based on its primary key in the database.
     * 
     * @param objectId
     *            the ID of the object requested. Must be one set by the
     *            database.
     * @return the local object, if found
     * @throws UnhandledCelestialBodyException
     *             if the object ID is not found
     */
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

    /**
     * Add an object to the universe, and to the list of children of its parent.
     * 
     * @param body
     *            the object to add. Must have a parent object ID.
     */
    public void add(CelestialBody body) throws UnhandledCelestialBodyException {
        if(body.getParentId() == 0) {
            throw new UnhandledCelestialBodyException("Parent is required");
        }
        mLock.writeLock().lock();
        mCelestialBodies.put(body.getId(), body);
        mCelestialBodies.get(body.getParentId()).addChild(body.getId());
        mLock.writeLock().unlock();
    }

    /**
     * Adds a link to the universe.
     * 
     * TODO require that the ends of the link exist
     * 
     * @param link
     *            the link to add.
     */
    public void add(Link link) {
        mLock.writeLock().lock();
        mLinks.put(link.getId(), link);
        mLock.writeLock().unlock();
    }

    /**
     * Update an object in the universe, overwriting it completely if it exists
     * or adding if it doesn't.
     * 
     * This is intended for updating from an XML feed, where in some cases we
     * will know about the link already, and in others it will have been added
     * by another client.
     * 
     * @param body
     *            the body to add or update
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

    /**
     * Add a link to the database if it doesn't already exist.
     * 
     * This is intended for updated from an XML feed, where in some cases we
     * will know about the link already, and in others it will have been added
     * by another client.
     * 
     * @param link
     *            the link to add
     */
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
