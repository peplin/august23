package twoverse.util;

import java.sql.Timestamp;

public class Session {
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

    private User mUser;
    private int mId;
    private Timestamp mLastRefresh;
    private static int sNextId = 0;
}
