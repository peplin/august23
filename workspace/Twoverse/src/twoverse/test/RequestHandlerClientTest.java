package twoverse.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.ObjectManagerClient;
import twoverse.RequestHandlerClient;
import twoverse.TwoverseServer;
import twoverse.object.Galaxy;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

public class RequestHandlerClientTest {
    private static TwoverseServer server;
    private static RequestHandlerClient client;
    private static ObjectManagerClient objectManagerClient;

    private User[] users;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        server = new TwoverseServer();
        server.run();
        objectManagerClient = new ObjectManagerClient();
        client = new RequestHandlerClient(objectManagerClient);

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        users = new User[4];
        users[0] = new User(0, "xmlrpcfirst", "first@first.org", "1111111111",
                100);
        users[1] = new User(0, "second", "second@second.org", "2222222222", 100);
        users[2] = new User(0, "third", "third@third.org", "3333333333", 100);
        users[3] = new User(0, "fourth", "fourth@fourth.org", "4444444444", 100);

        users[0].setPlaintextPassword("firstpass");
        users[1].setPlaintextPassword("secondpass");
        users[2].setPlaintextPassword("thirdpass");
        users[3].setPlaintextPassword("fourthpass");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createAccount() {
        int idBefore = users[0].getId();
        Assert.assertTrue(0 < client.createAccount(users[0]));
        Assert.assertFalse(idBefore == users[0].getId());
    }

    @Test
    public void testLogin() {
        Assert.assertTrue(client.login("xmlrpcfirst", "firstpass") != null);
    }

    @Test
    public void testAddGalaxy() {
        Galaxy galaxy = new Galaxy(0, -1, "theBody", null, null, -1, new Point(
                42, 43, 44), new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8), new GalaxyShape(1, "test",
                        "test"), 1000.5, 2000.20);
        client.addGalaxy(galaxy);
        Assert.assertTrue(galaxy.getId() != 0);
    }
}
