package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        Element root = new Element(mConfigFile.getProperty("ROOT_TAG"));
        {
            Iterator<Galaxy> it = mGalaxies.values().iterator();
            while (it.hasNext()) {
                root.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<PlanetarySystem> it =
                    mPlanetarySystems.values().iterator();
            while (it.hasNext()) {
                root.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<Planet> it = mPlanets.values().iterator();
            while (it.hasNext()) {
                root.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<ManmadeBody> it = mManmadeBodies.values().iterator();
            while (it.hasNext()) {
                root.appendChild(it.next().toXmlElement());
            }
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
        } catch (IOException e) {
            sLogger.log(Level.WARNING, "Unable to write feed file", e);
        }
    }

    protected void flushToDatabase() {
        sLogger.log(Level.INFO, "Flushing to database");
        mLock.writeLock().lock();
        // simulation calls this when done with a timestep
        ArrayList<CelestialBody> allBodies = getAllBodies();
        for (CelestialBody body : allBodies) {
            if(body.isDirty()) {
                // mDatabase.update(body); //TODO how do we get the actual type
                // here?
                // it seems like I should add to the celestialbody interface
                // methods to commit to db, etc...but it seems wrong to make
                // them
                // know about the database
                body.setDirty(false);
            }
        }
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Flush to database completed");
    }

    private void initialize() {
        sLogger.log(Level.INFO, "Initializing ObjectManager from Database");
        // All of these are marked clean explicitly
        mGalaxies.putAll(mDatabase.getGalaxies());
        mPlanetarySystems.putAll(mDatabase.getPlanetarySystems());
        mManmadeBodies.putAll(mDatabase.getManmadeBodies());
    }

    /**
     * Modifies galaxy, sets ID and birth time
     */
    @Override
    public void add(Galaxy galaxy) {
        sLogger.log(Level.INFO, "Adding galaxy: " + galaxy);
        mLock.writeLock().lock();
        // Make sure to add to DB first, since it sets the ID
        mDatabase.add(galaxy);
        super.add(galaxy);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Galaxy added is: " + galaxy);
    }

    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(PlanetarySystem system) {
        sLogger.log(Level.INFO, "Adding planetary system: " + system);
        mLock.writeLock().lock();
        mDatabase.add(system);
        super.add(system);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Planetary system added is: " + system);
        }

    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(Planet planet) {
        sLogger.log(Level.INFO, "Adding planet: " + planet);
         mLock.writeLock().lock();
        mDatabase.add(planet);
        super.add(planet);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Planet added is: " + planet);
            }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    @Override
    public void add(ManmadeBody manmadeBody) {
        sLogger.log(Level.INFO, "Adding manmade body: " + manmadeBody);
        mLock.writeLock().lock();
        mDatabase.add(manmadeBody);
        super.add(manmadeBody);
        mLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Manmade body added is: " + manmadeBody);
        }

    @Override
    public void update(Galaxy galaxy) {
        sLogger.log(Level.INFO, "Updating with galaxy: " + galaxy);
        mLock.writeLock().lock();
        mDatabase.update(galaxy);
        super.update(galaxy);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(PlanetarySystem system) {
        sLogger.log(Level.INFO, "Updating with planetary system: " + system);
        mLock.writeLock().lock();
        mDatabase.update(system);
        super.update(system);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(Planet planet) {
        sLogger.log(Level.INFO, "Updating with planet: " + planet);
        mLock.writeLock().lock();
        mDatabase.update(planet);
        super.update(planet);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(ManmadeBody manmadeBody) {
        sLogger.log(Level.INFO, "Updating with manmade body: " + manmadeBody);
        mLock.writeLock().lock();
        mDatabase.update(manmadeBody);
        super.update(manmadeBody);
        mLock.writeLock().unlock();
    }
}
