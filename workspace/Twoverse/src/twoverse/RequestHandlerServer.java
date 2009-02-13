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

import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

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
    public void logout(int session) {
        mSessionManager.logout(session);
    }

    @Override
    public int createAccount(String username, String hashedPassword,
                             String salt, String email, String phone) {
        return mSessionManager.createAccount(username, hashedPassword, salt,
            email, phone, 0);
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
    public int add(Galaxy galaxy) {
        return mObjectManager.add(galaxy);
    }

    @Override
    public int add(ManmadeBody body) {
        return mObjectManager.add(body);
    }

    @Override
    public int add(PlanetarySystem system) {
        return mObjectManager.add(system);
    }

    /**
     * Check that a user exists, confirm the password is correct. If so, create
     * a new session and return true to the client.
     */
    private boolean isAuthenticated(String username, String plaintextPassword) {
        return mSessionManager.login(username, plaintextPassword);
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
