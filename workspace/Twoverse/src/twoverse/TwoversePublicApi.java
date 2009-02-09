package twoverse;

import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

public interface TwoversePublicApi {

    /**
     * Create a new account in the system
     * 
     * @param username
     * @param hashedPassword
     * @param email
     * @param phone
     * @return ID for new user account
     */
    public int createAccount(String username, String hashedPassword,
            String email, String phone) throws InvalidUserException;

    /**
     * Unregisters a currently active session. Confirm that session number
     * belongs to requesting user.
     * 
     * We don't need a login method as each RPC call is individually
     * authenticated. The first request will "login".
     * 
     * @param username
     * @param session
     */
    public void logout(int session);

    /**
     * 
     * @param galaxy
     * @return ID for new object
     */
    public int add(Galaxy galaxy);

    /**
     * 
     * @param system
     * @return ID for new object
     */
    public int add(PlanetarySystem system);

    /**
     * 
     * @param body
     * @return ID for new object
     */
    public int add(ManmadeBody body);

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
     * Change the name of an existing object.
     * 
     * @param objectId
     * @param newName
     */
    public void changeName(int objectId, String newName);

}
