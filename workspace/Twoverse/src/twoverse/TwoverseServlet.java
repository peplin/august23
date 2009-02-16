package twoverse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class TwoverseServlet extends XmlRpcServlet {
    
    public void init(ServletConfig pConfig) throws ServletException {
        super.init(pConfig);
    }
    
    /**
     * Check that a user exists, confirm the password is correct. If so, create
     * a new session and return true to the client. TODO this will not work.
     * Need to hash password on client, so it's not sent plaintext Need actual
     * correct hashed password in order to hash the candidate use
     * unauthenticated login method that returns the hashed actual, allowing
     * correct hash to be generated. then it can be set for the config.
     */
    private boolean isAuthenticated(String username, String hashedPassword) {
        //return (mSessionManager.login(username, hashedPassword) != -1);
        return true;
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException {
        PropertyHandlerMapping mapping =
                (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
                new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
                    public boolean isAuthorized(XmlRpcRequest pRequest) {
                        //if(mMethodAuthorization.get(pRequest.getMethodName())) {
                            XmlRpcHttpRequestConfig config =
                                    (XmlRpcHttpRequestConfig) pRequest
                                            .getConfig();
                            return isAuthenticated(config.getBasicUserName(),
                                config.getBasicPassword());
                        //} else {
                        //    return true;
                        //}
                    };
                };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }
}
