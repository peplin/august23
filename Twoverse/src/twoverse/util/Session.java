package twoverse.util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Session implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3290251644990110932L;

    public Session(User user) {
        refresh();
        setUser(user);
        setId(sNextId++);
    }

    public void setUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    public void setId(int sessionId) {
        mId = sessionId;
    }

    public int getId() {
        return mId;
    }

    public void refresh() {
        java.util.Date now = new java.util.Date();
        mLastRefresh = new Timestamp(now.getTime());
    }

    public Timestamp getLastRefresh() {
        return mLastRefresh;
    }

    @SuppressWarnings("serial")
    public class TimedOutException extends Exception {
        TimedOutException(String message) {
        }
    }

    @SuppressWarnings("serial")
    public class BadUsernameException extends Exception {
        BadUsernameException(String message) {
        }
    }

    @SuppressWarnings("serial")
    public class BadPasswordException extends Exception {
        BadPasswordException(String message) {
        }
    }

    public boolean equals(Session other) {
        return mId == other.mId && mUser.equals(other.mUser);
    }

    private User mUser;
    private int mId;
    private Timestamp mLastRefresh;
    private static int sNextId = 0;
}
