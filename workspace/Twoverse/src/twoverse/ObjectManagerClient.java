package twoverse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.Database;

public class ObjectManagerClient extends ObjectManager {

    public ObjectManagerClient(Database database) {
        super(database);
        // TODO Auto-generated constructor stub
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(config);
    }

    public void run() {

    }

    // TODO need to confirm they don't exist, since we're not going to let the
    // client modify anything for the time being
    // TODO will overloaded method get picked up with serialized object?
    // TODO any way stop the duplication?

    private void updateXmlRpc(Object[] parameters) {
        try {
            mXmlRpcClient.execute("ObjectManager.update", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void update(Galaxy newGalaxy) {
        mGalaxies.put(newGalaxy.getId(), newGalaxy);
        Object[] parameters = new Object[] { newGalaxy };
        updateXmlRpc(parameters);
    }

    public void update(PlanetarySystem newSystem) {
        mPlanetarySystems.put(newSystem.getId(), newSystem);
        Object[] parameters = new Object[] { newSystem };
        updateXmlRpc(parameters);
    }

    public void update(ManmadeBody newManmadeBody) {
        mManmadeBodies.put(newManmadeBody.getId(), newManmadeBody);
        Object[] parameters = new Object[] { newManmadeBody };
        updateXmlRpc(parameters);
    }

    // TODO FIX ALL STRING COMPARES!!!!
    public void pullFeed() {
        try {
            Document doc = mParser.build(mConfigFile.getProperty("FEED"));
            Elements children = doc.getRootElement().getChildElements();
            // TODO design feed, figure out how to parse it
        } catch (ParsingException ex) {
            System.err.println("Feed is malformed");
        } catch (IOException ex) {
            System.err.println("Unable to connect to feed");
        }
    }

    private Builder mParser;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
}
