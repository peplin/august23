package twoverse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.Database;

public class ObjectManagerServer extends ObjectManager {

	public ObjectManagerServer(Database database) {
		super(database);
		// TODO Auto-generated constructor stub
		        
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
      
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        /* Load handler definitions from a property file.
         * The property file might look like:
         *   Calculator=org.apache.xmlrpc.demo.Calculator
         *   org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.AdderImpl
         */
        phm.load(Thread.currentThread().getContextClassLoader(),
                 "MyHandlers.properties");

        /* You may also provide the handler classes directly,
         * like this:
         * phm.addHandler("Calculator",
         *     org.apache.xmlrpc.demo.Calculator.class);
         * phm.addHandler(org.apache.xmlrpc.demo.proxy.Adder.class.getName(),
         *     org.apache.xmlrpc.demo.proxy.AdderImpl.class);
         */
        xmlRpcServer.setHandlerMapping(phm);
      
        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();

	}
	
	public void run() {
		
	}
	
	public void publishFeed() {		   
	    Element root = new Element("root");    
	    root.appendChild("Hello World!");
	    Document doc = new Document(root);
	    try {
	    	FileOutputStream xmlFeedFile = new FileOutputStream("feed.xml");
	    	OutputStream bufferedXmlFeedFile =
						new BufferedOutputStream(xmlFeedFile);
	    	OutputStreamWriter outStream =
	    				new OutputStreamWriter(bufferedXmlFeedFile, "8859_1");
	    	outStream.write(doc.toXML());
	    	outStream.flush();
	    	outStream.close();
	    } catch (IOException ex) {
	    	System.err.println(ex); 
	    }
	}
	
}
