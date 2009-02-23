package twoverse.test;

import java.sql.SQLException;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.Database;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DatabaseTest {
    private static Database database;
    private static User[] users;
    private static int startingId;
    private static CelestialBody body;

    @BeforeClass
    public static void setUp() throws Exception {
        database = new Database();
        users = new User[4];
        users[0] = new User(0, "first", "first@first.org", "1111111111", 100);
        users[1] = new User(0, "second", "second@second.org", "2222222222", 100);
        users[2] = new User(0, "third", "third@third.org", "3333333333", 100);
        users[3] = new User(0, "fourth", "fourth@fourth.org", "4444444444", 100);

        users[0].setPlaintextPassword("firstpass");
        users[1].setPlaintextPassword("secondpass");
        users[2].setPlaintextPassword("thirdpass");
        users[3].setPlaintextPassword("fourthpass");

        startingId = database.addUser(users[0]);
        database.addUser(users[1]);
        database.addUser(users[2]);
        database.addUser(users[3]);

        body = new CelestialBody(0, -1, "theBody", null, null, -1, new Point(
                42, 43, 44), new PhysicsVector3d(1, 2, 3, 4),
                new PhysicsVector3d(5, 6, 7, 8));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        database.deleteUser(users[0]);
        database.deleteUser(users[1]);
        database.deleteUser(users[2]);
        database.deleteUser(users[3]);
        database.finalize();
    }

    @Test
    public void testInsertUser() throws Exception {
        Assert.assertEquals(startingId, users[0].getId());
        Assert.assertEquals(startingId + 1, users[1].getId(), 2);
        Assert.assertEquals(startingId + 2, users[2].getId(), 3);
        Assert.assertEquals(startingId + 3, users[3].getId(), 4);
    }

    @Test
    public void testUpdateTime() throws Exception {
        database.updateLoginTime(users[2]);
    }

    @Test
    public void testDeleteUser()
            throws MySQLIntegrityConstraintViolationException {
        User deadUser = new User(0, "dead", "dead@dead.org", "dddddddddd", 0);
        deadUser.setPlaintextPassword("password");
        database.addUser(deadUser);
        database.deleteUser(deadUser);
    }

    @Test
    public void testGetUsers() throws Exception {
        HashMap<String, User> returnedUsers = database.getUsers();
        Assert.assertEquals(4, returnedUsers.size());
        Assert.assertTrue(users[0].equals(returnedUsers.get(users[0]
                .getUsername())));
    }

    @Test
    public void testAddManmadeBody() throws SQLException {
        ManmadeBody manmadeBody = new ManmadeBody(body);
        database.add(manmadeBody);
    }

    @Test
    public void testDeleteManmadeBody() throws SQLException {
        int previousCount = database.getManmadeBodies().size();
        ManmadeBody manmadeBody = new ManmadeBody(body);
        database.add(manmadeBody);
        database.deleteObject(manmadeBody);
        Assert.assertEquals(previousCount, database.getManmadeBodies().size());
    }

    @Test
    public void testAddPlanetarySystem() {
        PlanetarySystem system = new PlanetarySystem(body, -1, 1000.1);
        database.add(system);
    }

    @Test
    public void testDeletePlanetarySystem() {
        int previousCount = database.getPlanetarySystems().size();
        PlanetarySystem system = new PlanetarySystem(body, -1, 1000.1);
        database.add(system);
        database.deleteObject(system);
        Assert.assertEquals(previousCount, database.getPlanetarySystems()
                .size());
    }

    @Test
    public void testAddGalaxy() {
        Galaxy galaxy = new Galaxy(body, new GalaxyShape(1, "test", "test"),
                1000.5, 2000.20);
        database.add(galaxy);
        Assert.assertNotNull(galaxy.getBirthTime());
    }

    @Test
    public void testDeleteGalaxy() {
        int previousCount = database.getGalaxies().size();
        Galaxy galaxy = new Galaxy(body, new GalaxyShape(1, "test", "test"),
                1000.5, 2000.20);
        database.add(galaxy);
        database.deleteObject(galaxy);
        Assert.assertEquals(previousCount, database.getGalaxies().size());
    }

    @Test
    public void testGetGalaxies() {
        database.getGalaxies();
    }

    @Test
    public void testGetPlanetarySystems() {
        database.getPlanetarySystems();
    }

    @Test
    public void testGetManmadeBodies() {
        database.getManmadeBodies();
    }

    @Test
    public void testAddPlanetarySystems() {
        PlanetarySystem[] systems = new PlanetarySystem[10];
        for (int i = 0; i < 10; i++) {
            systems[i] = new PlanetarySystem(body, -1, 1000 + i);
        }
        int previousCount = database.getPlanetarySystems().size();
        database.addPlanetarySystems(systems);
        Assert.assertEquals(previousCount + 10, database.getPlanetarySystems()
                .size());
    }

    @Test
    public void testAddManmadeBodies() {
        ManmadeBody[] manmadeBodies = new ManmadeBody[10];
        for (int i = 0; i < 10; i++) {
            manmadeBodies[i] = new ManmadeBody(body);
        }
        int previousCount = database.getManmadeBodies().size();
        database.addManmadeBodies(manmadeBodies);
        Assert.assertEquals(previousCount + 10, database.getManmadeBodies()
                .size());
    }

    @Test
    public void testAddGalaxies() {
        Galaxy[] galaxies = new Galaxy[10];
        for (int i = 0; i < 10; i++) {
            galaxies[i] = new Galaxy(body, new GalaxyShape(1, "test", "test"),
                    1000.5 + i, 2000.20 - i);
        }
        int previousCount = database.getGalaxies().size();
        database.addGalaxies(galaxies);
        Assert.assertEquals(previousCount + 10, database.getGalaxies().size());
    }
}
