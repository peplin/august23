package twoverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import processing.core.PApplet;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.CelestialBody;
import twoverse.object.Star;
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

            Element universe =
                    doc.getRootElement()
                            .getFirstChildElement(mConfigFile.getProperty("CELESTIAL_BODY_TAG"));
            update(new CelestialBody(universe));

            Elements planets =
                    doc.getRootElement()
                            .getChildElements(mConfigFile.getProperty("STAR_TAG"));
            for (int i = 0; i < planets.size(); i++) {
                Star planet = new Star(planets.get(i));
                update(planet);
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
            allBodies.add(body.getAsApplet(parent));
        }
        mLock.readLock().unlock();
        return allBodies;
    }

    /**
     * Modifies body, sets ID and birth time
     * 
     * @throws UnhandledCelestialBodyException
     */
    @Override
    public void add(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to add body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.add(body);
        try {
            super.add(body);
        } catch (UnhandledCelestialBodyException e) {
            sLogger.log(Level.WARNING, "Unable to add", e);
        }
        mLock.writeLock().unlock();
    }

    @Override
    public void update(CelestialBody body) {
        sLogger.log(Level.INFO, "Attemping to update body: " + body);
        mLock.writeLock().lock();
        mRequestHandler.update(body);
        super.update(body);
        mLock.writeLock().unlock();
    }
}
