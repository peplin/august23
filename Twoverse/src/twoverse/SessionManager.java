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
        } catch (IOException e) {
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
        mUsers = mDatabase.getUsers();
    }

    @Override
    public void run() {
        cleanup();
    }

    public int createAccount(User user) throws UnsetPasswordException {
        if(user.getHashedPassword() == null) {
            throw new User.UnsetPasswordException("User doesn't have password set");
        }
        mUsersLock.writeLock().lock();
        if(!mUsers.containsKey(user.getUsername())) {
            mDatabase.addUser(user);
            mUsers.put(user.getUsername(), user);
        } else {
            mUsersLock.writeLock().unlock();
            sLogger.log(Level.WARNING, "Username " + user.getUsername()
                    + " is already in use");
            return mUsers.get(user.getUsername()).getId();
        }
        mUsersLock.writeLock().unlock();
        return user.getId();
    }

    /**
     * 
     * @param user
     */
    public void deleteAccount(Session session) {
        mUsersLock.writeLock().lock();
        mSessionsLock.writeLock().lock();
        if(mSessions.get(session.getUser().getUsername()).equals(session)) {
            mDatabase.deleteUser(session.getUser());
            mSessions.remove(session.getUser().getUsername());
            mUsers.remove(session.getUser().getUsername());
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
     * @param username
     * @param password
     * @return successful login or existing session
     * @throws UnsetPasswordException
     * @throws Exception
     */
    public Session login(User user) throws UnsetPasswordException {
        mUsersLock.readLock().lock();
        mSessionsLock.writeLock().lock();
        User actualUser = mUsers.get(user.getUsername());
        try {
            if(actualUser != null && actualUser.validate(user)) {
                Session userSession = mSessions.get(actualUser.getUsername());
                if(userSession == null) {
                    mSessions.put(actualUser.getUsername(),
                            new Session(actualUser));
                    mDatabase.updateLoginTime(actualUser);
                }
                mUsersLock.readLock().unlock();
                mSessionsLock.writeLock().unlock();
                return mSessions.get(actualUser.getUsername());
            }
        } catch (UnsetPasswordException e) {
            sLogger.log(Level.WARNING,
                    "Tried to login with user with uninitialized password",
                    e);
        }
        mUsersLock.readLock().unlock();
        mSessionsLock.writeLock().unlock();
        return null;
    }

    public void logout(Session session) {
        mSessionsLock.writeLock().lock();
        if(mSessions.get(session.getUser().getUsername()).equals(session)) {
            mSessions.remove(session.getUser().getUsername());
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
        while (it.hasNext()) {
            Session session = (Session) it.next().getValue();
            if(timeNow.getTime() - session.getLastRefresh().getTime() > Long.valueOf(mConfigFile.getProperty("SESSION_TIMEOUT"))) {
                it.remove();
            }
        }
        mSessionsLock.writeLock().unlock();
    }

    public User getUser(String username) throws UnknownUserException {
        mUsersLock.readLock().lock();
        User user = mUsers.get(username);
        if(user == null) {
            throw new UnknownUserException("Unknown user " + user);
        }
        mUsersLock.readLock().unlock();
        return user;
    }

    public boolean isLoggedIn(String username, String hashedPassword) {
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
                }
            }
        } catch (UnsetPasswordException e) {
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
