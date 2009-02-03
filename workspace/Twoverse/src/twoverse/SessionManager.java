package twoverse;

import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.util.Database;
import twoverse.util.Session;
import twoverse.util.User;



public class SessionManager extends XmlRpcServlet { // extends Thread
	public SessionManager(Database database, ObjectManager objectManager) {
		// TODO Auto-generated constructor stub
	}

	public boolean login(String username, String password) throws Exception {
		return false;
	}
	
	public boolean logout(String username, int session) throws Exception {
		return false;
	}
	
	public void refresh(String username, int session) throws Exception {
		
	}
	
	public void cleanup() {
		
	}
	
	 private boolean isAuthenticated(String username, String password) {
         return false;
     }
	 
	 // TODO can this go here and still let SessionManager be a thread?
     protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
     										throws XmlRpcException {
         PropertyHandlerMapping mapping
             = (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
         AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
             new AbstractReflectiveHandlerMapping.AuthenticationHandler(){
                     public boolean isAuthorized(XmlRpcRequest pRequest){
                         XmlRpcHttpRequestConfig config =
                             (XmlRpcHttpRequestConfig) pRequest.getConfig();
                         return isAuthenticated(config.getBasicUserName(),
                             config.getBasicPassword());
                     };
             };
         mapping.setAuthenticationHandler(handler);
         return mapping;
     }

	
	private HashMap<Integer, Session> mSessions;
	private HashMap<Integer, User> mUsers; // fills up as we request more by id
}
