package twoverse.object;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

import nu.xom.Attribute;
import nu.xom.Element;

public class Link implements Serializable {
    private static final long serialVersionUID = -382030465269046974L;
    private static Properties sLinkConfigFile;
    protected static Logger sLogger =
            Logger.getLogger(CelestialBody.class.getName());
    private static PreparedStatement sSelectAllLinksStatement;
    private static PreparedStatement sInsertStatement;
    private static Connection sConnection;
    private int mId;
    private int mFirstId;
    private int mSecondId;

    public Link(int id, int firstId, int secondId) {
        mId = id;
        mFirstId = firstId;
        mSecondId = secondId;
    }

    public Link(Star first, Star second) {
        mFirstId = first.getId();
        mSecondId = second.getId();
    }

    public Link(Element element) {
        loadConfig();

        if(!element.getLocalName()
                .equals(sLinkConfigFile.getProperty("LINK_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a link");
        }

        mId =
                Integer.valueOf(element.getAttribute(sLinkConfigFile.getProperty("ID_ATTRIBUTE_TAG"))
                        .getValue());
        mFirstId =
                Integer.valueOf(element.getAttribute(sLinkConfigFile.getProperty("FIRST_ATTRIBUTE_TAG"))
                        .getValue());
        mSecondId =
                Integer.valueOf(element.getAttribute(sLinkConfigFile.getProperty("SECOND_ATTRIBUTE_TAG"))
                        .getValue());
    }

    private synchronized void loadConfig() {
        if(sLinkConfigFile == null) {
            sLinkConfigFile = loadConfigFile("Link");
        }
    }

    protected synchronized Properties loadConfigFile(String className) {
        Properties configFile = null;
        try {
            configFile = new Properties();
            configFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/" + className
                            + ".properties"));
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, "Unable to load config: "
                    + e.getMessage(), e);
        }
        return configFile;
    }

    protected static Logger getLogger() {
        return sLogger;
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sInsertStatement =
                sConnection.prepareStatement("INSERT INTO link (first, second) "
                        + "VALUES (?, ?)");
    }

    public Link(Star first) {
        mFirstId = first.getId();
        mSecondId = 0;
    }

    public int getFirstId() {
        return mFirstId;
    }

    public int getSecondId() {
        return mSecondId;
    }

    public void setFirst(Star first) {
        mFirstId = first.getId();
    }

    public void setSecond(Star second) {
        mSecondId = second.getId();
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public synchronized void insertInDatabase() throws SQLException {
        if(sConnection == null) {
            throw new SQLException("Unset database connection");
        }
        try {
            sInsertStatement.setInt(1, getFirstId());
            sInsertStatement.setInt(2, getSecondId());
            sInsertStatement.executeUpdate();
            ResultSet keySet = sInsertStatement.getGeneratedKeys();
            if(!keySet.next()) {
                throw new SQLException("Couldn't find key of object we just added");
            }
            setId(keySet.getInt(1));
            keySet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Add link query failed for link: "
                    + this, e);
            throw new SQLException("Add link query failed for link: " + this);
        }
    }

    public static synchronized HashMap<Integer, Link> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, Link> links = new HashMap<Integer, Link>();
        try {
            ResultSet resultSet = sSelectAllLinksStatement.executeQuery();
            while(resultSet.next()) {
                Link link = parse(resultSet);
                links.put(link.getId(), link);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get stars", e);
        }
        return links;
    }

    static protected Link parse(ResultSet resultSet) {
        Link link = null;
        try {
            link =
                    new Link(resultSet.getInt("link.id"),
                            resultSet.getInt("link.first"),
                            resultSet.getInt("link.second"));
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to parse links from set: "
                    + resultSet, e);
        }
        return link;
    }

    public Element toXmlElement() {
        loadConfig();
        Element element = new Element(sLinkConfigFile.getProperty("LINK_TAG"));

        element.addAttribute(new Attribute(sLinkConfigFile.getProperty("ID_ATTRIBUTE_TAG"),
                String.valueOf(mId)));
        element.addAttribute(new Attribute(sLinkConfigFile.getProperty("FIRST_ATTRIBUTE_TAG"),
                String.valueOf(mFirstId)));
        element.addAttribute(new Attribute(sLinkConfigFile.getProperty("SECOND_ATTRIBUTE_TAG"),
                String.valueOf(mSecondId)));
        return element;
    }

    public String toString() {
        return "[id: " + getId() + ", " + "first id: " + getFirstId() + ", "
                + "second id: " + getSecondId() + "]";
    }
}
