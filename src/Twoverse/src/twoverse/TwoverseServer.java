/**
 * Twoverse Server
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import twoverse.Database.DatabaseException;

public class TwoverseServer {
    private static Logger sLogger =
            Logger.getLogger(TwoverseServer.class.getName());
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
            XmlRpcServerConfigImpl config =
                    (XmlRpcServerConfigImpl) server.getConfig();
            config.setEnabledForExtensions(true);

            mWebServer.start(); // accept requests
        } catch(DatabaseException e) {
            sLogger.log(Level.WARNING, e.getMessage(), e);
        } catch(ServletException e) {
            sLogger.log(Level.SEVERE, "Unable to create or start servlet", e);
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        TwoverseServer server = new TwoverseServer();
        try {
            Logger.getLogger("").addHandler(new FileHandler("twoverse.log",
                    true));
        } catch(SecurityException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
        }
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for(Handler handler : handlers) {
            handler.setLevel(Level.INFO);
        }
        server.run();
    }
}
