package twoverse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jbcrypt.BCrypt;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

public class RequestHandlerClient implements TwoversePublicApi {
    private ObjectManagerClient mObjectManager;
    private Session mSession;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
    XmlRpcClientConfigImpl mXmlRpcConfig;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerClient.class.getName());

    public RequestHandlerClient(ObjectManagerClient objectManager) {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                        "twoverse/conf/RequestHandlerClient.properties"));
        } catch (IOException e) {

        }

        mXmlRpcConfig = new XmlRpcClientConfigImpl();
        try {
            mXmlRpcConfig.setServerURL(new URL(mConfigFile
                    .getProperty("XMLRPCSERVER")));
        } catch (MalformedURLException e) {
            sLogger.log(Level.WARNING,
                "Unable to parse URL for XML-RPC server: "
                    + mConfigFile.getProperty("XMLRPCSERVER"), e);
        }

        mXmlRpcConfig.setEnabledForExtensions(true);
        mXmlRpcConfig.setConnectionTimeout(60 * 1000);
        mXmlRpcConfig.setReplyTimeout(60 * 1000);
        // TODO need to add user/password to all request configs
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(mXmlRpcConfig);
    }

    private void setAuthentication(String username, String hashedPassword) {
        mXmlRpcConfig.setBasicUserName(username);
        mXmlRpcConfig.setBasicPassword(hashedPassword);
    }

    public boolean login(String username, String plaintextPassword) {
        Object[] parameters = new Object[] { username };
        try {
            String actualHash =
                    String.valueOf(mXmlRpcClient.execute(
                        "RequestHandlerServer.getHashedPassword", parameters));
            if(BCrypt.checkpw(plaintextPassword, actualHash)) {
                setAuthentication(username, actualHash);
                return true;
            }
        } catch (XmlRpcException e) {
            sLogger.log(Level.INFO, "Unknown user " + username, e);
            return false;
        }
        return false;
    }

    // TODO this doesn't really need the arguments, but it's in the API
    public void logout(String username, int session) {
        Object[] parameters = new Object[] { username, mSession.getId() };
        try {
            mXmlRpcClient.execute("RequestHandlerServer.logout", parameters);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC logout", e);
        }
    }

    public void changeName(int objectId, String newName) {

    }

    // TODO do these also need to add to ObjectManager? Does OM have a ref to
    // the request handler in the same way the ObjectManagerServer has a
    // reference
    // to the database? maybe not, since client can exist when not logged in
    @Override
    public Galaxy addGalaxy(Galaxy galaxy) {
        try {
            Object[] parameters = new Object[] { galaxy };
            Galaxy returnedGalaxy =
                    (Galaxy) mXmlRpcClient.execute(
                        "RequestHandlerServer.addGalaxy", parameters);
            galaxy.setId(returnedGalaxy.getId());
            galaxy.setBirthTime(returnedGalaxy.getBirthTime());
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
        return galaxy;
    }

    @Override
    public ManmadeBody addManmadeBody(ManmadeBody body) {
        try {
            Object[] parameters = new Object[] { body };
            ManmadeBody returnedBody =
                    (ManmadeBody) mXmlRpcClient.execute(
                        "RequestHandlerServer.addManmadeBody", parameters);
            body.setId(returnedBody.getId());
            body.setBirthTime(returnedBody.getBirthTime());
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
        return body;
    }

    @Override
    public PlanetarySystem addPlanetarySystem(PlanetarySystem system) {
        try {
            Object[] parameters = new Object[] { system };
            PlanetarySystem returnedSystem =
                    (PlanetarySystem) mXmlRpcClient.execute(
                        "RequestHandlerServer.addPlanetarySystem", parameters);
            system.setId(returnedSystem.getId());
            system.setBirthTime(returnedSystem.getBirthTime());
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
        return system;
    }

    @Override
    public int createAccount(User user) {
        Object[] parameters =
                new Object[] { user.getUsername(), user.getHashedPassword(),
                    user.getEmail(), user.getPhone() };
        try {
            int newId =
                    (Integer) mXmlRpcClient.execute(
                        "RequestHandlerServer.createAccount", parameters);
            user.setId(newId);
            return newId;
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC logout", e);
        }
        return -1;
    }

}
