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



public class SessionManager extends Thread {
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
	
	private HashMap<Integer, Session> mSessions;
	private HashMap<Integer, User> mUsers; // fills up as we request more by id
}
