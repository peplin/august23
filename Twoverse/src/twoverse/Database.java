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
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.Planet;
import twoverse.object.PlanetarySystem;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

public class Database {
    private final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mDeleteUserStatement; // cascade update
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
        } catch (SQLException e) {
            sLogger.log(Level.WARNING,
                    "Update celestial body query failed for body: " + body,
                    e);
        } catch (TwoDimensionalException e) {
            sLogger.log(Level.WARNING,
                    "Expected 3D point but was 2D: " + body,
                    e);
        }
    }

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
            Galaxy.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
            Planet.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
            PlanetarySystem.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
            ManmadeBody.prepareDatabaseStatements(DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                    mConfigFile.getProperty("DB_USER"),
                    mConfigFile.getProperty("DB_PASSWORD")));
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't prepare statements: "
                    + e.getMessage());
        }
    }

    private void closeConnection() throws java.sql.SQLException {
        if(mConnection != null) {
            mConnection.close();
        }
    }

    public Database() throws DatabaseException {
        try {
            // Load properties file
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("twoverse/conf/Database.properties"));

            Class.forName(DB_CLASS_NAME);
        } catch (IOException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Unable to load config file: "
                    + e.getMessage());
        } catch (ClassNotFoundException e) {
            sLogger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Unable to load JDBC class driver");
        }

        try {
            mConnection =
                    DriverManager.getConnection(mConfigFile.getProperty("CONNECTION"),
                            mConfigFile.getProperty("DB_USER"),
                            mConfigFile.getProperty("DB_PASSWORD"));
            mConnection.setAutoCommit(true);
        } catch (Exception e) {
            sLogger.log(Level.SEVERE, "Connection to database failed", e);
            throw new DatabaseException("Connection to database failed: "
                    + e.getMessage());
        }

        prepareStatements();
    }

    public void finalize() {
        try {
            closeConnection();
        } catch (SQLException e) {
            sLogger.log(Level.SEVERE,
                    "Failed while closing database connection",
                    e);
        }
    }

    public synchronized HashMap<String, User> getUsers() {
        HashMap<String, User> users = new HashMap<String, User>();
        try {
            ResultSet resultSet = mSelectAllUsersStatement.executeQuery();
            while (resultSet.next()) {
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
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Select users query failed", e);
        }
        return users;
    }

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
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Add user query failed for user: "
                    + user, e);
        }
        return user.getId();

    }

    public synchronized void updateLoginTime(User user) {
        sLogger.log(Level.INFO, "Attempting to update login time for user: "
                + user);
        try {
            mUpdateUserLastLoginStatement.setInt(1, user.getId());
            mUpdateUserLastLoginStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Update login time query failed", e);
        }
    }

    public synchronized void deleteUser(User user) {
        sLogger.log(Level.INFO, "Attempting to delete user: " + user);
        try {
            mDeleteUserStatement.setInt(1, user.getId());
            mDeleteUserStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Delete user query failed for user: "
                    + user, e);
        }
    }

    public synchronized void insert(CelestialBody body) {
        try {
            body.insertInDatabase();
        } catch (SQLException e) {

        }
    }

    public synchronized void delete(CelestialBody body) {
        try {
            body.deleteFromDatabase();
        } catch (SQLException e) {

        }
    }

    public synchronized void update(CelestialBody body) {
        try {
            body.updateInDatabase();
        } catch (SQLException e) {

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
