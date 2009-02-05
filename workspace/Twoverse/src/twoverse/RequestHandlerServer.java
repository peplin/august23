package twoverse;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class RequestHandlerServer extends XmlRpcServlet implements
        TwoversePublicApi {
    public RequestHandlerServer(ObjectManager objectManager,
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

    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        PropertyHandlerMapping mapping = (PropertyHandlerMapping) super
                .newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler = new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
            public boolean isAuthorized(XmlRpcRequest pRequest) {
                XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) pRequest
                        .getConfig();
                return isAuthenticated(config.getBasicUserName(), config
                        .getBasicPassword());
            };
        };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }

    private ObjectManagerServer objectManager;
    private SessionManager sessionManager;
}
