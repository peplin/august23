package twoverse;

import java.io.IOException;
import java.util.logging.Level;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

public class ObjectManagerClient extends ObjectManager {
    private Builder mParser;
    private RequestHandlerClient mRequestHandler;

    public ObjectManagerClient(RequestHandlerClient requestHandler) {
        super();
        mParser = new Builder();
        mRequestHandler = requestHandler;
    }

    @Override
    public void run() {
        pullFeed();
    }

    private void pullFeed() {
        try {
            Document doc = mParser.build(mConfigFile.getProperty("FEED_URL"));
            // TODO test this works
            // TODO how do we pick up deleted objects?
            Elements galaxies = doc.getRootElement().getChildElements(
                    mConfigFile.getProperty("GALAXY_TAG"));
            for (int i = 0; i < galaxies.size(); i++) {
                Galaxy g = new Galaxy(galaxies.get(i));
                update(g);
            }

            Elements planetarySystems = doc.getRootElement().getChildElements(
                    mConfigFile.getProperty("PLANETARY_SYSTEM_TAG"));
            for (int i = 0; i < planetarySystems.size(); i++) {
                PlanetarySystem system = new PlanetarySystem(planetarySystems
                        .get(i));
                update(system);
            }

            Elements manmadeBodies = doc.getRootElement().getChildElements(
                    mConfigFile.getProperty("CELESTIAL_BODY_TAG"));
            for (int i = 0; i < manmadeBodies.size(); i++) {
                ManmadeBody manmadeBody = new ManmadeBody(manmadeBodies.get(i));
                update(manmadeBody);
            }

        } catch (ParsingException e) {
            sLogger.log(Level.WARNING, "Feed may be malformed", e);
        } catch (IOException e) {
            sLogger.log(Level.WARNING, "Unable to connect to feed", e);
        }
    }
    
    /**
     * Modifies galaxy, sets ID and birth time
     */
    @Override
    public void add(Galaxy galaxy) {
        mLock.writeLock().lock();
        super.add(galaxy);
        mRequestHandler.addGalaxy(galaxy);
        mLock.writeLock().unlock();
    }
    
    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(PlanetarySystem system) {
        mLock.writeLock().lock();
        super.add(system);
        mRequestHandler.addPlanetarySystem(system);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    @Override
    public void add(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        super.add(manmadeBody);
        mRequestHandler.addManmadeBody(manmadeBody);
        mLock.writeLock().unlock();
    }
    
    @Override
    public void update(Galaxy galaxy) {
        mLock.writeLock().lock();
        super.update(galaxy);
        //mRequestHandler.updateGalaxy(galaxy); TODO
        mLock.writeLock().unlock();
    }

    @Override
    public void update(PlanetarySystem system) {
        mLock.writeLock().lock();
        super.update(system);
        //mRequestHandler.updatePlanetarySystem(system);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(ManmadeBody manmadeBody) {
        mLock.writeLock().lock();
        super.update(manmadeBody);
        //mRequestHandler.updateManmadeBody(manmadeBody);
        mLock.writeLock().unlock();
    }

}
