package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class PlanetarySystem extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private int mCenterId;
    private double mMass;

    public PlanetarySystem(int ownerId, String name, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, int centerStarId, double mass) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(int id, int ownerId, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, int centerStarId, double mass) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(CelestialBody body, int centerStarId, double mass) {
        super(body);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if (!element.getLocalName().equals(
                sConfigFile.getProperty("PLANETARY_SYSTEM_TAG"))) {
            throw new UnexpectedXmlElementException(
                    "Element is not a planetary system");
        }

        int centerStarId = Integer.valueOf(element.getAttribute(
                sConfigFile.getProperty("CENTER_ID_ATTRIBUTE_TAG")).getValue());

        double mass = Double.valueOf(element.getAttribute(
                sConfigFile.getProperty("MASS_ATTRIBUTE_TAG")).getValue());

        initialize(centerStarId, mass);
    }

    public PlanetarySystem(PlanetarySystem system) {
		super(system);
		initialize(system.getCenterId(), system.getMass());
	}

	private void initialize(int centerStarId, double mass) {
        setCenter(centerStarId);
        setMass(mass);
    }
    
    private synchronized void loadConfig() {
        if (sConfigFile == null) {
            sConfigFile = loadConfigFile("PlanetarySystem");
        }
    }

    public void setCenter(int center) {
        mCenterId = center;
    }

    public int getCenterId() {
        return mCenterId;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile
                .getProperty("PLANETARY_SYSTEM_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile
                .getProperty("CENTER_ID_ATTRIBUTE_TAG"), String
                .valueOf(mCenterId)));
        root.addAttribute(new Attribute(sConfigFile
                .getProperty("MASS_ATTRIBUTE_TAG"), String.valueOf(mMass)));
        return root;
    }
}
