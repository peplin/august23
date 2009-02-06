package twoverse;

import java.io.IOException;
import javax.servlet.ServletException;

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
            // TODO make all managers thread safe
            Database database = new Database();
            ObjectManager objectManager = new ObjectManagerServer(database);
            SessionManager sessionManager = new SessionManager(database,
                    objectManager);
            SimulationRunner simulation = new SimulationRunner(objectManager);

            XmlRpcServlet servlet = new RequestHandlerServer(objectManager,
                    sessionManager);

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
