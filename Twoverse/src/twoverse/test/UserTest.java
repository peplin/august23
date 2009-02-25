package twoverse.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

public class UserTest {
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(0, "first", "first@first.org", "1111111111", 100);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = UnsetPasswordException.class)
    public void testValidateWithoutPassword() throws UnsetPasswordException {
        Assert.assertFalse(user.validatePassword("test"));
    }

    @Test
    public void testSetPlaintextPassword() {
        user.setPlaintextPassword("real_password");
        try {
            Assert.assertTrue(user.validatePassword("real_password"));
        } catch (UnsetPasswordException e) {
            Assert.fail("Password was not set");
        }
        Assert.assertFalse("real_password" == user.getHashedPassword());
    }
}
