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

public class RequestHandlerClient implements TwoversePublicApi {
    private Session mSession;
    private XmlRpcClient mXmlRpcClient;
    private String mServerIp;
    XmlRpcClientConfigImpl mXmlRpcConfig;
    private static Logger sLogger =
            Logger.getLogger(RequestHandlerClient.class.getName());

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

    private void setAuthentication(String username, String hashedPassword) {
        sLogger.log(Level.INFO, "Setting authentication to username: "
                + username + " and hashedPassword: " + hashedPassword);
        mXmlRpcConfig.setBasicUserName(username);
        mXmlRpcConfig.setBasicPassword(hashedPassword);
    }

    /*
     * @param user must already have correctly hashed password candidate
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

    private void clearAuthentication() {
        mSession = null;
        mXmlRpcConfig.setBasicUserName("");
        mXmlRpcConfig.setBasicPassword("");
        sLogger.log(Level.INFO, "Cleared authentication");
    }

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

    public void changeName(Session session, int objectId, String newName) {
        Object[] parameters = new Object[] { mSession, objectId, newName };
        try {
            sLogger.log(Level.INFO, "Attempting to change name of objectId: "
                    + objectId + " to " + newName);
            mXmlRpcClient.execute("RequestHandlerServer.changeName", parameters);
        } catch(XmlRpcException e) {
            sLogger.log(Level.WARNING, "Unable to execute RPC changeName", e);
        }
    }

    public CelestialBody add(CelestialBody body) {
        sLogger.log(Level.INFO, "Seting owner of body: " + body + " to user: "
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
