package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import processing.core.PApplet;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.Planet;
import twoverse.object.PlanetarySystem;
import twoverse.object.applet.AppletBodyInterface;

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
            // TODO how do we pick up deleted objects?
            Elements galaxies =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("GALAXY_TAG"));
            for (int i = 0; i < galaxies.size(); i++) {
                Galaxy g = new Galaxy(galaxies.get(i));
                update(g);
            }

            Elements planetarySystems =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("PLANETARY_SYSTEM_TAG"));
            for (int i = 0; i < planetarySystems.size(); i++) {
                PlanetarySystem system =
                        new PlanetarySystem(planetarySystems.get(i));
                update(system);
            }

            Elements planets =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("PLANET_TAG"));
            for (int i = 0; i < planets.size(); i++) {
                Planet planet = new Planet(planets.get(i));
                update(planet);
            }

            Elements manmadeBodies =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("CELESTIAL_BODY_TAG"));
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

    public ArrayList<AppletBodyInterface> getAllBodiesAsApplets(PApplet parent) {
        mLock.readLock().lock();
        ArrayList<AppletBodyInterface> allBodies =
                new ArrayList<AppletBodyInterface>();
        Collection<CelestialBody> bodies = mCelestialBodies.values();
        for (CelestialBody body : bodies) {
            allBodies.add(body.getBodyAsApplet(parent));
        }
        mLock.readLock().unlock();
        return allBodies;
    }

    /**
     * Modifies body, sets ID and birth time
     */
    @Override
    public void add(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to add body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.addCelestialBody(body);
        super.add(body);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to update body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.updateCelestialBody(body);
        super.update(body);
        mLock.writeLock().unlock();
    }
}
