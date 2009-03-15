package twoverse.test;

import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.Database;
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
    private static Database database;
    private static User[] users;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        database = new Database();
        server = new TwoverseServer();
        server.run();
        client = new RequestHandlerClient();
        objectManagerClient = new ObjectManagerClient(client);

        users = new User[4];
        users[0] =
                new User(0, "xmlrpcfirst", "first@first.org", "1111111111", 100);
        users[1] =
                new User(0, "second", "second@second.org", "2222222222", 100);
        users[2] = new User(0, "third", "third@third.org", "3333333333", 100);
        users[3] =
                new User(0, "fourth", "fourth@fourth.org", "4444444444", 100);

        users[0].setPlaintextPassword("firstpass");
        users[1].setPlaintextPassword("secondpass");
        users[2].setPlaintextPassword("thirdpass");
        users[3].setPlaintextPassword("fourthpass");
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
    public void createAccount() {
        int idBefore = users[0].getId();
        Assert.assertTrue(0 < client.createAccount(users[0]));
        Assert.assertFalse(idBefore == users[0].getId());
        client.login("xmlrpcfirst", "firstpass");
        client.deleteAccount();
    }

    @Test
    public void testLogin() {
        client.createAccount(users[0]);
        Assert.assertTrue(client.login("xmlrpcfirst", "firstpass") != null);
        client.deleteAccount();
    }

    @Test
    public void testAddGalaxy() {
        client.createAccount(users[0]);
        client.login("xmlrpcfirst", "firstpass");

        Galaxy galaxy =
                new Galaxy(0,
                        "theBody",
                        0,
                        new Point(42, 43, 44),
                        new PhysicsVector3d(1, 2, 3, 4),
                        new PhysicsVector3d(5, 6, 7, 8),
                        new GalaxyShape(1, "test", "test"),
                        1000.5,
                        2000.20);
        objectManagerClient.add(galaxy);
        Assert.assertTrue(galaxy.getId() != 0);
        database.delete(galaxy);
        client.deleteAccount();
    }
}
