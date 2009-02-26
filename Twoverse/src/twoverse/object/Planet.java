package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Planet extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private double mRadius;
    private double mMass;

    public Planet(int ownerId, String name, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, double mass, double radius) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(radius, mass);
    }

    public Planet(int id, int ownerId, String name,
            Timestamp birthTime, Timestamp deathTime, int parentId,
            Point position, PhysicsVector3d velocity,
            PhysicsVector3d acceleration, double mass, double radius) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        loadConfig();
        initialize(radius, mass);
    }

    public Planet(CelestialBody body, double mass, double radius) {
        super(body);
        loadConfig();
        initialize(radius, mass);
    }

    public Planet(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if (!element.getLocalName().equals(
                sConfigFile.getProperty("PLANET_TAG"))) {
            throw new UnexpectedXmlElementException(
                    "Element is not a planetary system");
        }

        double radius = Double.valueOf(element.getAttribute(
                sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG")).getValue());

        double mass = Double.valueOf(element.getAttribute(
                sConfigFile.getProperty("MASS_ATTRIBUTE_TAG")).getValue());

        initialize(radius, mass);
    }

    public Planet(Planet planet) {
		super(planet);
		initialize(planet.getRadius(), planet.getMass());
	}

	private void initialize(double radius, double mass) {
        setRadius(radius);
        setMass(mass);
    }
    
    private synchronized void loadConfig() {
        if (sConfigFile == null) {
            sConfigFile = loadConfigFile("Planet");
        }
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    public void setRadius(double mRadius) {
        this.mRadius = mRadius;
    }

    public double getRadius() {
        return mRadius;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile
                .getProperty("PLANET_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile
                .getProperty("RADIUS_ATTRIBUTE_TAG"), String
                .valueOf(getRadius())));
        root.addAttribute(new Attribute(sConfigFile
                .getProperty("MASS_ATTRIBUTE_TAG"), String.valueOf(getMass())));
        return root;
    }
}
