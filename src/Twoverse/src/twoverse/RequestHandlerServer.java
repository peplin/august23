/**
 * Twoverse Request Handler Server
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

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
import twoverse.object.Link;
import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

/**
 * This class is the server side implementation of the Twoverse Public API.
 * 
 * The RequestHandlerServer accepts incoming Apache XML-RPC requests from the
 * Twoverse client.
 * 
 * The server side can support more than one session at the same time.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
@SuppressWarnings("serial")
public class RequestHandlerServer extends XmlRpcServlet implements
        TwoversePublicApi {
    private static ObjectManagerServer sObjectManager;
    private static SessionManager sSessionManager;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerServer.class.getName());
    private static HashMap<String, Boolean> sMethodAuthorization =
            new HashMap<String, Boolean>();

    /**
     * Construct a default RequestHandlerServer. An XML-RPC Servlet must have a
     * default constructor and must be stateless. The servlet server uses the
     * same instance to handle all incoming requests.
     * 
     * For that reason, all of the attributes are static and this class is
     * somewhat dangerous when not used properly. Make sure the call the init()
     * function once on this class!
     */
    public RequestHandlerServer() {
    }

    /**
     * Initialize the static attributes of this servlet.
     * 
     * This class MUST be called once before it can properly accept and handle
     * XML-RPC requests.
     * 
     * This method also initializes the list of functions available to XML-RPC
     * clients. Normally, this list can be read dynamically by the clients, but
     * since we have some functions that do NOT require authentication, we need
     * to keep a list here to check whether or not to require a proper session.
     * 
     * ALL new API functions must be added to this list and marked "true" if
     * they require authentication.
     * 
     * @param objectManager
     *            refernece to the server's object manager
     * @param sessionManager
     *            reference to the server's session manager
     */
    public static void init(ObjectManagerServer objectManager,
            SessionManager sessionManager) {
        sObjectManager = objectManager;
        sSessionManager = sessionManager;

        sMethodAuthorization.put("RequestHandlerServer.login", false);
        sMethodAuthorization.put("RequestHandlerServer.logout", true);
        sMethodAuthorization.put("RequestHandlerServer.createAccount", false);
        sMethodAuthorization.put("RequestHandlerServer.deleteAccount", true);
        sMethodAuthorization.put("RequestHandlerServer.add", true);
        sMethodAuthorization.put("RequestHandlerServer.update", true);
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

    public CelestialBody add(CelestialBody body)
            throws UnhandledCelestialBodyException {
        sLogger.log(Level.INFO, "Attempting to add body: " + body);
        sObjectManager.add(body);
        return body;
    }

    public CelestialBody update(CelestialBody body) {
        sLogger.log(Level.INFO, "Attempting to update body: " + body);
        sObjectManager.update(body);
        return body;
    }

    public Link add(Link link) {
        sLogger.log(Level.INFO, "Attempting to add link: " + link);
        sObjectManager.add(link);
        return link;
    }

    /**
     * Returns the hashed password for the user with the provided username.
     * 
     * TODO this is not safe - will get a null pointer exception if the username
     * doesn't exist. Need to handle unknown username.
     * 
     * @param username
     *            requesting this user's hashed password
     * @return hashed password for the user
     */
    public String getHashedPassword(String username)
            throws UnknownUserException {
        sLogger.log(Level.INFO,
                "Attemping to get hashed password for username: " + username);
        return sSessionManager.getUser(username).getHashedPassword();
    }

    /**
     * Check that a user exists, confirm the password is correct. If so, create
     * a new session and return true to the client.
     * 
     * @param username
     *            username to look up
     * @param hashedPassword
     *            candidate hashed password
     * @return true if username and hashed password candidate are valid
     */
    private boolean isAuthenticated(String username, String hashedPassword) {
        sLogger.log(Level.FINE,
                "RPC request request authentication for username: " + username
                        + " and hashedPassword: " + hashedPassword);
        return sSessionManager.isLoggedIn(username, hashedPassword);
    }

    /**
     * Sets the XML-RPC authentication handler so we can require authenticated
     * clients.
     * 
     * If the client is calling an authenitcated function (defined "true" in the
     * sMethodAuthorization map) and the provided username/password are valid,
     * the request is allowed to proceed.
     */
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
