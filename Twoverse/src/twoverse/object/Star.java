package twoverse.object;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import processing.core.PApplet;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.object.applet.AppletStar;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class Star extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -1152118681822794656L;
    private static Properties sConfigFile;
    private double mRadius;
    private double mMass;
    private int mColorR;
    private int mColorG;
    private int mColorB;
    private double mLuminosity;
    private double mFrequency;
    private int mState = 5;
    private static PreparedStatement sSelectAllStarsStatement;
    private static PreparedStatement sInsertStarStatement;
    private static PreparedStatement sUpdateStarStatement;
    private static Connection sConnection;

    public Star(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            double mass, double radius, int colorR, int colorB, int colorG,
            double luminosity, double frequency) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    public Star(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children, double mass, double radius, int colorR,
            int colorB, int colorG, double luminosity, double frequency) {
        super(id,
                ownerId,
                name,
                birthTime,
                deathTime,
                parentId,
                position,
                velocity,
                acceleration,
                children);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    public Star(CelestialBody body, double mass, double radius, int colorR,
            int colorB, int colorG, double luminosity, double frequency) {
        super(body);
        loadConfig();
        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    public Star(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName().equals(sConfigFile.getProperty("STAR_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a star");
        }

        double radius =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"))
                        .getValue());

        double mass =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"))
                        .getValue());

        int colorR =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_R_ATTRIBUTE_TAG"))
                        .getValue());
        int colorG =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_G_ATTRIBUTE_TAG"))
                        .getValue());
        int colorB =
                Integer.valueOf(element.getAttribute(sConfigFile.getProperty("COLOR_B_ATTRIBUTE_TAG"))
                        .getValue());

        double luminosity =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("LUMINOSITY_ATTRIBUTE_TAG"))
                        .getValue());

        double frequency =
                Double.valueOf(element.getAttribute(sConfigFile.getProperty("FREQUENCY_ATTRIBUTE_TAG"))
                        .getValue());

        setState(Integer.valueOf(element.getAttribute(sConfigFile.getProperty("STATE_ATTRIBUTE_TAG"))
                .getValue()));

        initialize(radius, mass, colorR, colorG, colorB, luminosity, frequency);
    }

    public Star(Star star) {
        super(star);
        mState = star.getState();
        initialize(star.getRadius(),
                star.getMass(),
                star.getColorR(),
                star.getColorG(),
                star.getColorB(),
                star.getLuminosity(),
                star.getFrequency());
    }

    private void initialize(double radius, double mass, int colorR, int colorB,
            int colorG, double luminosity, double frequency) {
        setRadius(radius);
        setMass(mass);
        setColorR(colorR);
        setColorG(colorG);
        setColorB(colorB);
        setLuminosity(luminosity);
        setFrequency(frequency);
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("Star");
        }
    }

    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletStar(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sSelectAllStarsStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN star " + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)" + " LEFT JOIN (state) "
                        + "ON (object.state = state.id)");
        sInsertStarStatement =
                sConnection.prepareStatement("INSERT INTO star (id, mass, radius, colorR, colorB, colorG, luminosity, frequency, state) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        sUpdateStarStatement =
                sConnection.prepareStatement("UPDATE star "
                        + "SET mass = ?,"
                        + " radius = ?, colorR = ?, colorG = ?, colorB = ?, luminosity = ?, frequency = ?, state = ? "
                        + "WHERE id = ?");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> stars =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet = sSelectAllStarsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between stars and celestial bodies");
                }
                Star star =
                        new Star(body,
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("radius"),
                                resultSet.getInt("colorR"),
                                resultSet.getInt("colorG"),
                                resultSet.getInt("colorB"),
                                resultSet.getDouble("luminosity"),
                                resultSet.getDouble("frequency"));
                star.setState(resultSet.getInt("state"));
                star.setDirty(false);
                stars.put(star.getId(), star);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get stars", e);
        }
        return stars;
    }

    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add star: " + this);
        try {
            super.insertInDatabase();

            sInsertStarStatement.setInt(1, getId());
            sInsertStarStatement.setDouble(2, getMass());
            sInsertStarStatement.setDouble(3, getRadius());
            sInsertStarStatement.setInt(4, getColorR());
            sInsertStarStatement.setInt(5, getColorG());
            sInsertStarStatement.setInt(6, getColorB());
            sInsertStarStatement.setDouble(7, getLuminosity());
            sInsertStarStatement.setDouble(8, getFrequency());
            sInsertStarStatement.setInt(9, getState());

            sInsertStarStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + this, e);
        }
    }

    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update star: " + this);
        try {
            super.updateInDatabase();

            sUpdateStarStatement.setDouble(1, getMass());
            sUpdateStarStatement.setDouble(2, getRadius());
            sInsertStarStatement.setInt(3, getColorR());
            sInsertStarStatement.setInt(4, getColorG());
            sInsertStarStatement.setInt(5, getColorB());
            sInsertStarStatement.setDouble(6, getLuminosity());
            sInsertStarStatement.setDouble(7, getFrequency());
            sInsertStarStatement.setInt(8, getState());
            sUpdateStarStatement.setDouble(9, getId());
            sUpdateStarStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not update starary system "
                    + this, e);
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

    public void setColorR(int colorR) {
        mColorR = colorR;
    }

    public void setColorG(int colorG) {
        mColorG = colorG;
    }

    public void setColorB(int colorB) {
        mColorB = colorB;
    }

    public int getColorR() {
        return mColorR;
    }

    public int getColorG() {
        return mColorG;
    }

    public int getColorB() {
        return mColorB;
    }

    public void setLuminosity(double luminosity) {
        mLuminosity = luminosity;
    }

    public double getLuminosity() {
        return mLuminosity;
    }

    public void setFrequency(double frequency) {
        mFrequency = frequency;
    }

    public double getFrequency() {
        return mFrequency;
    }

    public void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    @Override
    public Element toXmlElement() {
        loadConfig();
        Element root = new Element(sConfigFile.getProperty("STAR_TAG"));
        root.appendChild(super.toXmlElement());
        root.addAttribute(new Attribute(sConfigFile.getProperty("RADIUS_ATTRIBUTE_TAG"),
                String.valueOf(getRadius())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("MASS_ATTRIBUTE_TAG"),
                String.valueOf(getMass())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_R_ATTRIBUTE_TAG"),
                String.valueOf(getColorR())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_G_ATTRIBUTE_TAG"),
                String.valueOf(getColorG())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("COLOR_B_ATTRIBUTE_TAG"),
                String.valueOf(getColorB())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("LUMINOSITY_ATTRIBUTE_TAG"),
                String.valueOf(getLuminosity())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("FREQUENCY_ATTRIBUTE_TAG"),
                String.valueOf(getFrequency())));
        root.addAttribute(new Attribute(sConfigFile.getProperty("STATE_ATTRIBUTE_TAG"),
                String.valueOf(getState())));
        return root;
    }
}
