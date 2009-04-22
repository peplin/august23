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

public interface TwoversePublicApi {

    /**
     * @throws UnsetPasswordException
     *             Create a new account in the system
     * 
     * @param username
     * @param hashedPassword
     * @param email
     * @param phone
     * @return ID for new user account
     * @throws
     */
    public int createAccount(User user) throws ExistingUserException,
            UnsetPasswordException;

    /**
     * These functions modify galaxy, but over XML-RPC that doesn't really work.
     * Need to explicitly return the new object
     * 
     * @param body
     * @return ID for new object
     * @throws UnhandledCelestialBodyException
     */
    public CelestialBody add(CelestialBody body)
            throws UnhandledCelestialBodyException;

    public CelestialBody update(CelestialBody body);

    public Link add(Link link);

    /**
     * Change the name of an existing object. User must own the object.
     * 
     * @param objectId
     * @param newName
     */
    public void changeName(Session session, int objectId, String newName);
}
