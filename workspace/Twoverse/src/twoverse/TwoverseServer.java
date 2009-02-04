package twoverse;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.util.Database;
import twoverse.util.DatabaseException;

public class TwoverseServer {

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
	        
			XmlRpcServlet servlet
					= new RequestHandlerServer(objectManager, sessionManager);
			XmlRpcServerConfigImpl config = new XmlRpcServerConfigImpl();
			config.setEnabledForExtensions(true);
			servlet.init((ServletConfig) config);
			
			ServletWebServer webServer;
	        webServer = new ServletWebServer(servlet, 8080);
			webServer.start();
		} catch (DatabaseException e) {
			e.printStackTrace();			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
