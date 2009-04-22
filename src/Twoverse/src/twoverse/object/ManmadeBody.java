/**
 * Twoverse Manmade Body Object
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

import nu.xom.Element;
import processing.core.PApplet;
import twoverse.object.applet.AppletBodyInterface;
import twoverse.object.applet.AppletManmadeBody;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.XmlExceptions.UnexpectedXmlElementException;

public class ManmadeBody extends CelestialBody implements Serializable {
    private static final long serialVersionUID = -6112151366389080968L;
    private static Properties sConfigFile;
    private static PreparedStatement sSelectAllManmadeBodiesStatement;
    private static PreparedStatement sInsertManmadeBodyStatement;
    private static Connection sConnection;

    public ManmadeBody(int ownerId, String name, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration) {
        super(ownerId, name, parentId, position, velocity, acceleration);
        loadConfig();
    }

    // This class stands for both satellites and deep space probes - one
    // is just orbiting
    public ManmadeBody(int id, int ownerId, String name, Timestamp birthTime,
            Timestamp deathTime, int parentId, Point position,
            PhysicsVector3d velocity, PhysicsVector3d acceleration,
            Vector<Integer> children) {
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
    }

    public ManmadeBody(CelestialBody body) {
        super(body);
        loadConfig();
    }

    public ManmadeBody(Element element) {
        super(element.getFirstChildElement(CelestialBody.XML_TAG));
        loadConfig();

        if(!element.getLocalName()
                .equals(sConfigFile.getProperty("MANMADE_BODY_TAG"))) {
            throw new UnexpectedXmlElementException("Element is not a manmade body");
        }
    }

    private synchronized void loadConfig() {
        if(sConfigFile == null) {
            sConfigFile = loadConfigFile("ManmadeBody");
        }
    }

    @Override
    public AppletBodyInterface getAsApplet(PApplet parent) {
        return new AppletManmadeBody(parent, this);
    }

    public static void prepareDatabaseStatements(Connection connection)
            throws SQLException {
        sConnection = connection;
        // super.prepareDatabaseStatements(sConnection);
        sSelectAllManmadeBodiesStatement =
                sConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN manmade " + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)");
        sInsertManmadeBodyStatement =
                sConnection.prepareStatement("INSERT INTO manmade (id) "
                        + "VALUES (?)");
    }

    public static synchronized HashMap<Integer, CelestialBody> selectAllFromDatabase()
            throws SQLException {
        HashMap<Integer, CelestialBody> manmadeBodies =
                new HashMap<Integer, CelestialBody>();
        try {
            ResultSet resultSet =
                    sSelectAllManmadeBodiesStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseAll(resultSet);
            resultSet.beforeFirst();
            for(CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between manmade and celestial bodies");
                }
                ManmadeBody manmadeBody = new ManmadeBody(body);
                manmadeBody.setDirty(false);
                manmadeBodies.put(manmadeBody.getId(), manmadeBody);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get manmade bodies", e);
        }
        return manmadeBodies;
    }

    @Override
    public synchronized void insertInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to add manmade body: " + this);
        try {
            super.insertInDatabase();

            sInsertManmadeBodyStatement.setInt(1, getId());
            sInsertManmadeBodyStatement.executeUpdate();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Could not add manmade body " + this, e);
        }
    }

    @Override
    public synchronized void updateInDatabase() throws SQLException {
        sLogger.log(Level.INFO, "Attempting to update manmade body: " + this);
        try {
            super.updateInDatabase();
            setDirty(false);
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Could not update manmade body " + this,
                    e);
        }
    }

    @Override
    public Element toXmlElement() {
        Element root = new Element(sConfigFile.getProperty("MANMADE_BODY_TAG"));
        root.appendChild(super.toXmlElement());
        return root;
    }
}
