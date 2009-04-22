/**
 * Twoverse User Test Suite
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
        } catch(UnsetPasswordException e) {
            Assert.fail("Password was not set");
        }
        Assert.assertFalse("real_password" == user.getHashedPassword());
    }
}
