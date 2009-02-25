package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;

public class ManmadeBody extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -6112151366389080968L;
    private static Properties sConfigFile;

    public ManmadeBody(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
    }

    // This class stands for both satellites and deep space probes - one
    // is just orbiting
    public ManmadeBody(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        loadConfig();
    }

    public ManmadeBody(CelestialBody body) {
        super(body);
        loadConfig();
    }

    public ManmadeBody(Element element) {
        //TODO same problem as in galaxy
        super(element.getFirstChildElement("CelestialBody"));
        loadConfig();
    }

    
    private synchronized void loadConfig() {
        if (sConfigFile == null) {
            sConfigFile = loadConfigFile("ManmadeBody");
        }
    }
    
    @Override
    public Element toXmlElement() {
        Element root = new Element(sConfigFile.getProperty("MANMADE_BODY_TAG"));
        root.appendChild(super.toXmlElement());
        return root;
    }
}
