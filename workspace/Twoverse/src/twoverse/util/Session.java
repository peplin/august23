package twoverse.util;

import java.sql.Timestamp;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

class TimedOutException extends Exception {
    TimedOutException(String message) {
    }
}

class BadUsernameException extends Exception {
    BadUsernameException(String message) {
    }
}

class BadPasswordException extends Exception {
    BadPasswordException(String message) {
    }
}

public class Session {
    public Session(String username, Timestamp loginTime) {

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

    public void setLastRefresh(Timestamp lastRefresh) {
        mLastRefresh = lastRefresh;
    }

    public Timestamp getLastRefresh() {
        return mLastRefresh;
    }

    private User mUser;
    private int mId;
    private Timestamp mLastRefresh;
}
