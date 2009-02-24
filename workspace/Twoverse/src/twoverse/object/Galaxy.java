package twoverse.object;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.RequestHandlerClient;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Galaxy extends CelestialBody implements Serializable {
    private GalaxyShape mShape;
    private double mMass;
    private double mDensity;
    private static final long serialVersionUID = 4163663398347532933L;
    private Properties mConfigFile;

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
        super(element);
        loadConfig();

        if(!element.getLocalName()
                .equals(mConfigFile.getProperty("GALAXY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a galaxy");
        }

        Element shapeElement =
                element.getFirstChildElement(mConfigFile
                        .getProperty("GALAXY_SHAPE_TAG"));
        GalaxyShape shape = new GalaxyShape(shapeElement);

        double mass =
                Double.valueOf(element.getAttribute(
                    mConfigFile.getProperty("MASS_ATTRIBUTE_TAG")).getValue());

        double density =
                Double.valueOf(element.getAttribute(
                    mConfigFile.getProperty("DENSITY_ATTRIBUTE_TAG"))
                        .getValue());

        initialize(shape, mass, density);
    }

    private void initialize(GalaxyShape shape, double mass, double density) {
        setShape(shape);
        setMass(mass);
        setDensity(density);
    }

    private void loadConfig() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("twoverse/conf/Galaxy.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                + e.getMessage(), e);
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
        Element root = new Element(mConfigFile.getProperty("GALAXY_TAG"));
        super.appendXmlAttributes(root);
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("MASS_ATTRIBUTE_TAG"), String.valueOf(mMass)));
        root
                .addAttribute(new Attribute(mConfigFile
                        .getProperty("DENSITY_ATTRIBUTE_TAG"), String
                        .valueOf(mDensity)));
        root.appendChild(mShape.toXmlElement());
        return root;
    }
}
