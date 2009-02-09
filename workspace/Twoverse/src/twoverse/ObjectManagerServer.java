package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Level;

import nu.xom.Document;
import nu.xom.Element;
import twoverse.Database;
import twoverse.util.User;

public class ObjectManagerServer extends ObjectManager {
    Database mDatabase;

    public ObjectManagerServer(Database database) {
        super();
        mDatabase = database;
    }

    public void run() {
        //TODO continuously update feed

    }

    public void publishFeed() {
        Element root = new Element("root");
        root.appendChild("Hello World!");
        Document doc = new Document(root);
        try {
            FileOutputStream xmlFeedFile = new FileOutputStream("feed.xml");
            OutputStream bufferedXmlFeedFile = new BufferedOutputStream(
                    xmlFeedFile);
            OutputStreamWriter outStream = new OutputStreamWriter(
                    bufferedXmlFeedFile, "8859_1");
            outStream.write(doc.toXML());
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void initialize(HashMap<Integer, User> users) {
        // how to find parent object? maybe run obj.linkParent(map[id]) after
        // coming back from parseCelestialBodies

        mGalaxies.putAll(mDatabase.getGalaxies(users));
        mPlanetarySystems.putAll(mDatabase.getPlanetarySystems(users));
        mManmadeBodies.putAll(mDatabase.getManmadeBodies(users));

    }

}
