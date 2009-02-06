package twoverse;

import twoverse.util.Session;

public class RequestHandlerClient implements TwoversePublicApi {
    public RequestHandlerClient(ObjectManager objectManager,
            SessionManager sessionManager) {
        try {
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                    "twoverse/conf/RequestHandlerClient.properties"));
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
    }

    public boolean login(String username, String password) {
        // if good, set login time
        Object[] parameters = new Object[] { username, plaintextPassword };
        try {
            mXmlRpcClient.execute("SessionManager.login", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean logout(String username, int session) {
        // logout
        Object[] parameters = new Object[] { mUser.getUsername(), mSessionId };
        try {
            mXmlRpcClient.execute("SessionManager.logout", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void refreshUser(String username, int session) {
        Object[] parameters = new Object[] { mUser.getUsername(), mSessionId };
        try {
            mXmlRpcClient.execute("SessionManager.refresh", parameters);
        } catch (XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // TODO double all of these, one accepts serialized object
    public void addGalaxy() {

    }

    public void addPlanetarySystem() {

    }

    public void addManmadeBody() {

    }

    public void changeName(int objectId) {

    }

    private boolean isAuthenticated(String username, String password) {
        return false;
    }

    private ObjectManagerClient mObjectManager;
    private SessionManager mSessionManager;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;
}
