package twoverse.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import twoverse.Database;
import twoverse.SessionManager;
import twoverse.SessionManager.ExistingUserException;
import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

public class SessionManagerTest {
    private SessionManager manager;
    private static Database database;
    private static User[] users;

    @BeforeClass
    public static void setUpOnce() throws Exception {
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
        database.addUser(users[0]);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        database.deleteUser(users[0]);
    }

    @Before
    public void setUp() throws Exception {
        manager = new SessionManager(database);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLogin() throws UnsetPasswordException {
        User candidateUser = new User(0, "first", "first@first.org",
                "1111111111", 100);
        candidateUser.setHashedPassword(users[0].getHashedPassword());
        Assert.assertTrue(manager.login(candidateUser) != null);
        Assert.assertTrue(manager.login(users[1]) == null);

    }

    @Test
    public void testLogout() throws UnsetPasswordException {
        User candidateUser = new User(0, "first", "first@first.org",
                "1111111111", 100);
        candidateUser.setHashedPassword(users[0].getHashedPassword());
        Session firstSession = manager.login(candidateUser);
        manager.logout(firstSession);
        Session secondSession = manager.login(candidateUser);
        Assert.assertFalse(firstSession.equals(secondSession));
    }

    @Test
    public void testCreateAccount() throws ExistingUserException,
            UnsetPasswordException {
        manager.createAccount(users[1]);
        Session session = manager.login(users[1]);
        Assert.assertFalse(session.getId() == -1);
        database.deleteUser(users[1]);
    }

    @Test
    public void testCreatAccountTwice() {
        try {
            manager.createAccount(users[1]);
            manager.createAccount(users[1]);
            Assert.fail();
        } catch (ExistingUserException e) {
            database.deleteUser(users[1]);
        } catch (UnsetPasswordException e) {
            Assert.fail();
        }
    }
}
