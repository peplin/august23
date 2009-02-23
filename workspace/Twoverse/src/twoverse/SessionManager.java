package twoverse;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

public class SessionManager extends Thread {
    private HashMap<String, Session> mSessions; // username to session obj
    private HashMap<String, User> mUsers; // username to user obj
    private Database mDatabase;
    private static Logger sLogger = Logger.getLogger(SessionManager.class
            .getName());

    public SessionManager(Database database) {
        mDatabase = database;
        mSessions = new HashMap<String, Session>();
        mUsers = new HashMap<String, User>();
        initializeUsers();
    }

    private void initializeUsers() {
        mUsers = mDatabase.getUsers();
    }

    public int createAccount(User user) throws ExistingUserException {
        if (!mUsers.containsKey(user.getUsername())) {
            mDatabase.addUser(user);
            mUsers.put(user.getUsername(), user);
            return user.getId();
        } else {
            throw new ExistingUserException("Username " + user.getUsername()
                    + " is already in use");
        }
    }

    /**
     * Create session if not logged in, else ignore.
     * 
     * @param username
     * @param password
     * @return successful login or existing session
     * @throws UnsetPasswordException
     * @throws Exception
     */
    public Session login(User user) throws UnsetPasswordException {
        User actualUser = mUsers.get(user.getUsername());
        try {
            if (actualUser != null && actualUser.validate(user)) {
                Session userSession = mSessions.get(actualUser.getUsername());
                if (userSession == null) {
                    mSessions.put(actualUser.getUsername(), new Session(
                            actualUser));
                    mDatabase.updateLoginTime(actualUser);
                }
                return mSessions.get(actualUser.getUsername());
            }
        } catch (UnsetPasswordException e) {
            sLogger.log(Level.INFO,
                    "Tried to login with user with uninitialized password", e);
            throw e;
        }
        return null;
    }

    public void logout(Session session) {
        if (mSessions.get(session.getUser().getUsername()).equals(session)) {
            mSessions.remove(session.getUser().getUsername());
        }
    }

    /**
     * Find timed out sessions, delete them.
     */
    private void cleanup() {
        Timestamp timeNow = new Timestamp((new java.util.Date()).getTime());
        Iterator<Map.Entry<String, Session>> it = mSessions.entrySet()
                .iterator();
        while (it.hasNext()) {
            Session session = (Session) it.next();
            // TODO pull out this constant
            if (timeNow.getTime() - session.getLastRefresh().getTime() > 1000) {
                it.remove();
            }
        }
    }

    public User getUser(String username) {
        return mUsers.get(username);
    }

    public class ExistingUserException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 5064882885554598200L;

        public ExistingUserException(String e) {
            super(e);
        }
    }

    public boolean isLoggedIn(String username, String hashedPassword) {
        User user = mUsers.get(username);
        try {
            if (user != null && user.validateHashedPassword(hashedPassword)) {
                Session userSession = mSessions.get(username);
                if (userSession != null) {
                    mSessions.get(username).refresh();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (UnsetPasswordException e) {
            sLogger.log(Level.INFO,
                    "Tried to login with user with uninitialized password", e);
            return false;
        }
        return false;
    }
}
