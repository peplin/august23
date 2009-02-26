package twoverse.object;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Galaxy extends CelestialBody implements Serializable {
    private GalaxyShape mShape;
    private double mMass;
    private double mDensity;
    private static final long serialVersionUID = 4163663398347532933L;
    private static Properties sConfigFile;

    /**
     * A new client side galaxy, ID and birth are set and returned by the server
     * 
     * @param ownerId
     * @param name
     * @param parentId
     * @param position
     * @param velocity
     * @param acceleration
     * @param shape
     * @param mass
     * @param density
     */
    public Galaxy(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            GalaxyShape shape, double mass, double density) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(shape, mass, density);
    }

    public Galaxy(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            GalaxyShape shape, double mass, double density) {
        super(id, ownerId, name, birthTime, deathTime, parentId, position,
                velocity, acceleration);
        loadConfig();
        initialize(shape, mass, density);
    }

    public Galaxy(CelestialBody body, GalaxyShape shape, double mass,
            double density) {
        super(body);
        loadConfig();
        initialize(shape, mass, density);
    }

    public Galaxy(Element element) {
        //TODO can't pull this out to config as config isn't loaded
        super(element.getFirstChildElement("CelestialBody"));
        loadConfig();

        if (!element.getLocalName().equals(
                sConfigFile.getProperty("GALAXY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a galaxy");
        }

        Element shapeElement = element.getFirstChildElement(sConfigFile
                .getProperty("GALAXY_SHAPE_TAG"));
        GalaxyShape shape = new GalaxyShape(shapeElement);

        double mass = Double.valueOf(element.getAttribute(
                sConfigFile.getProperty("MASS_ATTRIBUTE_TAG")).getValue());

        double density = Double.valueOf(element.getAttribute(
                sConfigFile.getProperty("DENSITY_ATTRIBUTE_TAG")).getValue());

        initialize(shape, mass, density);
    }

    private void initialize(GalaxyShape shape, double mass, double density) {
        setShape(shape);
        setMass(mass);
        setDensity(density);
    }
    
    private synchronized void loadConfig() {
        if (sConfigFile == null) {
            sConfigFile = loadConfigFile("Galaxy");
        }
    }

    public void setShape(GalaxyShape shape) {
        mShape = shape;
    }

    public GalaxyShape getShape() {
        return mShape;
    }

    public void setMass(double mass) {
        mMass = mass;
    }

    public double getMass() {
        return mMass;
    }

    public void setDensity(double density) {
        mDensity = density;
    }

    public double getDensity() {
        return mDensity;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("GALAXY_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile
                .getProperty("MASS_ATTRIBUTE_TAG"), String.valueOf(mMass)));
        root
                .addAttribute(new Attribute(sConfigFile
                        .getProperty("DENSITY_ATTRIBUTE_TAG"), String
                        .valueOf(mDensity)));
        root.appendChild(mShape.toXmlElement());
        return root;
    }
}
