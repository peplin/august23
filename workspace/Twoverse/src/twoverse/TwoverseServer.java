package twoverse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcConfig;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.DatabaseException;

public class TwoverseServer {
	private static Logger sLogger = Logger.getLogger(TwoverseServer.class
			.getName());

	public void run() {
		try {
			// TODO make all managers thread safe
			Database database = new Database();
			ObjectManagerServer objectManager = new ObjectManagerServer(
					database);
			SessionManager sessionManager = new SessionManager(database);
			SimulationRunner simulation = new SimulationRunner(objectManager);

			RequestHandlerServer.init(objectManager, sessionManager);

			XmlRpcServerConfigImpl servletConfig = new XmlRpcServerConfigImpl();
			servletConfig.setEnabledForExtensions(true);

			XmlRpcServlet servlet = new RequestHandlerServer();
			servlet.init((ServletConfig)servletConfig);
			ServletWebServer webServer;
			webServer = new ServletWebServer(servlet, 8080);

			simulation.start(); // run simulation
			webServer.start(); // accept requests
		} catch (DatabaseException e) {
			sLogger.log(Level.WARNING, e.getMessage(), e);
		} catch (ServletException e) {
			sLogger.log(Level.SEVERE, "Unable to create or start servlet", e);
		} catch (IOException e) {
			sLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TwoverseServer server = new TwoverseServer();
		server.run();
	}
}
