package twoverse;

import java.util.ArrayList;
import java.util.HashMap;

import twoverse.util.Database;
import twoverse.util.User;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

public abstract class ObjectManager extends Thread {

	public ObjectManager(Database database) {
		// TODO Auto-generated constructor stub
	}

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
	
	public void add(CelestialBody body) {
		if(body.getClass().toString() == "Galaxy") {
			addGalaxy((Galaxy) body);
		} else if(body.getClass().toString() == "PlanetarySystem") {
			addPlanetarySystem((PlanetarySystem) body);
		} else if(body.getClass().toString() == "ManmadeBody") {
			addManmadeBody((ManmadeBody) body);
		}
	}
	
	private void addGalaxy(Galaxy galaxy) {
		
	}
	
	private void addPlanetarySystem(PlanetarySystem system) {
		
	}
	
	private void addManmadeBody(ManmadeBody body) {
		
	}
	
	
	
	HashMap<Integer, Galaxy> mGalaxies;
	HashMap<Integer, PlanetarySystem> mPlanetarySystems;
	HashMap<Integer, ManmadeBody> mManmadeBodies;
	Database mDatabase;
}
