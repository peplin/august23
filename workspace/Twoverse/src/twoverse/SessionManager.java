package twoverse;

import java.util.HashMap;

import twoverse.Database;
import twoverse.util.Session;
import twoverse.util.User;

public class SessionManager extends Thread {
    public SessionManager(Database database) {
        // TODO ask database for all users
        mDatabase = database;
        initializeUsers();
    }
    
    private void initializeUsers() {
        try {
            mUsers = mDatabase.getUsers();
        } catch (InvalidUserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public boolean login(String username, String password) throws Exception {
        return false;
    }

    public boolean logout(String username, int session) throws Exception {
        return false;
    }

    public void refresh(String username, int session) throws Exception {

    }

    public void cleanup() {

    }
    
    public User getUser(int id) {
        return mUsers.get(id);
    }

    private HashMap<Integer, Session> mSessions;
    private HashMap<Integer, User> mUsers; // fills up as we request more by id
    private Database mDatabase;
}
