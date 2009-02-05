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

    public Session(String username, String password) {
        try {
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("../config/Session.properties"));
        } catch (IOException e) {

        }

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config
                    .setServerURL(new URL(mConfigFile
                            .getProperty("XMLRPCSERVER")));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        mXmlRpcClient = new XmlRpcClient();
        mXmlRpcClient.setConfig(config);

        authenticate(username, password);
    }

    public void finalize() {
        // logout
        Object[] parameters = new Object[] { mUser.getUsername(), mSessionId };
        try {
            mXmlRpcClient.execute("SessionManager.logout", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void refresh() throws TimedOutException {
        Object[] parameters = new Object[] { mUser.getUsername(), mSessionId };
        try {
            mXmlRpcClient.execute("SessionManager.refresh", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void authenticate(String username, String plaintextPassword) {
        // if good, set login time
        Object[] parameters = new Object[] { username, plaintextPassword };
        try {
            mXmlRpcClient.execute("SessionManager.login", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private User mUser;
    private int mSessionId;
    private Time mLastRefresh;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
}
