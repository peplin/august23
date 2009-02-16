package twoverse;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.InvalidUserException;
import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.SessionManager.ExistingUserException;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;

@SuppressWarnings("serial")
public class RequestHandlerServer implements TwoversePublicApi {
    private ObjectManagerServer mObjectManager;
    private SessionManager mSessionManager;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerServer.class.getName());
    private static HashMap<String, Boolean> mMethodAuthorization =
            new HashMap<String, Boolean>();

    public RequestHandlerServer() {}

    private static class RequestHandlerServerHolder {
        private final static RequestHandlerServer INSTANCE =
                new RequestHandlerServer();
    }
    
    public static RequestHandlerServer getInstance() {
        return RequestHandlerServerHolder.INSTANCE;
    }

    public void init(ObjectManagerServer objectManager,
                                SessionManager sessionManager) {
        mObjectManager = objectManager;
        mSessionManager = sessionManager;

        mMethodAuthorization.put("RequestHandlerServer.logout", true);
        mMethodAuthorization.put("RequestHandlerServer.createAccount", false);
        mMethodAuthorization.put("RequestHandlerServer.changeName", true);
        mMethodAuthorization.put("RequestHandlerServer.addGalaxy", true);
        mMethodAuthorization.put("RequestHandlerServer.addManmadeBody", true);
        mMethodAuthorization.put("RequestHandlerServer.addPlanetarySystem",
            true);
        mMethodAuthorization
                .put("RequestHandlerServer.getHashedPassword", true);
    }

    @Override
    public void logout(String username, int session) {
        mSessionManager.logout(username, session);
    }

    @Override
    public int createAccount(User user) throws ExistingUserException {
        return mSessionManager.createAccount(user);
    }

    @Override
    public void changeName(int objectId, String newName) {
        try {
            mObjectManager.getCelestialBody(objectId).setName(newName);
        } catch (UnhandledCelestialBodyException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public Galaxy addGalaxy(Galaxy galaxy) {
        mObjectManager.add(galaxy);
        return galaxy;
    }

    @Override
    public ManmadeBody addManmadeBody(ManmadeBody body) {
        mObjectManager.add(body);
        return body;
    }

    @Override
    public PlanetarySystem addPlanetarySystem(PlanetarySystem system) {
        mObjectManager.add(system);
        return system;
    }

    public String getHashedPassword(String username) {
        return mSessionManager.getUser(username).getHashedPassword();
    }
}
