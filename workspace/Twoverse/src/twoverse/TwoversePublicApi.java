package twoverse;

public interface TwoversePublicApi { 
    public boolean login(String username, String password); 
    public boolean logout(String username, int session); 
    public void refreshUser(String username, int session);

    // TODO double all of these, one accepts serialized object
    public void addGalaxy(); 
    public void addPlanetarySystem();
    public void addManmadeBody();

    public void changeName(int objectId);
}
