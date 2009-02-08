package twoverse;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.object.Star;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Session;
import twoverse.util.User;

public class RequestHandlerClient implements TwoversePublicApi {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(config);
    }

    public boolean login(String username, String plaintextPassword) {
        // if good, set login time
        Object[] parameters = new Object[] { username, plaintextPassword };
        try {
            mXmlRpcClient.execute("SessionManager.login", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO check return value
        return true;
    }

    public boolean logout(String username, int session) {
        // logout
        Object[] parameters = new Object[] { mSession.getUser().getUsername(),
                mSession.getId() };
        try {
            mXmlRpcClient.execute("SessionManager.logout", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //TODO check return value
        return false;
    }

    public void refreshUser(String username, int session) {
        Object[] parameters = new Object[] { mSession.getUser().getUsername(),
                mSession.getId() };
        try {
            mXmlRpcClient.execute("SessionManager.refresh", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO double all of these, one accepts serialized object
    public void addGalaxy() {

    }

    public void addPlanetarySystem() {

    }

    public void addManmadeBody() {

    }

    public void changeName(int objectId) {

    }

    private boolean isAuthenticated(String username, String password) {
        return false;
    }

    private ObjectManagerClient mObjectManager;
    private Session mSession;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
    @Override
    public void addGalaxy(Galaxy galaxy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addGalaxy(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Color color, GalaxyShape shape) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addManmadeBody(ManmadeBody body) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addManmadeBody(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration, Color color) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPlanetarySystem(PlanetarySystem system) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addPlanetarySystem(User owner, CelestialBody parent,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Color color, Star center) {
        // TODO Auto-generated method stub
        
    }
}
