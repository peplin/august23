package twoverse;

import java.io.IOException;
import java.util.Properties;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.Database;

public class ObjectManagerClient extends ObjectManager {

	public ObjectManagerClient(Database database) {
		super(database);
		// TODO Auto-generated constructor stub
		mParser = new Builder();
		
		try {
	        mConfigFile.load(this.getClass().
	        				getClassLoader().getResourceAsStream(
	        				"../config/ObjectManagerClient.properties"));
    	} catch (IOException e) {
    	
    	}
	}

	public void run() {
		
	}
	
	public void pullFeed() {
		try {
			Document doc = mParser.build(mConfigFile.getProperty("FEED"));
			Elements children = doc.getRootElement().getChildElements();
			// TODO design feed, figure out how to parse it
		}
		catch (ParsingException ex) {
			System.err.println("Feed is malformed");
		}
		catch (IOException ex) {
			System.err.println("Unable to connect to feed");
		}
	}
	
	private Builder mParser;
    private Properties mConfigFile;
}
