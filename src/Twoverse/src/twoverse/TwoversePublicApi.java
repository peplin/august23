/**
 * Twoverse RPC Interface (incomplete)
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

package twoverse;

import twoverse.ObjectManager.UnhandledCelestialBodyException;
import twoverse.SessionManager.ExistingUserException;
import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.util.Session;
import twoverse.util.User;
import twoverse.util.User.UnsetPasswordException;

/**
The Twoverse Public API is my hacked up way of setting a public XML-RPC API
for clients of Twoverse to use. Defined here are MOST of the functions the
clients can call.

The API because slightly more complicated when differences in loggin in with
each request handler became obvious. There are now a few extra functions defined
outside of this API.

NOTE: All of these functions must return something - they CANNOT return void.
This is due to a limitation in Apache XML-RPC. In some cases, the return value
may seem unneccessary because of this.

   @author Christopher Peplin (chris.peplin@rhubarbtech.com)
   @version 1.0, Copyright 2009 under Apache License
*/
public interface TwoversePublicApi {
    /**
     * Creates a new account.
     * 
     * @param user the user to create. Must have a hashed password set and
     * must NOT have an ID set. The ID will be set when returning from this
     * method.
     * @return the ID for new account
     * @throws ExistingUserException if an account with this username already
     * exists
     * @throws UnsetPasswordException if the hashed password is not set
     */
    public int createAccount(User user) throws ExistingUserException,
            UnsetPasswordException;

    /**
     * Add a new object to the universe.

     * These functions modify galaxy, but over XML-RPC that doesn't really work.
     * So, we must explicitly return the new object
     * 
     * @param body the object to add. The ID must NOT be set, and will be set
     * on returning from this method.
     * @return the ID for the new object
     * @throws UnhandledCelestialBodyException
     */
    public CelestialBody add(CelestialBody body)
            throws UnhandledCelestialBodyException;

    /** 
    * Updates an object in the universe with the one provided, overwriting any
    previous values.

    @param body the body to update. The ID must be set and it must be known
    by the server.
    @return the updated object
    */
    public CelestialBody update(CelestialBody body);

    /**
     *  Add a new link to the universe.
     *
     * @param body the object to add. The ID must NOT be set, and will be set
     * on returning from this method.
     * @return the ID for the new object
     * @throws UnhandledCelestialBodyException
     */
    public Link add(Link link);
}
