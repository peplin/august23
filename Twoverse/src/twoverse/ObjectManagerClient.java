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
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.Planet;
import twoverse.object.PlanetarySystem;
import twoverse.object.applet.AppletGalaxy;
import twoverse.object.applet.AppletManmadeBody;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.object.applet.AppletPlanet;
import twoverse.object.applet.AppletPlanetarySystem;

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
        Collection<Galaxy> galaxies = mGalaxies.values();
        for (Galaxy galaxy : galaxies) {
            allBodies.add(new AppletGalaxy(parent, galaxy));
        }
        Collection<PlanetarySystem> systems = mPlanetarySystems.values();
        for (PlanetarySystem system : systems) {
            allBodies.add(new AppletPlanetarySystem(parent, system));
        }
        Collection<Planet> planets = mPlanets.values();
        for (Planet planet : planets) {
            allBodies.add(new AppletPlanet(parent, planet));
        }
        Collection<ManmadeBody> manmadeBodies = mManmadeBodies.values();
        for (ManmadeBody body : manmadeBodies) {
            allBodies.add(new AppletManmadeBody(parent, body));
        }
        mLock.readLock().unlock();
        return allBodies;
    }

    /**
     * Modifies galaxy, sets ID and birth time
     */
    @Override
    public void add(Galaxy galaxy) {
        sLogger.log(Level.INFO, "Attemping to add galaxy: " + galaxy);
        mLock.writeLock().lock();
        mRequestHandler.addGalaxy(galaxy);
        super.add(galaxy);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies system, sets ID and birth time
     */
    @Override
    public void add(PlanetarySystem system) {
        sLogger.log(Level.INFO, "Attemping to add planetary system: " + system);
        mLock.writeLock().lock();
        mRequestHandler.addPlanetarySystem(system);
        super.add(system);
        mLock.writeLock().unlock();
    }

    @Override
    public void add(Planet planet) {
        sLogger.log(Level.INFO, "Attemping to add planet: " + planet);
        mLock.writeLock().lock();
        mRequestHandler.addPlanet(planet);
        super.add(planet);
        mLock.writeLock().unlock();
    }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    @Override
    public void add(ManmadeBody manmadeBody) {
        sLogger.log(Level.INFO, "Attemping to add manmade body: " + manmadeBody);
        mLock.writeLock().lock();
        mRequestHandler.addManmadeBody(manmadeBody);
        super.add(manmadeBody);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(Galaxy galaxy) {
        sLogger.log(Level.INFO, "Attemping to update galaxy: " + galaxy);
        mLock.writeLock().lock();
        // mRequestHandler.updateGalaxy(galaxy); TODO
        super.update(galaxy);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(PlanetarySystem system) {
        sLogger.log(Level.INFO, "Attemping to update planetary system: "
                + system);
        mLock.writeLock().lock();
        // mRequestHandler.updatePlanetarySystem(system);
        super.update(system);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(Planet planet) {
        sLogger.log(Level.INFO, "Attemping to update planet: " + planet);
        mLock.writeLock().lock();
        // mRequestHandler.updatePlanet(system);
        super.update(planet);
        mLock.writeLock().unlock();
    }

    @Override
    public void update(ManmadeBody manmadeBody) {
        sLogger.log(Level.INFO, "Attemping to update manmade body: "
                + manmadeBody);
        mLock.writeLock().lock();
        // mRequestHandler.updateManmadeBody(manmadeBody);
        super.update(manmadeBody);
        mLock.writeLock().unlock();
    }

}
