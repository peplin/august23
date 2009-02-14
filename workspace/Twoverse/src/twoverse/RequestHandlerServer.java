package twoverse;

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
public class RequestHandlerServer extends XmlRpcServlet implements
        TwoversePublicApi {
    private ObjectManagerServer mObjectManager;
    private SessionManager mSessionManager;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerServer.class.getName());

    public RequestHandlerServer(ObjectManagerServer objectManager,
                                SessionManager sessionManager) {
        mObjectManager = objectManager;
        mSessionManager = sessionManager;
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

    /**
     * Check that a user exists, confirm the password is correct. If so, create
     * a new session and return true to the client. TODO this will not work.
     * Need to hash password on client, so it's not sent plaintext Need actual
     * correct hashed password in order to hash the candidate use
     * unauthenticated login method that returns the hashed actual, allowing
     * correct hash to be generated. then it can be set for the config.
     */
    private boolean isAuthenticated(String username, String hashedPassword) {
        return (mSessionManager.login(username, hashedPassword) != -1);
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        PropertyHandlerMapping mapping =
                (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
                new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
                    public boolean isAuthorized(XmlRpcRequest pRequest) {
                        XmlRpcHttpRequestConfig config =
                                (XmlRpcHttpRequestConfig) pRequest.getConfig();
                        return isAuthenticated(config.getBasicUserName(),
                            config.getBasicPassword());
                    };
                };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }

}
