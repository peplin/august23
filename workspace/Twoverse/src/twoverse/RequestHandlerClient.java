package twoverse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.Session;

//TODO need to add user/password to all request configs
public class RequestHandlerClient implements TwoversePublicApi {
    private ObjectManagerClient mObjectManager;
    private Session mSession;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
    private static Logger sLogger = Logger.getLogger(RequestHandlerClient.class
            .getName());

    public RequestHandlerClient(ObjectManagerClient objectManager) {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                            "twoverse/conf/RequestHandlerClient.properties"));
        } catch (IOException e) {

        }

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config
                    .setServerURL(new URL(mConfigFile
                            .getProperty("XMLRPCSERVER")));
        } catch (MalformedURLException e) {
            sLogger.log(Level.WARNING,
                    "Unable to parse URL for XML-RPC server: "
                            + mConfigFile.getProperty("XMLRPCSERVER"), e);
        }

        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(config);
    }

    public void logout(int session) {
        // logout
        Object[] parameters = new Object[] { mSession.getUser().getUsername(),
                mSession.getId() };
        try {
            mXmlRpcClient.execute("SessionManager.logout", parameters);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC logout", e);
        }
    }

    public void changeName(int objectId, String newName) {

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
            int newId = (Integer) mXmlRpcClient.execute("RequestHandler.add",
                    parameters);
            body.setId(newId);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public int add(Galaxy galaxy) {
        addXmlRpc(galaxy);
        return galaxy.getId();
    }

    @Override
    public int add(ManmadeBody body) {
        addXmlRpc(body);
        return body.getId();
    }

    @Override
    public int add(PlanetarySystem system) {
        addXmlRpc(system);
        return system.getId();

    }

    @Override
    public int createAccount(String username, String hashedPassword,
            String email, String phone) {
        // TODO Auto-generated method stub
        return 0;
    }

}
