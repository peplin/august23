/**
 * Twoverse Object Manager Server
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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.object.Star;

/**
 * The Object Manager Server is an extentension of the Object Manager that uses
 * a Database instance as its backend storage. This derivative also periodically
 * pushes the universe to an XML feed file, so it must be run on the same
 * machine as the web server.
 * 
 * This class is thread-safe.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class ObjectManagerServer extends ObjectManager {
    private Database mDatabase;

    /**
     * Construct an instance of the ObjectManagerServer, storing a reference to
     * the database. Initializes the universe with all objects in the database.
     */
    public ObjectManagerServer(Database database) {
        super();
        mDatabase = database;
        initialize();
    }

    /**
     * Run any tasks scheduled for periodic execution.
     * 
     * At the moment, this only includes publishing the XML feed to a file.
     */
    @Override
    public void run() {
        mLock.readLock().lock();
        publishFeed();
        mLock.readLock().unlock();
    }

    /**
     * Build the XML feed for the universe and save it to a file. The filename
     * is specified in twoverse.conf.ObjectManager.properties.
     * 
     * For readability, the feed is formatted with tabs. To save bandwidth and
     * increase performance, this formatting may be removed.
     */
    public void publishFeed() {
        // TODO long term, or if performance is an issue, figure out if
        // a feed for each scale is feasible. right now, that wouldn't be very
        // simple as all objects are stored together, and we would have to
        // recurse
        // to figure out the number of feeds
        Element root = new Element(mConfigFile.getProperty("ROOT_TAG"));
        for(CelestialBody body : mCelestialBodies.values()) {
            root.appendChild(body.toXmlElement());
        }

        for(Link link : mLinks.values()) {
            root.appendChild(link.toXmlElement());
        }

        Document doc = new Document(root);
        try {
            FileOutputStream xmlFeedFile =
                    new FileOutputStream(mConfigFile.getProperty("FEED_FILE_LOCATION"));
            OutputStream bufferedXmlFeedFile =
                    new BufferedOutputStream(xmlFeedFile);
            Serializer serializer =
                    new Serializer(bufferedXmlFeedFile, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch(IOException e) {
            sLogger.log(Level.WARNING, "Unable to write feed file", e);
        }
    }

    /**
     * Flush all locally stored objects to the database, updating only if the
     * local object is marked dirty.
     */
    protected void flushToDatabase() {
        sLogger.log(Level.INFO, "Flushing to database");
        mLock.writeLock().lock();
        // simulation calls this when done with a timestep
        ArrayList<CelestialBody> allBodies = getAllBodies();
        for(CelestialBody body : allBodies) {
            if(body.isDirty()) {
                mDatabase.update(body);
                body.setDirty(false);
            }
        }
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Flush to database completed");
        // TODO do we need to flush links? never modified, so probably not
    }

    /**
     * Load all objects from the database into the universe. This method must be
     * updated if a new object type is added.
     */
    private void initialize() {
        sLogger.log(Level.INFO, "Initializing ObjectManager from Database");
        // All of these are marked clean explicitly
        try {
            mCelestialBodies.put(1, CelestialBody.selectFromDatabase(1));
            mCelestialBodies.putAll(Star.selectAllFromDatabase());

            for(CelestialBody body : mCelestialBodies.values()) {
                if(body.getParentId() != 0) {
                    mCelestialBodies.get(body.getParentId())
                            .addChild(body.getId());
                }
            }

            mLinks.putAll(Link.selectAllFromDatabase());
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to initialize objects", e);
        }
    }

    /**
     * Add an object to the universe and database.
     * 
     * @param body
     *            the object to insert. The ID and birth time of the object are
     *            set after being inserted into the database.
     * 
     * @throws UnhandledCelestialBodyException
     */
    @Override
    public void add(CelestialBody body) throws UnhandledCelestialBodyException {
        sLogger.log(Level.INFO, "Adding body: " + body);
        mLock.writeLock().lock();
        // Make sure to add to DB first, since it sets the ID
        mDatabase.insert(body);
        super.add(body);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Body added is: " + body);
    }

    /**
     * Add a link to the universe and database.
     * 
     * @param link
     *            the link to insert. The ID of the object is set after being
     *            inserted into the database.
     * 
     */
    @Override
    public void add(Link link) {
        sLogger.log(Level.INFO, "Adding link: " + link);
        mLock.writeLock().lock();
        // Make sure to add to DB first, since it sets the ID
        mDatabase.insert(link);
        super.add(link);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Link added is: " + link);
    }

    /**
     * Update an object locally and in the database.
     * 
     * @param body
     *            the object to updateinsert. The ID must be set, and it must
     *            match an ID in the database.
     * 
     * @throws UnhandledCelestialBodyException
     */
    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Updating with body: " + body);
        mLock.writeLock().lock();
        mDatabase.update(body);
        super.update(body);
        mLock.writeLock().unlock();
    }
}
