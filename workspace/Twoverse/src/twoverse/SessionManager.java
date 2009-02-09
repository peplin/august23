package twoverse;

import java.util.HashMap;
import java.util.logging.Logger;

import twoverse.Database;
import twoverse.util.Session;
import twoverse.util.User;

public class SessionManager extends Thread {
    private HashMap<String, Session> mSessions; // username to session obj
    private HashMap<String, User> mUsers; // username to user obj
    private Database mDatabase;
    private static Logger sLogger = Logger.getLogger(SessionManager.class
            .getName());

    public SessionManager(Database database) {
        mDatabase = database;
        initializeUsers();
    }

    private void initializeUsers() {
        mUsers = mDatabase.getUsers();
    }

    public int createAccount(String username, String hashedPassword,
            String email, String phone, int points) {
        if (!mUsers.containsKey(username)) {
            mUsers.put(username, new User(username, hashedPassword, email,
                    phone, points));
        }
        return -1;
    }

    /**
     * Create session if not logged in, else refresh the existing session.
     * 
     * @param username
     * @param password
     * @return successful login or existing session
     * @throws Exception
     */
    public boolean login(String username, String plaintextPassword) {
        User user = mUsers.get(username);
        if (user != null && user.validatePassword(plaintextPassword)) {
            Session userSession = mSessions.get(username);

            if (userSession == null) {
                mSessions.put(username, new Session(user));
            } else {
                mSessions.get(username).refresh();
            }
            return true;
        }
        return false;
    }

    public boolean logout(int session) {
        return false;
    }

    /**
     * Find timed out sessions, delete them.
     */
    public void cleanup() {
        // TODO Find timed out sessions, delete them

    }

    public User getUser(String username) {
        return mUsers.get(username);
    }

}
