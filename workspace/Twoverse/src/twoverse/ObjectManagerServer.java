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
        while (true) {
            publishFeed();
            try {
                sleep(5000);
            } catch (InterruptedException e) {}
        }
    }

    public void publishFeed() {
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
        mGalaxies.putAll(mDatabase.getGalaxies());
        mPlanetarySystems.putAll(mDatabase.getPlanetarySystems());
        mManmadeBodies.putAll(mDatabase.getManmadeBodies());
    }

    /**
     * Modifies galaxy, sets ID and birth time
     */
    public synchronized void add(Galaxy galaxy) {
        super.add(galaxy);
        mDatabase.add(galaxy);
    }
    
    /**
     * Modifies system, sets ID and birth time
     */
    public synchronized void add(PlanetarySystem system) {
        super.add(system);
        mDatabase.add(system);
    }

    /**
     * Modifies manmadeBody, sets ID and birth time
     */
    public synchronized void add(ManmadeBody manmadeBody) {
        mManmadeBodies.put(manmadeBody.getId(), manmadeBody);
        mDatabase.add(manmadeBody);
    }
    
}
