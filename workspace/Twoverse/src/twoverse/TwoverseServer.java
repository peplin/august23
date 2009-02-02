package twoverse;

import twoverse.util.Database;
import twoverse.util.DatabaseException;

public class TwoverseServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database database = new Database();
			ObjectManager objectManager = new ObjectManager(database);
			SessionManager sessionManager
					= new SessionManager(database, objectManager);
			SimulationRunner simulation = new SimulationRunner(objectManager);
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

	}

}
