package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
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
            Iterator<PlanetarySystem> it = mPlanetarySystems.values()
                    .iterator();
            while (it.hasNext()) {
                root.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<Planet> it = mPlanets.values()
                    .iterator();
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
            FileOutputStream xmlFeedFile = new FileOutputStream(mConfigFile
                    .getProperty("FEED_FILE_LOCATION"));
            OutputStream bufferedXmlFeedFile = new BufferedOutputStream(
                    xmlFeedFile);
            Serializer serializer = new Serializer(bufferedXmlFeedFile,
                    "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch (IOException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    protected void flushToDatabase() {
        mLock.writeLock().lock();
        //TODO write flushToDatabase
        // simulation calls this when done with a timestep
        ArrayList<CelestialBody> allBodies = getAllBodies();
        for(CelestialBody body : bodies) {
            if(body.isDirty()) {
                mDatabase.update(body); //TODO how do we get the actual type here?
                //it seems like I should add to the celestialbody interface
                //methods to commit to db, etc...but it seems wrong to make them
                //know about the database
                body.setDirty(false);
            }
        }
        mLock.writeLock().unlock();
    }

    private void initialize() {
        //TODO coming directly from DB, mark these as clean or make
        // a constructor to do so
        mGalaxies.putAll(mDatabase.getGalaxies());
        mPlanetarySystems.putAll(mDatabase.getPlanetarySystems());
        mManmadeBodies.putAll(mDatabase.getManmadeBodies());
    }

    /**
     * Modifies galaxy, sets ID and birth time
     */
    @Override
    public void add(Galaxy galaxy) {
        mLock.writeLock().lock();
        // Make sure to add to DB first, since it sets the ID
        mDatabase.add(galaxy); //TODO should this be queued? will block now.
        //TODO don't allow adding objects without ID set
        super.add(galaxy);
        galaxy.setDirty(false);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(PlanetarySystem system) {
        mLock.writeLock().lock();
        mDatabase.add(system);
        super.add(system);
        system.setDirty(false);
        mLock.writeLock().unlock();
    }
    
    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(Planet planet) {
        mLock.writeLock().lock();
        mDatabase.add(planet);
        super.add(planet);
        planet.setDirty(false);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    @Override
    public void add(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        mDatabase.add(manmadeBody);
        super.add(manmadeBody);
        manmadeBody.setDirty(false);
        mLock.writeLock().unlock();
    }
    
    @Override
    public void update(Galaxy galaxy) {
        mLock.writeLock().lock();
        mDatabase.update(galaxy);
        super.update(galaxy);
        galaxy.setDirty(false);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(PlanetarySystem system) {
        mLock.writeLock().lock();
        super.update(system);
        mDatabase.update(system);
        system.setDirty(false);
        mLock.writeLock().unlock();
    }
    
    @Override
    public void update(Planet planet) {
        mLock.writeLock().lock();
        super.update(planet);
        mDatabase.update(planet);
        planet.setDirty(false);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        super.update(manmadeBody);
        mDatabase.update(manmadeBody);
        manmadeBody.setDirty(false);
        mLock.writeLock().unlock();
    }
}
