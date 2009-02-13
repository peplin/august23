package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;

public class ObjectManagerServer extends ObjectManager {
    Database mDatabase;

    public ObjectManagerServer(Database database) {
        super();
        mDatabase = database;
    }

    @Override
    public void run() {
        // TODO continuously update feed
        while (true) {
            publishFeed();
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void publishFeed() {
        // TODO new object types need to be added here
        Element root = new Element("August");
        Element galaxies = new Element("Galaxies");
        Element planetarySystems = new Element("PlanetarySystems");
        Element manmadeBodies = new Element("ManmadeBodies");

        root.appendChild(galaxies);
        root.appendChild(planetarySystems);
        root.appendChild(manmadeBodies);

        {
            Iterator<Galaxy> it = mGalaxies.values().iterator();
            while (it.hasNext()) {
                galaxies.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<PlanetarySystem> it =
                    mPlanetarySystems.values().iterator();
            while (it.hasNext()) {
                planetarySystems.appendChild(it.next().toXmlElement());
            }
        }
        {
            Iterator<ManmadeBody> it = mManmadeBodies.values().iterator();
            while (it.hasNext()) {
                manmadeBodies.appendChild(it.next().toXmlElement());
            }
        }
        Document doc = new Document(root);
        try {
            FileOutputStream xmlFeedFile = new FileOutputStream("feed.xml");
            OutputStream bufferedXmlFeedFile =
                    new BufferedOutputStream(xmlFeedFile);
            Serializer serializer =
                    new Serializer(bufferedXmlFeedFile, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);

            // TODO confirm the serializer works, and doesn't waste space
            /*
             * OutputStreamWriter outStream = new OutputStreamWriter(
             * bufferedXmlFeedFile, "8859_1"); outStream.write(doc.toXML());
             * outStream.flush(); outStream.close();
             */
        } catch (IOException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void initialize(HashMap<Integer, User> users) {
        // TODO how to find parent object? maybe run obj.linkParent(map[id])
        // after
        // coming back from parseCelestialBodies...Do we need parent beyond ID?
        // easy to find in HashMap with id...

        mGalaxies.putAll(mDatabase.getGalaxies(users));
        mPlanetarySystems.putAll(mDatabase.getPlanetarySystems(users));
        mManmadeBodies.putAll(mDatabase.getManmadeBodies(users));

    }

}
