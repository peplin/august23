package twoverse.test;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.Database;
import twoverse.ObjectManager;
import twoverse.ObjectManagerClient;
import twoverse.ObjectManagerServer;
import twoverse.RequestHandlerClient;
import twoverse.RequestHandlerServer;
import twoverse.SessionManager;

public class RequestHandlerClientTest {
    private static RequestHandlerServer server;
    private static RequestHandlerClient client;
    private static ObjectManagerServer objectManager;
    private static ObjectManagerClient objectManagerClient;
    private static SessionManager sessionManager;
    private static Database database;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        database = new Database();
        objectManager = new ObjectManagerServer(database);
        objectManagerClient = new ObjectManagerClient();
        sessionManager = new SessionManager(database);
        server = new RequestHandlerServer(objectManager, sessionManager);
        client = new RequestHandlerClient(objectManagerClient);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testLogin() {
        
    }

}
