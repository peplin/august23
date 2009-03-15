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

import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.SessionManager.ExistingUserException;
import twoverse.SessionManager.UnknownUserException;
import twoverse.object.CelestialBody;
import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

@SuppressWarnings("serial")
public class RequestHandlerServer extends XmlRpcServlet implements
        TwoversePublicApi {
    private static ObjectManagerServer sObjectManager;
    private static SessionManager sSessionManager;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerServer.class.getName());
    private static HashMap<String, Boolean> sMethodAuthorization =
            new HashMap<String, Boolean>();

    public RequestHandlerServer() {
    }

    public static void init(ObjectManagerServer objectManager,
            SessionManager sessionManager) {
        sObjectManager = objectManager;
        sSessionManager = sessionManager;

        sMethodAuthorization.put("RequestHandlerServer.login", false);
        sMethodAuthorization.put("RequestHandlerServer.logout", true);
        sMethodAuthorization.put("RequestHandlerServer.createAccount", false);
        sMethodAuthorization.put("RequestHandlerServer.deleteAccount", true);
        sMethodAuthorization.put("RequestHandlerServer.changeName", true);
        sMethodAuthorization.put("RequestHandlerServer.addGalaxy", true);
        sMethodAuthorization.put("RequestHandlerServer.addManmadeBody", true);
        sMethodAuthorization.put("RequestHandlerServer.addPlanetarySystem",
                true);
        sMethodAuthorization.put("RequestHandlerServer.addCelestialBody", true);
        sMethodAuthorization.put("RequestHandlerServer.addPlanet", true);
        sMethodAuthorization.put("RequestHandlerServer.getHashedPassword",
                false);
        sLogger.log(Level.CONFIG, "Method authorization configuration is: "
                + sMethodAuthorization);
    }

    public Session login(User user) throws UnsetPasswordException {
        sLogger.log(Level.INFO, "Attempting to login with user: " + user);
        return sSessionManager.login(user);
    }

    public int logout(Session session) {
        sLogger.log(Level.INFO, "Attempting to logout of session: " + session);
        sSessionManager.logout(session);
        return 0;
    }

    public int createAccount(User user) throws ExistingUserException,
            UnsetPasswordException {
        sLogger.log(Level.INFO, "Attempting to create account for user: "
                + user);
        return sSessionManager.createAccount(user);
    }

    public int deleteAccount(Session session) {
        sLogger.log(Level.INFO, "Attempting to delete account from session: "
                + session);
        sSessionManager.deleteAccount(session);
        return 0;
    }

    public void changeName(Session session, int objectId, String newName) {
        try {
            CelestialBody body = sObjectManager.getCelestialBody(objectId);
            sLogger.log(Level.INFO, "Attempting to change name of object: "
                    + body + " to: " + newName + " from session: " + session);
            if(body != null
                    && isAuthenticated(session.getUser().getUsername(),
                            session.getUser().getHashedPassword())
                    && session.getUser().getId() == body.getOwnerId()) {
                body.setName(newName);
            } else {
                sLogger.log(Level.WARNING,
                        "Object doesn't exist or session is not authenticated as the owner");
            }
        } catch(UnhandledCelestialBodyException e) {
            sLogger.log(Level.WARNING, "Unknown type of CelestialBody", e);
        }
    }

    public CelestialBody addCelestialBody(CelestialBody body) {
        sLogger.log(Level.INFO, "Attempting to add body: " + body);
        sObjectManager.add(body);
        return body;
    }

    @Override
    public CelestialBody updateCelestialBody(CelestialBody body) {
        sLogger.log(Level.INFO, "Attempting to update body: " + body);
        sObjectManager.update(body);
        return body;
    }

    public String getHashedPassword(String username)
            throws UnknownUserException {
        sLogger.log(Level.INFO,
                "Attemping to get hashed password for username: " + username);
        return sSessionManager.getUser(username).getHashedPassword();
    }

    /**
     * Check that a user exists, confirm the password is correct. If so, create
     * a new session and return true to the client.
     */
    private boolean isAuthenticated(String username, String hashedPassword) {
        sLogger.log(Level.FINE,
                "RPC request request authentication for username: " + username
                        + " and hashedPassword: " + hashedPassword);
        return sSessionManager.isLoggedIn(username, hashedPassword);
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        PropertyHandlerMapping mapping =
                (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
                new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
                    public boolean isAuthorized(XmlRpcRequest pRequest)
                            throws XmlRpcException {
                        if(sMethodAuthorization == null) {
                            throw new XmlRpcException("Missing authentication information for method: "
                                    + pRequest.getMethodName());
                        }
                        if(sMethodAuthorization.get(pRequest.getMethodName())) {
                            XmlRpcHttpRequestConfig config =
                                    (XmlRpcHttpRequestConfig) pRequest.getConfig();
                            return isAuthenticated(config.getBasicUserName(),
                                    config.getBasicPassword());
                        } else {
                            return true;
                        }
                    };
                };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }
}
