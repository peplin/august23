/**
 * Session Manager
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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

/**
 * The SessionManager handles all user account creation, updates, authentication
 * and session timeout.
 * 
 * The list of know users is loaded from the database at runtime, and any new
 * users created afterwards are flushed the database.
 * 
 * Before using any methods defined by the RequestHandlerServer, a client must
 * have created a session here with a valid account. If there is no activity for
 * an extended period of time, the session is closed by a periodic task run from
 * this class.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class SessionManager extends TimerTask {
    private HashMap<String, Session> mSessions;
    private HashMap<String, User> mUsers;
    private Database mDatabase;
    private ReentrantReadWriteLock mUsersLock;
    private ReentrantReadWriteLock mSessionsLock;
    private Properties mConfigFile;
    private static Logger sLogger =
            Logger.getLogger(SessionManager.class.getName());

    /**
     * Create a new Session Manager, storing a reference to the database.
     * 
     * All known users are loaded into the manager at this point.
     * 
     * Registers the periodic session timeout task to occur at the time
     * specified in at twoverse.conf.SessionManager.properties.
     * 
     * @param database
     *            reference to the database to use
     */
    public SessionManager(Database database) {
        try {
            // Load properties file
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/SessionManager.properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }
        mDatabase = database;
        mSessions = new HashMap<String, Session>();
        mUsers = new HashMap<String, User>();
        mUsersLock = new ReentrantReadWriteLock();
        mSessionsLock = new ReentrantReadWriteLock();
        initializeUsers();

        Timer sessionCleanupTimer = new Timer();
        sessionCleanupTimer.scheduleAtFixedRate(this,
                0,
                Long.valueOf(mConfigFile.getProperty("CLEANUP_DELAY")));
    }

    private void initializeUsers() {
        sLogger.log(Level.INFO, "Initializing user from database");
        mUsers = mDatabase.getUsers();
    }

    /**
     * /** Run any tasks scheduled for periodic execution.
     * 
     * At the moment, this only includes cleaning up timed out sessions.
     */
    @Override
    public void run() {
        sLogger.log(Level.FINE, "Cleaning up user sessions");
        cleanup();
        sLogger.log(Level.FINE, "Done cleaning up user sessions");
    }

    /**
     * Creates a new user account and stores it in the database.
     * 
     * @param user
     *            the user account to create. Must have the hashed password set.
     *            If the account already exists, nothing happens. If it does
     *            not, the ID is set.
     * @return the ID of the newly created or existing user
     */
    public int createAccount(User user) throws UnsetPasswordException {
        sLogger.log(Level.INFO, "Attempting to create account for user: "
                + user);
        if(user.getHashedPassword() == null) {
            sLogger.log(Level.WARNING, "User doesn't have password set");
            throw new User.UnsetPasswordException("User doesn't have password set");
        }
        mUsersLock.writeLock().lock();
        if(!mUsers.containsKey(user.getUsername())) {
            mDatabase.addUser(user);
            mUsers.put(user.getUsername(), user);
        } else {
            mUsersLock.writeLock().unlock();
            sLogger.log(Level.WARNING, "User " + user + " already exists");
            return mUsers.get(user.getUsername()).getId();
        }
        mUsersLock.writeLock().unlock();
        sLogger.log(Level.INFO, "Added user is: " + user);
        return user.getId();
    }

    /**
     * Deletes an account from the session manager and database. The account
     * must have an active session before the account can be deleted. This is to
     * make sure that only the account holder can delete their account, as the
     * user must be authenticated to create a session.
     * 
     * If the session is not logged in, or the account of the session is not
     * valid, nothing happens.
     * 
     * @param session
     *            the owner of this session is the account to be deleted
     */
    public void deleteAccount(Session session) {
        sLogger.log(Level.INFO, "Attempting to delete account from session: "
                + session);
        mUsersLock.writeLock().lock();
        mSessionsLock.writeLock().lock();
        if(mSessions.get(session.getUser().getUsername()).equals(session)) {
            mDatabase.deleteUser(session.getUser());
            mSessions.remove(session.getUser().getUsername());
            mUsers.remove(session.getUser().getUsername());
            sLogger.log(Level.INFO, "Account deleted for session: " + session);
        } else {
            sLogger.log(Level.WARNING, "Username "
                    + session.getUser().getUsername() + " does not exist or"
                    + "is not logged in");
        }
        mUsersLock.writeLock().unlock();
    }

    /**
     * Creates a new session if not logged in, otherwise nothing happens.
     * 
     * @return a new or existing Session, null if invalid user
     * @param user
     *            user to login with. Hashed password must be set.
     */
    public Session login(User user) throws UnsetPasswordException {
        sLogger.log(Level.INFO, "Attempting to login with user: " + user);
        mUsersLock.readLock().lock();
        mSessionsLock.writeLock().lock();
        User actualUser = mUsers.get(user.getUsername());
        sLogger.log(Level.INFO, "Found corresponding local user: " + actualUser);
        try {
            if(actualUser != null && actualUser.validate(user)) {
                Session userSession = mSessions.get(actualUser.getUsername());
                if(userSession == null) {
                    mSessions.put(actualUser.getUsername(),
                            new Session(actualUser));
                    mDatabase.updateLoginTime(actualUser);
                    sLogger.log(Level.INFO, "Created session: "
                            + mSessions.get(user.getUsername()) + " for user: "
                            + user);

                } else {
                    sLogger.log(Level.INFO, "User: " + user
                            + " already has existing session: " + userSession);
                }
                return mSessions.get(actualUser.getUsername());
            }
        } catch(UnsetPasswordException e) {
            sLogger.log(Level.WARNING,
                    "Tried to login with user with uninitialized password",
                    e);
        } finally {
            mUsersLock.readLock().unlock();
            mSessionsLock.writeLock().unlock();
        }
        return null;
    }

    /**
     * Logs a session out (destroys it) if valid.
     */
    public void logout(Session session) {
        sLogger.log(Level.INFO, "Attempting to logout from session: " + session);
        mSessionsLock.writeLock().lock();
        if(mSessions.get(session.getUser().getUsername()).equals(session)) {
            mSessions.remove(session.getUser().getUsername());
            sLogger.log(Level.INFO, "Logged out from session: " + session);
        } else {
            sLogger.log(Level.WARNING,
                    "Unable to logout from nonexistant session: " + session);
        }
        mSessionsLock.writeLock().unlock();
    }

    /**
     * Find timed out sessions, delete them.
     */
    private void cleanup() {
        mSessionsLock.writeLock().lock();
        Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());
        Iterator<Map.Entry<String, Session>> it =
                mSessions.entrySet().iterator();
        while(it.hasNext()) {
            Session session = it.next().getValue();
            if(timeNow.getTime() - session.getLastRefresh().getTime() > Long.valueOf(mConfigFile.getProperty("SESSION_TIMEOUT"))) {
                it.remove();
            }
        }
        mSessionsLock.writeLock().unlock();
    }

    /**
     * Returns the user with a given username.
     * 
     * @param username
     *            the username of the requested user
     * @throws UnknownUserException
     *             if the username is not valid
     */
    public User getUser(String username) throws UnknownUserException {
        sLogger.log(Level.INFO, "Requesting user for username: " + username);
        mUsersLock.readLock().lock();
        User user = mUsers.get(username);
        if(user == null) {
            sLogger.log(Level.WARNING, "Unknown username: " + username);
            throw new UnknownUserException("Unknown username: " + username);
        }
        mUsersLock.readLock().unlock();
        sLogger.log(Level.INFO, "Found user: " + user);
        return user;
    }

    /**
     * Checks if a user is currently logged in (they have a valid session).
     * Refreshes the last logged in time for a user if they have a valid
     * session.
     * 
     * This function takes the hashed password because it is used by the request
     * handler to authenticate each server request. At the moment, every single
     * request must be invidually authenticated which somewhat negates the need
     * for sessions at all. In the future, however, the sessions can be used to
     * store the password and to keep a valid, authenticated window open.
     * 
     * @param username
     *            the username of the user to check
     * @param hashedPassword
     *            the hashed password of the user to authenticate with
     * @return true if the user has a valid session, false otherwise
     */
    public boolean isLoggedIn(String username, String hashedPassword) {
        sLogger.log(Level.INFO, "Attempted to get login status of username : "
                + username + " with hashed password: " + hashedPassword);
        mUsersLock.readLock().lock();
        mSessionsLock.writeLock().lock();
        User user = mUsers.get(username);
        boolean result = false;
        try {
            if(user != null && user.validateHashedPassword(hashedPassword)) {
                Session userSession = mSessions.get(username);
                if(userSession != null) {
                    mSessions.get(username).refresh();
                    result = true;
                    sLogger.log(Level.INFO,
                            "User has an existing session, which was refreshed");
                } else {
                    sLogger.log(Level.INFO,
                            "User does not have an existing session");
                }
            } else {
                sLogger.log(Level.WARNING,
                        "Unknown username or bad password for username: "
                                + username + " and password: " + hashedPassword);
            }
        } catch(UnsetPasswordException e) {
            sLogger.log(Level.WARNING,
                    "Tried to login with user with uninitialized password",
                    e);
            result = false;
        }
        mUsersLock.readLock().unlock();
        mSessionsLock.writeLock().unlock();
        return result;
    }

    public class ExistingUserException extends Exception {
        private static final long serialVersionUID = 5064882885554598200L;

        public ExistingUserException(String e) {
            super(e);
        }
    }

    public class UnknownUserException extends Exception {
        private static final long serialVersionUID = -8589966171614031273L;

        public UnknownUserException(String e) {
            super(e);
        }
    }
}
