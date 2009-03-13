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
        users[1] =
                new User(0, "second", "second@second.org", "2222222222", 100);
        users[2] = new User(0, "third", "third@third.org", "3333333333", 100);
        users[3] =
                new User(0, "fourth", "fourth@fourth.org", "4444444444", 100);

        users[0].setPlaintextPassword("firstpass");
        users[1].setPlaintextPassword("secondpass");
        users[2].setPlaintextPassword("thirdpass");
        users[3].setPlaintextPassword("fourthpass");

        startingId = database.addUser(users[0]);
        database.addUser(users[1]);
        database.addUser(users[2]);
        database.addUser(users[3]);

        body =
                new CelestialBody(0,
                        -1,
                        "theBody",
                        null,
                        null,
                        -1,
                        new Point(42, 43, 44),
                        new PhysicsVector3d(1, 2, 3, 4),
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
        Assert.assertTrue(users[0].equals(returnedUsers.get(users[0].getUsername())));
    }

    @Test
    public void testAddManmadeBody() throws SQLException {
        ManmadeBody manmadeBody = new ManmadeBody(body);
        database.add(manmadeBody);
        database.delete(manmadeBody);
    }

    @Test
    public void testDeleteManmadeBody() throws SQLException {
        int previousCount = ManmadeBody.selectAllFromDatabase().size();
        ManmadeBody manmadeBody = new ManmadeBody(body);
        database.add(manmadeBody);
        database.delete(manmadeBody);
        Assert.assertEquals(previousCount, ManmadeBody.selectAllFromDatabase()
                .size());
    }

    @Test
    public void testAddPlanetarySystem() {
        PlanetarySystem system = new PlanetarySystem(body, -1, 1000.1);
        database.add(system);
        database.delete(system);
    }

    @Test
    public void testDeletePlanetarySystem() throws SQLException {
        int previousCount = PlanetarySystem.selectAllFromDatabase().size();
        PlanetarySystem system = new PlanetarySystem(body, -1, 1000.1);
        database.add(system);
        database.delete(system);
        Assert.assertEquals(previousCount,
                PlanetarySystem.selectAllFromDatabase().size());
    }

    @Test
    public void testAddGalaxy() {
        Galaxy galaxy =
                new Galaxy(body,
                        new GalaxyShape(1, "test", "test"),
                        1000.5,
                        2000.20);
        database.add(galaxy);
        Assert.assertNotNull(galaxy.getBirthTime());
        database.delete(galaxy);
    }

    @Test
    public void testDeleteGalaxy() throws SQLException {
        int previousCount = Galaxy.selectAllFromDatabase().size();
        Galaxy galaxy =
                new Galaxy(body,
                        new GalaxyShape(1, "test", "test"),
                        1000.5,
                        2000.20);
        database.add(galaxy);
        database.delete(galaxy);
        Assert.assertEquals(previousCount, Galaxy.selectAllFromDatabase()
                .size());
    }

    @Test
    public void testGetGalaxies() throws SQLException {
        Galaxy.selectAllFromDatabase();
    }

    @Test
    public void testGetPlanetarySystems() throws SQLException {
        PlanetarySystem.selectAllFromDatabase();
    }

    @Test
    public void testGetManmadeBodies() throws SQLException {
        ManmadeBody.selectAllFromDatabase();
    }

    @Test
    public void testAddPlanetarySystems() throws SQLException {
        PlanetarySystem[] systems = new PlanetarySystem[10];
        for (int i = 0; i < 10; i++) {
            systems[i] = new PlanetarySystem(body, -1, 1000 + i);
        }
        int previousCount = PlanetarySystem.selectAllFromDatabase().size();
        for (PlanetarySystem system : systems) {
            system.insertInDatabase();
        }
        Assert.assertEquals(previousCount + 10,
                PlanetarySystem.selectAllFromDatabase().size());
        for (int i = 0; i < 10; i++) {
            database.delete(systems[i]);
        }
    }

    @Test
    public void testAddManmadeBodies() throws SQLException {
        ManmadeBody[] manmadeBodies = new ManmadeBody[10];
        for (int i = 0; i < 10; i++) {
            manmadeBodies[i] = new ManmadeBody(body);
        }
        int previousCount = ManmadeBody.selectAllFromDatabase().size();
        for (ManmadeBody body : manmadeBodies) {
            body.insertInDatabase();
        }
        Assert.assertEquals(previousCount + 10,
                ManmadeBody.selectAllFromDatabase().size());
        for (int i = 0; i < 10; i++) {
            database.delete(manmadeBodies[i]);
        }
    }

    @Test
    public void testAddGalaxies() throws SQLException {
        Galaxy[] galaxies = new Galaxy[10];
        for (int i = 0; i < 10; i++) {
            galaxies[i] =
                    new Galaxy(body,
                            new GalaxyShape(1, "test", "test"),
                            1000.5 + i,
                            2000.20 - i);
        }
        int previousCount = Galaxy.selectAllFromDatabase().size();
        for (Galaxy galaxy : galaxies) {
            galaxy.insertInDatabase();
        }
        Assert.assertEquals(previousCount + 10, Galaxy.selectAllFromDatabase());
        for (int i = 0; i < 10; i++) {
            database.delete(galaxies[i]);
        }
    }
}
