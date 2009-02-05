package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import nu.xom.Document;
import nu.xom.Element;
import twoverse.util.Database;

public class ObjectManagerServer extends ObjectManager {

    public ObjectManagerServer(Database database) {
        super(database);
        // TODO Auto-generated constructor stub

    }

    public void run() {

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
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
