package twoverse.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Properties;

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
    public Session(String username, Time loginTime) {

    }

    private User mUser;
    private int mSessionId;
    private Time mLastRefresh;
}
