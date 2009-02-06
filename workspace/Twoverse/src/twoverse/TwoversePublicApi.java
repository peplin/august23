package twoverse;

public interface TwoversePublicApi {
    public boolean login(String username, String password);

    public boolean logout(String username, int session);

    public void refreshUser(String username, int session);

    public void addGalaxy(Galaxy galaxy);
    public void addPlanetarySystem(PlanetarySystem system);
    public void addManmadeBody(ManmadeBody body);

    /** 
     * Methods for creating via Javascript web interface - 
     * can't use Serialized Java objects, so we give explicit constructor
     * looking things
     *
     * TODO do we need owner here, or can it use the currently authenticated
     * user over HTTP?
     */
    public void addGalaxy(User owner, CelestialBody parent, 
                            PhysicsVector3d velocity, 
                            PhysicsVector3d acceleration, 
                            Color color,
                            GalaxyShape shape);
    public void addPlanetarySystem(User owner, CelestialBody parent, 
                            PhysicsVector3d velocity, 
                            PhysicsVector3d acceleration, 
                            Color color,
                            Star center);
    public void addManmadeBody(User owner, CelestialBody parent, 
                            PhysicsVector3d velocity, 
                            PhysicsVector3d acceleration, 
                            Color color);

    public void changeName(int objectId);
}
