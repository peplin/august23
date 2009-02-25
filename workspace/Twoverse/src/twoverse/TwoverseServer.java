package twoverse;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.DatabaseException;

public class TwoverseServer {
    private static Logger sLogger = Logger.getLogger(TwoverseServer.class
            .getName());
    private ServletWebServer mWebServer;
    private Database mDatabase;
    private ObjectManagerServer mObjectManager;
    private SessionManager mSessionManager;
    private SimulationRunner mSimulation;

    public void run() {
        try {
            mDatabase = new Database();
            mObjectManager = new ObjectManagerServer(mDatabase);
            mSessionManager = new SessionManager(mDatabase);
            mSimulation = new SimulationRunner(mObjectManager);

            RequestHandlerServer.init(mObjectManager, mSessionManager);

            XmlRpcServlet servlet = new RequestHandlerServer();
            mWebServer = new ServletWebServer(servlet, 8080);
            XmlRpcServer server = servlet.getXmlRpcServletServer();
            XmlRpcServerConfigImpl config = (XmlRpcServerConfigImpl) server
                    .getConfig();
            config.setEnabledForExtensions(true);

            Timer feedPushTimer = new Timer();
            feedPushTimer.scheduleAtFixedRate(mObjectManager, 0, mObjectManager
                    .getFeedDelay());

            Timer sessionCleanupTimer = new Timer();
            sessionCleanupTimer.scheduleAtFixedRate(mSessionManager, 0,
                    mSessionManager.getCleanupDelay());

            mSimulation.start(); // run simulation
            mWebServer.start(); // accept requests
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
