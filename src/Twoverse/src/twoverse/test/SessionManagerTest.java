/**
 * Twoverse Session Manager Test Suite
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
        users[1] =
                new User(0, "second", "second@second.org", "2222222222", 100);
        users[2] = new User(0, "third", "third@third.org", "3333333333", 100);
        users[3] =
                new User(0, "fourth", "fourth@fourth.org", "4444444444", 100);

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
        User candidateUser =
                new User(0, "first", "first@first.org", "1111111111", 100);
        candidateUser.setHashedPassword(users[0].getHashedPassword());
        Assert.assertTrue(manager.login(candidateUser) != null);
        Assert.assertTrue(manager.login(users[1]) == null);

    }

    @Test
    public void testLogout() throws UnsetPasswordException {
        User candidateUser =
                new User(0, "first", "first@first.org", "1111111111", 100);
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
        Assert.assertFalse(session.getId() == 0);
        database.deleteUser(users[1]);
    }

    @Test
    public void testCreatAccountTwice() {
        try {
            manager.createAccount(users[1]);
            manager.createAccount(users[1]);
        } catch(UnsetPasswordException e) {
            Assert.fail();
        } finally {
            database.deleteUser(users[1]);
        }
    }
}
