package twoverse;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

public class SessionManager extends Thread {
    private HashMap<String, Session> mSessions; // username to session obj
    private HashMap<String, User> mUsers; // username to user obj
    private Database mDatabase;
    private static Logger sLogger =
            Logger.getLogger(SessionManager.class.getName());

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
        if(!mUsers.containsKey(user.getUsername())) {
            mDatabase.addUser(user);
            mUsers.put(user.getUsername(), user);
            return user.getId();
        } else {
            throw new ExistingUserException("Username " + user.getUsername()
                + " is already in use");
        }
    }

    /**
     * Create session if not logged in, else refresh the existing session.
     * 
     * @param username
     * @param password
     * @return successful login or existing session
     * @throws Exception
     */
    public int login(String username, String hashedPassword) {
        User user = mUsers.get(username);
        try {
            if(user != null && user.validateHashedPassword(hashedPassword)) {
                Session userSession = mSessions.get(username);
                mDatabase.updateLoginTime(user);

                if(userSession == null) {
                    mSessions.put(username, new Session(user));
                    mDatabase.updateLoginTime(user);
                } else {
                    mSessions.get(username).refresh();
                }
                return mSessions.get(username).getId();
            }
        } catch (UnsetPasswordException e) {
            sLogger.log(Level.INFO,
                "Tried to login with user with uninitialized password", e);
            return -1;
        }
        return -1;
    }

    public void logout(String username, int session) {
        if(mSessions.get(username).getId() == session) {
            mSessions.remove(username);
        }
    }

    /**
     * Find timed out sessions, delete them.
     */
    private void cleanup() {
        // TODO Find timed out sessions, delete them

    }

    public User getUser(String username) {
        return mUsers.get(username);
    }
    
    public class ExistingUserException extends Exception {
        public ExistingUserException(String e) {
            super(e);
        }
    }

}
