package twoverse.object;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class PlanetarySystem extends CelestialBody implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1152118681822794656L;
    private Properties mConfigFile;
    private int mCenterId;
    private double mMass;

    public PlanetarySystem(int ownerId, String name, int parentId,
                           Point position, PhysicsVector3d velocity,
                           PhysicsVector3d acceleration, int centerStarId,
                           double mass) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(centerStarId, mass);
    }

    public PlanetarySystem(int id, int ownerId, String name,
                           Timestamp birthTime, Timestamp deathTime,
                           int parentId, Point position,
                           PhysicsVector3d velocity,
                           PhysicsVector3d acceleration, int centerStarId,
                           double mass) {
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
        loadConfig();

        if(!element.getLocalName().equals(
            mConfigFile.getProperty("PLANETARY_SYSTEM_TAG"))) {
            throw new UnexpectedXmlElementException(
                    "Element is not a planetary system");
        }

        int centerStarId =
                Integer.valueOf(element.getAttribute(
                    mConfigFile.getProperty("CENTER_ID_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(
                    mConfigFile.getProperty("MASS_ATTRIBUTE_TAG")).getValue());

        initialize(centerStarId, mass);
    }

    private void loadConfig() {
        try {
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream(
                        "twoverse/conf/PlanetarySystem.properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to laod config: "
                + e.getMessage(), e);
        }
    }

    private void initialize(int centerStarId, double mass) {
        setCenter(centerStarId);
        setMass(mass);
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
        Element root =
                new Element(mConfigFile.getProperty("PLANETARY_SYSTEM_TAG"));
        super.appendXmlAttributes(root);
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("CENTER_ID_ATTRIBUTE_TAG"), String
                .valueOf(mCenterId)));
        root.addAttribute(new Attribute(mConfigFile
                .getProperty("MASS_ATTRIBUTE_TAG"), String.valueOf(mMass)));
        return root;
    }
}
