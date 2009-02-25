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

import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.Session;
import twoverse.util.User;

public class RequestHandlerClient implements TwoversePublicApi {
    private Session mSession;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
    XmlRpcClientConfigImpl mXmlRpcConfig;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerClient.class.getName());

    public RequestHandlerClient() {
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
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(mXmlRpcConfig);
    }

    private void setAuthentication(String username, String hashedPassword) {
        mXmlRpcConfig.setBasicUserName(username);
        mXmlRpcConfig.setBasicPassword(hashedPassword);
    }

    @Override
    /*
     * Normal client will not call this function!!
     * TODO this should be private, but API has it being public...client and
     * server differ in this regard
     * @param user must already have correctly hashed password candidate
     */
    public Session login(User user) {
        Object[] parameters = new Object[] { user };
        try {
            return (Session) (mXmlRpcClient.execute(
                "RequestHandlerServer.login", parameters));
        } catch (XmlRpcException e) {
            sLogger.log(Level.INFO, "Unknown user " + user, e);
            return null;
        }
    }

    /**
     * Used to get the correct hash salt for a candidate plaintext password and
     * login over XML-RPC
     * 
     * @param username
     * @param plaintextPassword
     * @return
     */
    public Session login(String username, String plaintextPassword) {
        Object[] parameters = new Object[] { username };
        try {
            String actualHash =
                    String.valueOf(mXmlRpcClient.execute(
                        "RequestHandlerServer.getHashedPassword", parameters));
            User candidateUser = new User(0, username, "", "", 0);
            candidateUser.setHashedPassword(BCrypt.hashpw(plaintextPassword,
                actualHash));
            mSession = login(candidateUser);
            if(mSession != null) {
                setAuthentication(username, actualHash);
                return mSession;
            }
        } catch (XmlRpcException e) {
            sLogger.log(Level.INFO, "Unknown user " + username, e);
            return null;
        }
        return null;
    }

    public void logout() {
        if(mSession != null) {
            logout(mSession);
        }
    }

    @Override
    public void logout(Session session) {
        Object[] parameters = new Object[] { session };
        try {
            mXmlRpcClient.execute("RequestHandlerServer.logout", parameters);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC logout", e);
        }
    }

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
        Object[] parameters = new Object[] { user };
        try {
            int newId =
                    (Integer) mXmlRpcClient.execute(
                        "RequestHandlerServer.createAccount", parameters);
            user.setId(newId);
            return newId;
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public void changeName(Session session, int objectId, String newName) {
        Object[] parameters = new Object[] { mSession, objectId, newName };
        try {
            mXmlRpcClient
                    .execute("RequestHandlerServer.changeName", parameters);
        } catch (XmlRpcException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

}
