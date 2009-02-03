package twoverse;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.util.Database;
import twoverse.util.DatabaseException;

public class TwoverseServer  extends XmlRpcServlet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database database = new Database();
			ObjectManager objectManager = new ObjectManagerServer(database);
			SessionManager sessionManager
					= new SessionManager(database, objectManager);
			SimulationRunner simulation = new SimulationRunner(objectManager);
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		XmlRpcServlet servlet = new XmlRpcServlet();
        ServletWebServer webServer;
		try {
			webServer = new ServletWebServer(servlet, 8080);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			webServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private boolean isAuthenticated(String username, String password) {
        return false;
    }
	 
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
    										throws XmlRpcException {
        PropertyHandlerMapping mapping
            = (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
        AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
            new AbstractReflectiveHandlerMapping.AuthenticationHandler() {
	        	public boolean isAuthorized(XmlRpcRequest pRequest) {
	        		XmlRpcHttpRequestConfig config =
	        			(XmlRpcHttpRequestConfig) pRequest.getConfig();
	        		return isAuthenticated(config.getBasicUserName(),
	                            			config.getBasicPassword());
                };
            };
        mapping.setAuthenticationHandler(handler);
        return mapping;
    }
}
