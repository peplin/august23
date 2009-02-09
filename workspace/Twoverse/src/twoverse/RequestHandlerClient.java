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

    public RequestHandlerClient(ObjectManager objectManager,
            SessionManager sessionManager) {
        try {
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

    @Override
    public int addGalaxy(Galaxy galaxy) {
        // TODO Auto-generated method stub
        return galaxy.getId();
    }

    @Override
    public int addManmadeBody(ManmadeBody body) {
        // TODO Auto-generated method stub
        return body.getId();
    }

    @Override
    public int addPlanetarySystem(PlanetarySystem system) {
        // TODO Auto-generated method stub
        return system.getId();

    }

    @Override
    public int createAccount(String username, String hashedPassword,
            String email, String phone) {
        // TODO Auto-generated method stub
        return 0;
    }

}
