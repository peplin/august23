package twoverse;

import java.util.ArrayList;
import java.util.HashMap;

import twoverse.util.Database;
import twoverse.util.User;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

class UnhandledCelestialBodyException extends Exception {
    public UnhandledCelestialBodyException() {

    }
}

public abstract class ObjectManager extends Thread {

    public ObjectManager(Database database) {
        // TODO Auto-generated constructor stub
    }

    public abstract void run();

    public ArrayList<CelestialBody> getAllBodies() {
        ArrayList<CelestialBody> allBodies = new ArrayList<CelestialBody>();
        allBodies.addAll(mGalaxies.values());
        allBodies.addAll(mPlanetarySystems.values());
        allBodies.addAll(mManmadeBodies.values());
        return allBodies;
    }

    public ArrayList<Galaxy> getGalaxies() {
        return new ArrayList<Galaxy>(mGalaxies.values());
    }

    public ArrayList<PlanetarySystem> getPlanetarySystems() {
        return new ArrayList<PlanetarySystem>(mPlanetarySystems.values());
    }

    public ArrayList<ManmadeBody> getManmadeBodies() {
        return new ArrayList<ManmadeBody>(mManmadeBodies.values());
    }

    public void getOwnedBodies(User user) {

    }

    public Galaxy getGalaxy(int id) {
        return mGalaxies.get(id);
    }

    public PlanetarySystem getPlanetarySystem(int id) {
        return mPlanetarySystems.get(id);
    }

    public ManmadeBody getManmadeBody(int id) {
        return mManmadeBodies.get(id);
    }

    /**
     * If body exists, replaces with updatedBody. If not, inserts it in the
     * ObjectManager.
     * 
     * So, if this is a new object coming in over an XML feed, i need to match
     * it with its ID. Okay.
     * 
     * TODO How do I make sure this doesn't replace some metadata update that's
     * already happened?
     */
    public void update(Galaxy newGalaxy) {
        mGalaxies.put(newGalaxy.getId(), newGalaxy);
    }

    public void update(PlanetarySystem newSystem) {
        mPlanetarySystems.put(newSystem.getId(), newSystem);
    }

    public void update(ManmadeBody newManmadeBody) {
        mManmadeBodies.put(newManmadeBody.getId(), newManmadeBody);
    }

    HashMap<Integer, Galaxy> mGalaxies;
    HashMap<Integer, PlanetarySystem> mPlanetarySystems;
    HashMap<Integer, ManmadeBody> mManmadeBodies;
    Database mDatabase;
}
