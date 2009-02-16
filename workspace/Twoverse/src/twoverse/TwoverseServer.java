package twoverse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.DatabaseException;

public class TwoverseServer {
    private static Logger sLogger =
            Logger.getLogger(TwoverseServer.class.getName());

    public void run() {
        try {
            // TODO make all managers thread safe
            Database database = new Database();
            ObjectManagerServer objectManager =
                    new ObjectManagerServer(database);
            SessionManager sessionManager = new SessionManager(database);
            SimulationRunner simulation = new SimulationRunner(objectManager);

            //XmlRpcServlet servlet =
            //        new RequestHandlerServer(objectManager, sessionManager);
            XmlRpcServlet servlet = new TwoverseServlet();
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
