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

public class SessionManager extends TimerTask {
    private HashMap<String, Session> mSessions; // username to session obj
    private HashMap<String, User> mUsers; // username to user obj
    private Database mDatabase;
    private ReentrantReadWriteLock mUsersLock;
    private ReentrantReadWriteLock mSessionsLock;
    private Properties mConfigFile;
    private static Logger sLogger =
            Logger.getLogger(SessionManager.class.getName());

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

    @Override
    public void run() {
        sLogger.log(Level.FINE, "Cleaning up user sessions");
        cleanup();
        sLogger.log(Level.FINE, "Done cleaning up user sessions");
    }

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
     * 
     * @param user
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
     * Create session if not logged in, else ignore.
     * 
     * @return successful login or existing session
     * @throws UnsetPasswordException
     * @throws Exception
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
                mUsersLock.readLock().unlock();
                mSessionsLock.writeLock().unlock();
                return mSessions.get(actualUser.getUsername());
            }
        } catch(UnsetPasswordException e) {
            sLogger.log(Level.WARNING,
                    "Tried to login with user with uninitialized password",
                    e);
        }
        mUsersLock.readLock().unlock();
        mSessionsLock.writeLock().unlock();
        return null;
    }

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
