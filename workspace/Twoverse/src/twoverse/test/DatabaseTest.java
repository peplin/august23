package twoverse.test;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.Database;
import twoverse.util.User;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class DatabaseTest {
    private static Database database;
    private static User[] users;
    private static int startingId;

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
}
