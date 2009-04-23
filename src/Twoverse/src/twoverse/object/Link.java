/**
 * Twoverse Link Object
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.object;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

/**
 * A meta object that links two Star objects together. Useful for creating
 * constellations.<br><br>
 * 
 * This object is the only body in Twoverse that does not inherit from
 * CelestialBody. This is a good case for separating out the database
 * functionality of CelestialBody from the positioning attributes. Either that,
 * or create a new parent type for these meta-objects.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
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

    private synchronized void loadConfig() {
        if(sLinkConfigFile == null) {
            sLinkConfigFile = loadConfigFile("Link");
        }
    }

    private synchronized Properties loadConfigFile(String className) {
        Properties configFile = null;
        try {
            configFile = new Properties();
            configFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/" + className
                            + ".properties"));
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, "Unable to load config: "
                    + e.getMessage(), e);
        }
        return configFile;
    }

    private static Logger getLogger() {
        return sLogger;
    }

    private static Link parse(ResultSet resultSet) {
        Link link = null;
        try {
            link =
                    new Link(resultSet.getInt("link.id"),
                            resultSet.getInt("link.first"),
                            resultSet.getInt("link.second"));
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to parse links from set: "
                    + resultSet, e);
        }
        return link;
    }

    /**
     * Construct a new link with the IDs of the bodies it links.
     * 
     * @param id
     *            of this link, must be a valid link ID on the server
     * @param firstId
     *            ID of first body in link
     * @param secondId
     *            ID of second body in link
     */
    public Link(int id, int firstId, int secondId) {
        mId = id;
        mFirstId = firstId;
        mSecondId = secondId;
    }

    /**
     * Construct a new link with two Star objects. Figures out the IDs on its
     * own as a convenience. Order of stars (first, second) makes no difference.
     * 
     * @param first
     *            first star in link
     * @param second
     *            second star in link
     */
    public Link(Star first, Star second) {
        mFirstId = first.getId();
        mSecondId = second.getId();
    }

    /**
     * Construct a new link from an XML element.
     * 
     * @param element
     *            XML element that contains a Link
     */
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

    /**
     * Constructs an open link with only one Star. This instance cannot be
     * stored in the database until it receives a second Star.
     * 
     * @param first
     *            the opening Star for the link
     */
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

    /**
     * Store database connection and initialize all statements with it. This
     * should needs to be called once, and only once per run of the program.
     * 
     * @param connection
     *            a database connection that will be used exclusively by this
     *            class (ie. must be a fresh copy of another connection)
     * @throws SQLException
     *             if unable to prepare statements
     */
    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        sInsertStatement =
                sConnection.prepareStatement("INSERT INTO link (first, second) "
                        + "VALUES (?, ?)");
        sSelectAllLinksStatement =
                sConnection.prepareStatement("SELECT * FROM link");
    }

    /**
     * Insert this link into the database. After a successful insert, it will
     * have a valid ID.
     * 
     * @throws SQLException
     *             if unable to insert
     */
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
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Add link query failed for link: "
                    + this, e);
            throw new SQLException("Add link query failed for link: " + this);
        }
    }

    /**
     * Select all links from the database.
     * 
     * @return all links from database, mapped by ID
     * @throws SQLException
     *             if unable to select
     */
    public static synchronized HashMap<Integer, Link> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, Link> links = new HashMap<Integer, Link>();
        try {
            ResultSet resultSet = sSelectAllLinksStatement.executeQuery();
            while (resultSet.next()) {
                Link link = parse(resultSet);
                links.put(link.getId(), link);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get stars", e);
        }
        return links;
    }

    /**
     * Convert this Link to an XML element.
     * 
     * @return this link as an XML element
     */
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

    @Override
    public String toString() {
        return "[id: " + getId() + ", " + "first id: " + getFirstId() + ", "
                + "second id: " + getSecondId() + "]";
    }
}
