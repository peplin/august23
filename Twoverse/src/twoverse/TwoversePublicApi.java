package twoverse;

import twoverse.SessionManager.ExistingUserException;
import twoverse.object.CelestialBody;
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
     */
    public CelestialBody addCelestialBody(CelestialBody body);

    /**
     * Methods for creating via Javascript web interface - can't use Serialized
     * Java objects, so we give explicit constructor looking things
     * 
     */
    /*
     * public int addGalaxy(User owner, CelestialBody parent, PhysicsVector3d
     * velocity, PhysicsVector3d acceleration, GalaxyShape shape); public int
     * addPlanetarySystem(User owner, CelestialBody parent, PhysicsVector3d
     * velocity, PhysicsVector3d acceleration, Star center); public int
     * addManmadeBody(User owner, CelestialBody parent, PhysicsVector3d
     * velocity, PhysicsVector3d acceleration);
     */

    /**
     * Change the name of an existing object. User must own the object.
     * 
     * @param objectId
     * @param newName
     */
    public void changeName(Session session, int objectId, String newName);
}
