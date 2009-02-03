package twoverse;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class RequestHandlerClient implements TwoversePublicApi { 
    public RequestHandlerClient(ObjectManager objectManager,
                            SessionManager sessionManager) {
    }

    public boolean login(String username, String password) {
            return false;
    }
    
    public boolean logout(String username, int session) {
            return false;
    }
    
    public void refreshUser(String username, int session) {
            
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
    private Session mSession;
}
