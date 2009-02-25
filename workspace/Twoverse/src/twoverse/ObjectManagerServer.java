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
import twoverse.object.PlanetarySystem;

public class ObjectManagerServer extends ObjectManager {
    Database mDatabase;

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

    private void initialize() {
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
        super.add(galaxy);
        mDatabase.add(galaxy); //TODO should this be queued? will block now.
        mLock.writeLock().unlock();
    }

    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(PlanetarySystem system) {
        mLock.writeLock().lock();
        super.add(system);
        mDatabase.add(system);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    @Override
    public void add(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        super.add(manmadeBody);
        mDatabase.add(manmadeBody);
        mLock.writeLock().unlock();
    }
    
    @Override
    public void update(Galaxy galaxy) {
        mLock.writeLock().lock();
        super.update(galaxy);
        //mDatabase.update(galaxy); TODO when does this update? queued?
        mLock.writeLock().unlock();
    }

    @Override
    public void update(PlanetarySystem system) {
        mLock.writeLock().lock();
        super.update(system);
      //mDatabase.update(system);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        super.update(manmadeBody);
      //mDatabase.update(manmadeBody);
        mLock.writeLock().unlock();
    }
}
