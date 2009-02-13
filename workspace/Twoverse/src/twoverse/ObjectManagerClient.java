package twoverse;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.apache.xmlrpc.client.XmlRpcClient;

public class ObjectManagerClient extends ObjectManager {
    private Builder mParser;
    private Properties mConfigFile;
    private XmlRpcClient mXmlRpcClient;

    public ObjectManagerClient() {
        super();
        mParser = new Builder();

        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                        "twoverse/conf/ObjectManagerClient.properties"));
        } catch (IOException e) {

        }
    }

    public void pullFeed() {
    	/** TODO
    	 * pull feed
    	 * parse objects into a hash map
    	 * match our current map to this new one
    	 * 		update as we go, remove from the xml feed map after
    	 * 		remove as we go if not in xml feed map
    	 * now reverse, all remaining in xml feed map are new objs
    	 * 	add to our map
    	 */
        try {
            Document doc = mParser.build(mConfigFile.getProperty("FEED"));
            Elements children = doc.getRootElement().getChildElements();
            // TODO figure out how to parse feed
        } catch (ParsingException e) {
            sLogger.log(Level.WARNING, "Feed may be malformed", e);
        } catch (IOException e) {
            sLogger.log(Level.WARNING, "Unable to connect to feed", e);
        }
    }

}
