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
import twoverse.Database;

public class ObjectManagerClient extends ObjectManager {

    public ObjectManagerClient(Database database) {
        super(database);
        mParser = new Builder();

        try {
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                            "../config/ObjectManagerClient.properties"));
        } catch (IOException e) {

        }

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config
                    .setServerURL(new URL(mConfigFile
                            .getProperty("XMLRPCSERVER")));
        } catch (MalformedURLException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }

        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(config);
    }

    /**
     * These objects all have a -1 or null ID - the new iD is set by the
     * database, and returned by the function call. We save it to the object
     * itself, and return nothing.
     * 
     * @param parameters
     */
    private void addXmlRpc(CelestialBody body) {
        try {
            Object[] parameters = new Object[] { body };
            int newId = (Integer) mXmlRpcClient.execute("ObjectManager.add",
                    parameters);
            body.setId(newId);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public int add(Galaxy newGalaxy) {
        super.add(newGalaxy);
        addXmlRpc(newGalaxy);
        return 0;
    }

    public int add(PlanetarySystem newSystem) {
        super.add(newSystem);
        addXmlRpc(newSystem);
        return 0;
    }

    public int add(ManmadeBody newManmadeBody) {
        super.add(newManmadeBody);
        addXmlRpc(newManmadeBody);
        return 0;
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

    private Builder mParser;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
}
