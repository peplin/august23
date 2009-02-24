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

    public ObjectManagerClient() {
        super();
        mParser = new Builder();
    }

    @Override
    public void run() {
        while (true) {
            pullFeed();
            try {
                // TODO any way to check if it's actually updated before
                // polling?
                // plus, this isn't really CPU efficient
                sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void pullFeed() {
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

}
