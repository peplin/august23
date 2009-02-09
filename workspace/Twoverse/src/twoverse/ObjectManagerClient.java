package twoverse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

public class ObjectManagerClient extends ObjectManager {
    private Builder mParser;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;

    public ObjectManagerClient() {
        super();
        mParser = new Builder();

        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                            "twoverse/conf/ObjectManagerClient.properties"));
        } catch (IOException e) {

        }
    }

    public void pullFeed() {
        try {
            Document doc = mParser.build(mConfigFile.getProperty("FEED"));
            Elements children = doc.getRootElement().getChildElements();
            // TODO design feed, figure out how to parse it
        } catch (ParsingException e) {
            sLogger.log(Level.WARNING, "Feed may be malformed", e);
        } catch (IOException e) {
            sLogger.log(Level.WARNING, "Unable to connect to feed", e);
        }
    }

}
