/**
 * Twoverse Database
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

package twoverse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Link;
import twoverse.object.Star;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

/**
 * Database connection manager for Twoverse. All database queries are routed
 * through here, and database connections are divvied out from here as well.
 * 
 * Originally, all of the SQL queries for each object were stored in this class.
 * This was difficult to maintain, as every new object required multiple files
 * to be changed all across the package. Now, each object implements an
 * interface that requires it to implement its own database queries. This class
 * then call those functions to update/insert/delete objects from the database.
 * 
 * The only problem with this solution is the management of Connection objects.
 * In order to create a PreparedStatement object, each class needs the database
 * connection. So, each object must be initialized with a copy of the connection
 * first thing at runtime, or none of these database functions will work.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class Database {
    /**
     * Database driver for this class to use
     * 
     * @value
     */
    private final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mDeleteUserStatement;
    private PreparedStatement mSelectAllUsersStatement;
    private PreparedStatement mUpdateSimDataStatement;
    private static Logger sLogger = Logger.getLogger(Database.class.getName());

    private void updateSimulationBody(CelestialBody body) throws SQLException {
        try {
            mUpdateSimDataStatement.setDouble(1, body.getPosition().getX());
            mUpdateSimDataStatement.setDouble(2, body.getPosition().getY());
            mUpdateSimDataStatement.setDouble(3, body.getPosition().getZ());
            mUpdateSimDataStatement.setDouble(4, body.getVelocity()
                    .getMagnitude());
            mUpdateSimDataStatement.setDouble(5, body.getVelocity()
                    .getUnitDirection()
                    .getX());
            mUpdateSimDataStatement.setDouble(6, body.getVelocity()
                    .getUnitDirection()
                    .getY());
            mUpdateSimDataStatement.setDouble(7, body.getVelocity()
                    .getUnitDirection()
                    .getZ());
            mUpdateSimDataStatement.setDouble(8, body.getAcceleration()
                    .getMagnitude());
            mUpdateSimDataStatement.setDouble(9, body.getAcceleration()
                    .getUnitDirection()
                    .getX());
            mUpdateSimDataStatement.setDouble(10, body.getAcceleration()
                    .getUnitDirection()
                    .getY());
            mUpdateSimDataStatement.setDouble(11, body.getAcceleration()
                    .getUnitDirection()
                    .getZ());
            mUpdateSimDataStatement.setInt(12, body.getId());
            mUpdateSimDataStatement.executeUpdate();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING,
                    "Update celestial body query failed for body: " + body,
                    e);
        } catch(TwoDimensionalException e) {
            sLogger.log(Level.WARNING,
                    "Expected 3D point but was 2D: " + body,
                    e);
        }
    }

    /**
     * Initializes statements for all database queries.
     * 
     * Each new object type must be added here in order to initialize its own
     * statements.
     * 
     * @throws DatabaseException
     *             if unable to prepare statments
     */
    private void prepareStatements() throws DatabaseException {
        try {
            mAddUserStatement =
                    mConnection.prepareStatement("INSERT INTO user (username, password, email, sms) "
                            + "VALUES (?, ?, ?, ?)");
            mUpdateUserLastLoginStatement =
                    mConnection.prepareStatement("UPDATE user "
                            + "SET last_login = NOW() " + "WHERE id = ?");
            mDeleteUserStatement =
                    mConnection.prepareStatement("DELETE FROM user "
                            + "WHERE id = ?");
            mSelectAllUsersStatement =
                    mConnection.prepareStatement("SELECT * FROM user");
            mUpdateSimDataStatement =
                    mConnection.prepareStatement("UPDATE object "
                            + "SET x = ?, y = ?, z = ?, velocity_magnitude = ?, "
                            + "velocity_vector_x = ?, "
                            + "velocity_vector_y = ?, "
                            + "velocity_vector_z = ?, "
                            + "accel_magnitude = ?, " + "accel_vector_x = ?, "
                            + "accel_vector_y = ?, "
                            + "accel_vector_z = ? WHERE id = ?");

            CelestialBody.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
            Star.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
            Link.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
        } catch(SQLException e) {
            throw new DatabaseException("Couldn't prepare statements: "
                    + e.getMessage());
        }
    }

    private void closeConnection() throws java.sql.SQLException {
        if(mConnection != null) {
            mConnection.close();
        }
    }

    /**
     * Constructs a new Database object, initializing all statements.
     * 
     * Attemps to create a connection to the MySQL database specificed in
     * twoverse.conf.Database.properties. Initializes the statements for each
     * Twoverse object.
     * 
     * @throws DatabaseException
     *             if unable to connect to the database
     */
    public Database() throws DatabaseException {
        try {
            // Load properties file
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/Database.properties"));

            Class.forName(DB_CLASS_NAME);
        } catch(IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Unable to load config file: "
                    + e.getMessage());
        } catch(ClassNotFoundException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Unable to load JDBC class driver");
        }

        try {
            mConnection =
                    DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                            mConfigFile.getProperty("DB_USER"),
                            mConfigFile.getProperty("DB_PASSWORD"));
            mConnection.setAutoCommit(true);
        } catch(Exception e) {
            sLogger.log(Level.SEVERE, "Connection to database failed", e);
            throw new DatabaseException("Connection to database failed: "
                    + e.getMessage());
        }

        prepareStatements();
    }

    @Override
    /*
     * Closes the connection to the database.
     */
    public void finalize() {
        try {
            closeConnection();
        } catch(SQLException e) {
            sLogger.log(Level.SEVERE,
                    "Failed while closing database connection",
                    e);
        }
    }

    /**
     * Selects all of the users from the database.
     * 
     * @return map of usernames to User objects for all users in the database
     */
    public synchronized HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<String, User>();
        try {
            ResultSet resultSet = mSelectAllUsersStatement.executeQuery();
            while(resultSet.next()) {
                User user =
                        new User(resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("email"),
                                resultSet.getString("sms"),
                                resultSet.getInt("points"));
                user.setHashedPassword(resultSet.getString("password"));

                users.put(user.getUsername(), user);
            }
            resultSet.close();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Select users query failed", e);
        }
        return users;
    }

    /**
     * Inserts a user into the database.
     * 
     * @param user
     *            the User to add to the database
     * @return the ID of the new user returned from the database
     */
    public synchronized int addUser(User user) {
        sLogger.log(Level.INFO, "Attempting to add user: " + user);
        try {
            mAddUserStatement.setString(1, user.getUsername());
            mAddUserStatement.setString(2, user.getHashedPassword());
            mAddUserStatement.setString(3, user.getEmail());
            mAddUserStatement.setString(4, user.getPhone());
            mAddUserStatement.executeUpdate();
            ResultSet keySet = mAddUserStatement.getGeneratedKeys();
            keySet.next();
            user.setId(keySet.getInt(1));
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Add user query failed for user: "
                    + user, e);
        }
        return user.getId();

    }

    /**
     * Updates the last login time of a user to the current time.
     * 
     * @param user
     *            the user whose last login time should be updated
     */
    public synchronized void updateLoginTime(User user) {
        sLogger.log(Level.INFO, "Attempting to update login time for user: "
                + user);
        try {
            mUpdateUserLastLoginStatement.setInt(1, user.getId());
            mUpdateUserLastLoginStatement.executeUpdate();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Update login time query failed", e);
        }
    }

    /**
     * Deletes a user from the database.
     * 
     * @param user
     *            the user to delete. The ID of the user must be set, and it
     *            must correspond with an ID returned from addUser.
     * @see addUser
     */
    public synchronized void deleteUser(User user) {
        sLogger.log(Level.INFO, "Attempting to delete user: " + user);
        try {
            mDeleteUserStatement.setInt(1, user.getId());
            mDeleteUserStatement.executeUpdate();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Delete user query failed for user: "
                    + user, e);
        }
    }

    /**
     * Inserts an object into the database, using the CelestialBody interface.
     * 
     * @param body
     *            the body to insert
     */
    public synchronized void insert(CelestialBody body) {
        try {
            body.insertInDatabase();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Insert failed for object: " + body, e);
        }
    }

    /**
     * Inserts a Link into the database. This must be a separate method because
     * the Link object does not conform to the CelestialBody interface. This
     * should be revisited in later revisions, as this duplicated method is less
     * than ideal.
     * 
     * @param link
     *            the link to insert
     */
    public synchronized void insert(Link link) {
        try {
            link.insertInDatabase();
        } catch(SQLException e) {
            sLogger.log(Level.WARNING, "Insert failed for link: " + link, e);
        }
    }

    /**
     * Deletes an object from the database.
     * 
     * @param body
     *            the object to delete. The ID of the body must be set, and it
     *            must correspond with an ID returned from insert().
     * @see insert
     */
    public synchronized void delete(CelestialBody body) {
        try {
            body.deleteFromDatabase();
        } catch(SQLException e) {

        }
    }

    /**
     * Update an object in the database.
     * 
     * @param body
     *            the object to update. The ID of the body must be set, and it
     *            must correspond with an ID returned from insert().
     * @see insert
     */
    public synchronized void update(CelestialBody body) {
        try {
            body.updateInDatabase();
        } catch(SQLException e) {

        }
    }

    public class InvalidUserException extends Exception {
        private static final long serialVersionUID = 3985999412866921286L;

        public InvalidUserException(String e) {
            super(e);

        }
    }

    public class DatabaseException extends SQLException {
        private static final long serialVersionUID = 5278742396430531540L;

        public DatabaseException(String message) {
            super(message);
        }
    }
}
