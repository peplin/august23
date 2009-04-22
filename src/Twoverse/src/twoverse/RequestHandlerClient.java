/**
 * Twoverse Request Handler Client
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import jbcrypt.BCrypt;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.util.Session;
import twoverse.util.User;

/**
 * This class is the client side implementation of the Twoverse Public API.
 * 
 * The RequestHandlerClient uses Apache XML-RPC to communicate with the Twoverse
 * server.
 * 
 * A client should generally use this class only to log into the system. The
 * ObjectManagerClient should be used to modify the universe, as that then uses
 * the requst handler to update the server.
 * 
 * This should probably be changed in the future so that a client need only to
 * worry about one class.
 * 
 * The request handler can only have one active session at a time.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class RequestHandlerClient implements TwoversePublicApi {
    private Session mSession;
    private XmlRpcClient mXmlRpcClient;
    private String mServerIp;
    XmlRpcClientConfigImpl mXmlRpcConfig;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerClient.class.getName());

    private void setAuthentication(String username, String hashedPassword) {
        sLogger.log(Level.INFO, "Setting authentication to username: "
                + username + " and hashedPassword: " + hashedPassword);
        mXmlRpcConfig.setBasicUserName(username);
        mXmlRpcConfig.setBasicPassword(hashedPassword);
    }

    /**
     * @param user
     *            hashed password candidate must be set
     */
    private Session login(User user) {
        Object[] parameters = new Object[] { user };
        try {
            sLogger.log(Level.INFO, "Attempting to login with user " + user);
            return (Session) (mXmlRpcClient.execute("RequestHandlerServer.login",
                    parameters));
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unknown user " + user, e);
            return null;
        }
    }

    private void clearAuthentication() {
        mSession = null;
        mXmlRpcConfig.setBasicUserName("");
        mXmlRpcConfig.setBasicPassword("");
        sLogger.log(Level.INFO, "Cleared authentication");
    }

    /**
     * Create a new RequestHandlerClient and attempt to connect to the XML-RPC
     * server.
     * 
     * @param serverIp
     *            the IP address of the XML-RPC server
     */
    public RequestHandlerClient(String serverIp) {
        mServerIp = serverIp;
        mXmlRpcConfig = new XmlRpcClientConfigImpl();
        try {
            mXmlRpcConfig.setServerURL(new URL(mServerIp));
        } catch(MalformedURLException e) {
            sLogger.log(Level.SEVERE,
                    "Unable to parse URL for XML-RPC server: " + mServerIp,
                    e);
        }

        mXmlRpcConfig.setEnabledForExtensions(true);
        mXmlRpcConfig.setConnectionTimeout(60 * 1000);
        mXmlRpcConfig.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(mXmlRpcConfig);
    }

    /**
     * Given a plaintext password and a username, confirms with server and
     * creates a new session if valid.
     * 
     * This implementation tries to avoid sending the plaintext password over
     * the wire, although at some point that may be inevitable.
     * 
     * TODO Security needs to be entirely rethough and reimplemented.
     * 
     * @param username
     *            username of user attmempting to login
     * @param plaintextPassword
     *            the candidate plaintext password to check
     * @return a new session if the user is valid, null otherwise
     */
    public Session login(String username, String plaintextPassword) {
        Object[] parameters = new Object[] { username };
        try {
            String actualHash =
                    String.valueOf(mXmlRpcClient.execute("RequestHandlerServer.getHashedPassword",
                            parameters));
            User candidateUser = new User(0, username, "", "", 0);
            sLogger.log(Level.INFO, "Attemping login for user: "
                    + candidateUser);
            candidateUser.setHashedPassword(BCrypt.hashpw(plaintextPassword,
                    actualHash));
            mSession = login(candidateUser);
            if(mSession != null) {
                sLogger.log(Level.INFO, "Logged in with session: " + mSession);
                setAuthentication(username, actualHash);
                return mSession;
            }
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to login with username: "
                    + username, e);
            return null;
        }
        return null;
    }

    /**
     * Logout from the current session, becoming unauthenticated.
     * 
     * If there is no valid session, nothing happens.
     */
    public void logout() {
        if(mSession != null) {
            Object[] parameters = new Object[] { mSession };
            try {
                sLogger.log(Level.INFO, "Attempting to logout with session: "
                        + mSession);
                mXmlRpcClient.execute("RequestHandlerServer.logout", parameters);
            } catch(XmlRpcException e) {
                sLogger.log(Level.WARNING, "Unable to execute RPC logout", e);
            }
        } else {
            sLogger.log(Level.WARNING,
                    "Attempted to logout without a valid session");
        }
    }

    /**
     * Creates a new account on the server.
     * 
     * @param user
     *            the user to create an account for. The ID is set after
     *            returning from this method.
     * @return the ID of the new user, 0 if unable to create
     */
    public int createAccount(User user) {
        Object[] parameters = new Object[] { user };
        try {
            sLogger.log(Level.INFO, "Attempting to create account for user: "
                    + user);
            int newId =
                    (Integer) mXmlRpcClient.execute("RequestHandlerServer.createAccount",
                            parameters);
            user.setId(newId);
            sLogger.log(Level.INFO, "User created is: " + user);
            return newId;
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC createAccount", e);
        }
        return 0;
    }

    /**
     * Deletes the account for the current session from the server.
     * 
     * If there is no current, valid session, nothing happens.
     */
    public void deleteAccount() {
        if(mSession != null) {
            Object[] parameters = new Object[] { mSession };
            try {
                sLogger.log(Level.INFO,
                        "Attempting to delete account for session: " + mSession);
                mXmlRpcClient.execute("RequestHandlerServer.deleteAccount",
                        parameters);
                clearAuthentication();
            } catch(XmlRpcException e) {
                sLogger.log(Level.WARNING,
                        "Unable to execute RPC deleteAccount",
                        e);
            }
        } else {
            sLogger.log(Level.WARNING,
                    "Attempted ot delete account without a valid session");
        }
    }

    /**
     * Adds a new object to the server's universe.
     * 
     * Sets the owner of the body to the current session's user.
     * 
     * @param body
     *            the object to add
     * @return the object from the server with a valid ID
     */
    public CelestialBody add(CelestialBody body) {
        sLogger.log(Level.INFO, "Setting owner of body: " + body + " to user: "
                + mSession.getUser());
        body.setOwnerId(mSession.getUser().getId());
        try {
            Object[] parameters = new Object[] { body };
            sLogger.log(Level.INFO, "Attempting to add body: " + body);
            CelestialBody returnedBody =
                    (CelestialBody) mXmlRpcClient.execute("RequestHandlerServer.add",
                            parameters);
            body.setId(returnedBody.getId());
            body.setBirthTime(returnedBody.getBirthTime());
            sLogger.log(Level.INFO, "Body returned from add is: " + body);
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC add", e);
        }
        return body;
    }

    /**
     * Updates an object in the server's universe.
     * 
     * @param body
     *            the object to update
     * @return the object returned from the server - should be identical
     */
    public CelestialBody update(CelestialBody body) {
        try {
            Object[] parameters = new Object[] { body };
            sLogger.log(Level.INFO, "Attempting to update body: " + body);
            mXmlRpcClient.execute("RequestHandlerServer.update", parameters);
            sLogger.log(Level.INFO, "Body returned from update is: " + body);
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC update", e);
        }
        return body;
    }

    /**
     * Adds a new link to the server's universe.
     * 
     * TODO should probably confirm the ends of the link exist, otherwise we
     * just get an exception. In that case, maybe it's okay.
     * 
     * @param link
     *            the link to add
     * @return the object from the server with a valid ID
     */
    public Link add(Link link) {
        try {
            Object[] parameters = new Object[] { link };
            sLogger.log(Level.INFO, "Attempting to add link: " + link);
            Link returnedLink =
                    (Link) mXmlRpcClient.execute("RequestHandlerServer.add",
                            parameters);
            link.setId(returnedLink.getId());
            sLogger.log(Level.INFO, "Link returned from add is: " + link);
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC add", e);
        }
        return link;
    }
}
