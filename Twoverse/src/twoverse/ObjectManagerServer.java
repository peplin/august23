package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.Planet;
import twoverse.object.PlanetarySystem;

public class ObjectManagerServer extends ObjectManager {
    private Database mDatabase;

    public ObjectManagerServer(Database database) {
        super();
        mDatabase = database;
        initialize();
    }

    @Override
    public void run() {
        mLock.readLock().lock();
        publishFeed();
        mLock.readLock().unlock();
    }

    public void publishFeed() {
        // TODO long term, or if performance is an issue, figure out if
        // a feed for each scale is feasible. right now, that wouldn't be very
        // simple as all objects are stored together, and we would have to
        // recurse
        // to figure out the number of feeds
        Element root = new Element(mConfigFile.getProperty("ROOT_TAG"));
        Iterator<CelestialBody> it = mCelestialBodies.values().iterator();
        while(it.hasNext()) {
            root.appendChild(it.next().toXmlElement());
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
    }

    private void initialize() {
        sLogger.log(Level.INFO, "Initializing ObjectManager from Database");
        // All of these are marked clean explicitly
        try {
            mCelestialBodies.putAll(Galaxy.selectAllFromDatabase());
            mCelestialBodies.putAll(PlanetarySystem.selectAllFromDatabase());
            mCelestialBodies.putAll(ManmadeBody.selectAllFromDatabase());
            mCelestialBodies.putAll(Planet.selectAllFromDatabase());
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to initialize objects", e);
        }
    }

    /**
     * Modifies galaxy, sets ID and birth time
     */
    @Override
    public void add(CelestialBody body) {
        sLogger.log(Level.INFO, "Adding body: " + body);
        mLock.writeLock().lock();
        // Make sure to add to DB first, since it sets the ID
        mDatabase.insert(body);
        super.add(body);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Galaxy added is: " + body);
    }

    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Updating with body: " + body);
        mLock.writeLock().lock();
        mDatabase.update(body);
        super.update(body);
        mLock.writeLock().unlock();
    }
}
