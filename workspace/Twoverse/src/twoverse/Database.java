package twoverse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Properties;

import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;

class InvalidUserException extends Exception {
    public InvalidUserException(String e) {

    }
}

public class Database {
    public Database() throws DatabaseException {
        try {
            // Load properties file
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("twoverse/conf/Database.properties"));

            // Load the oracle driver
            Class.forName(DB_CLASS_NAME);
        } catch (IOException e) {
            throw new DatabaseException("Unable to load config file");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load MySQL driver ("
                    + e.toString() + ")");
        }

        try {
            mConnection = DriverManager.getConnection(mConfigFile
                    .getProperty("CONNECTION"), mConfigFile
                    .getProperty("DB_USER"), mConfigFile
                    .getProperty("DB_PASSWORD"));
            mConnection.setAutoCommit(true);
        } catch (Exception e) {
            System.out.print(e.getMessage());
            throw new DatabaseException("Connection to database failed: "
                    + e.getMessage());
        }

        prepareStatements();
    }

    private void prepareStatements() throws DatabaseException {
        try {
            mSelectUserStatement = mConnection
                    .prepareStatement("SELECT * FROM user "
                            + "WHERE username = ?");
            mAddUserStatement = mConnection
                    .prepareStatement("INSERT INTO user (username, password, email, sms) "
                            + "VALUES (?, ?, ?, ?)");
            mUpdateUserLastLoginStatement = mConnection
                    .prepareStatement("UPDATE user "
                            + "SET last_login = NOW() " + "WHERE username = ?");
            mDeleteUserStatement = mConnection
                    .prepareStatement("DELETE FROM user "
                            + "WHERE username = ?");
            mAddParentObjectStatement = mConnection
                    .prepareStatement("INSERT INTO object (owner, parent, velocity_magnitude, "
                            + "velocity_vector_x, velocity_vector_y, velocity_vector_z, "
                            + "accel_magnitude, accel_vector_x, accel_vector_y, "
                            + "accel_vector_z, color, type) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            mAddGalaxyStatement = mConnection
                    .prepareStatement("INSERT INTO galaxy (id, shape, mass, density) "
                            + "VALUES (?, ?, ?, ?)");
            mAddPlanetarySystemStatement = mConnection
                    .prepareStatement("INSERT INTO planetary_system (id, centerid, density) "
                            + "VALUES (?, ?, ?)");
            mAddManmadeBodyStatement = mConnection
                    .prepareStatement("INSERT INTO manmade (id) "
                            + "VALUES (?)");
            mGetGalaxiesStatement = mConnection
                    .prepareStatement("SELECT * FROM object, galaxy, galaxy_shapes, colors, user"
                            + "WHERE object.id = galaxy.id "
                            + "AND galaxy.shape = galaxy_shapes.id"
                            + "AND object.color = colors.id"
                            + "AND object.owner = user.id");
            mGetPlanetarySystemsStatement = mConnection
                    .prepareStatement("SELECT * FROM object, planetary_system, colors, user "
                            + "WHERE object.id = planetary_system.id "
                            + "AND object.color = colors.id"
                            + "AND object.owner = user.id");
            mGetManmadeBodiesStatement = mConnection
                    .prepareStatement("SELECT * FROM object, manmade, colors, user "
                            + "WHERE object.id = body.id "
                            + "AND object.color = colors.id"
                            + "AND object.owner = user.id");
            mUpdateSimDataStatement = mConnection
                    .prepareStatement("UPDATE object "
                            + "SET velocity_magnitude = ?,"
                            + "velocity_vector_x = ?, "
                            + "velocity_vector_y = ?, "
                            + "velocity_vector_z = ?, "
                            + "accel_magnitude = ?, " + "accel_vector_x = ?, "
                            + "accel_vector_y = ?, " + "accel_vector_z = ?,"
                            + "x = ?, " + "y = ?, " + "z = ? " + "WHERE id = ?");
            mUpdateGalaxyStatement = mConnection
                    .prepareStatement("UPDATE galaxy " + "SET shape = ?,"
                            + "mass = ?, " + "density = ? " + "WHERE id = ?");
            mUpdatePlanetarySystemStatement = mConnection
                    .prepareStatement("UPDATE galaxy " + "SET centerid = ?,"
                            + "density = ? " + "WHERE id = ?");
            mGetColorsStatement = mConnection
                    .prepareStatement("SELECT * FROM colors");
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't prepare statements: "
                    + e.getMessage());
        }
    }

    private void closeConnection() throws java.sql.SQLException {
        if (mConnection != null)
            mConnection.close();
    }

    private void unexpectedError(String message, Throwable e) {
        System.err.println("***ERROR: " + message);
        e.printStackTrace();
    }

    public User getUser(String username) throws InvalidUserException {
        User requestedUser = null;
        try {
            mSelectUserStatement.setString(1, username);

            ResultSet resultSet = mSelectUserStatement.executeQuery();
            if (resultSet.next()) {
                requestedUser = new User(resultSet.getInt("id"), resultSet
                        .getString("username"),
                        resultSet.getString("password"), resultSet
                                .getString("email"),
                        resultSet.getString("sms"), resultSet.getInt("points"));
                resultSet.close();
                return requestedUser;
            } else {
                // TODO problem, no user
                throw new InvalidUserException("No user " + username);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return requestedUser;
    }

    public void addUser(User user) {
        try {
            mAddUserStatement.setString(1, user.getUserame());
            mAddUserStatement.setString(2, user.getHashedPassword());
            mAddUserStatement.setString(3, user.getEmail());
            mAddUserStatement.setString(4, user.getPhone());
            assert(mAddUserStatement.executeQuery() == 1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    // TODO more graceful error handlings...what happens on update of bad
    // username - I guess that shouldn't happen, else it's a problem in my
    // server, so maybe assert is okay
    public void updateLoginTime(User user) {
        try {
            mUpdateUserLastLoginStatement.setString(1, user.getUsername());
            assert(mAddUserStatement.executeQuery() == 1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        try {
            mDeleteUserStatement.setString(1, user.getUserame());
            assert(mAddUserStatement.executeQuery() == 1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadBodies(ObjectManager manager) {
    // how to find parent object? maybe run obj.linkParent(map[id]) after
    // coming back from parseCelestialBodies

    }

    public CelestialBody[] parseCelestialBodies(ResultSet resultSet) {
        ArrayList<CelestialBody> bodies = new ArrayList<CelestialBodies>();
        while(resultSet.next()) {
            CelestialBody body = new CelestialBody(
                        resultSet.getInt("object.id"),
                        resultSet.getString("users.username"),
                        resultSet.getTimestamp("birth"),
                        resultSet.getInt("parent"),
                        new Point(
                            resultSet.getInt("x"),
                            resultSet.getInt("y"),
                            resultSet.getInt("z")),
                        new PhysicsVector3d(
                            resultSet.getDouble("velocity_vector_x"),
                            resultSet.getDouble("velocity_vector_y"),
                            resultSet.getDouble("velocity_vector_z"),
                            resultSet.getDouble("velocity_magnitude")),
                        new PhysicsVector3d(
                            resultSet.getDouble("accel_vector_x"),
                            resultSet.getDouble("accel_vector_y"),
                            resultSet.getDouble("accel_vector_z"),
                            resultSet.getDouble("accel_magnitude")),
                        // TODO rather than create one for each, why not
                        // share? could be very specific
                        // maybe drop color/shape for now
                        new Color(
                            resultSet.getInt("colors.id"),
                            resultSet.getString("colors.name"),
                            resultSet.getInt("colors.r"),
                            resultSet.getInt("colors.g"),
                            resultSet.getInt("colors.b")));

            if(resultSet.getTimestamp("death") != null) {
                body.setDeathTime(resultSet.getTimestamp("death"));
            }

            body.add(galaxy);
        }
        resultSet.close();
        return bodies.toArray();
    }

    public Galaxy[] getGalaxies() {
        ArrayList<Galaxy> galaxies = new ArrayList<Galaxy>();
        try {
            ResultSet resultSet = mGetGalaxiesStatement.executeQuery();
            CelestialBodies[] bodies = parseCelestialBodies(resultSet);
            resultSet.first();
            for(CelestialBody body : bodies) {
                resultSet.next();
                Galaxy galaxy = new Galaxy(
                            body,
                            new GalaxyShape(
                                resultSet.getInt("galaxy_shapes.id"),
                                resultSet.getString("galaxy_shapes.name"),
                                resultSet.getString("galaxy_shapes.texture")),
                            resultSet.getDouble("mass"),
                            resultSet.getDouble("density"));
            }
                galaxies.add(galaxy);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return galaxies.toArray();
    }

    public PlanetarySystem[] getPlanetarySystems() {
        ArrayList<PlanetarySystem> systems = new ArrayList<PlanetarySystem>();
        try {
            ResultSet resultSet = mGetPlanetarySystemsStatement.executeQuery();
            while(resultSet.next()) {
                // TODO lots of repeated code...what if
                // we have a generic method to construct the CelestialBody
                // and each subtype has a constructor that accepts the
                // parent type
                PlanetarySystem system = new PlanetarySystem(
                            resultSet.getInt("object.id"),
                            resultSet.getString("users.username"),
                            resultSet.getTimestamp("birth"),
                            resultSet.getInt("parent"),
                            new Point(
                                resultSet.getInt("x"),
                                resultSet.getInt("y"),
                                resultSet.getInt("z")),
                            new PhysicsVector3d(
                                resultSet.getDouble("velocity_vector_x"),
                                resultSet.getDouble("velocity_vector_y"),
                                resultSet.getDouble("velocity_vector_z"),
                                resultSet.getDouble("velocity_magnitude")),
                            new PhysicsVector3d(
                                resultSet.getDouble("accel_vector_x"),
                                resultSet.getDouble("accel_vector_y"),
                                resultSet.getDouble("accel_vector_z"),
                                resultSet.getDouble("accel_magnitude")),
                            new Color(
                                resultSet.getInt("colors.id"),
                                resultSet.getString("colors.name"),
                                resultSet.getInt("colors.r"),
                                resultSet.getInt("colors.g"),
                                resultSet.getInt("colors.b")),
                            resultSet.getInt("center"),
                            resultSet.getDouble("mass"));

                if(resultSet.getTimestamp("death") != null) {
                    system.setDeathTime(resultSet.getTimestamp("death"));
                }

                systems.add(system);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return systems.toArray();
    }

    public ManmadeBody[] getManmadeBodies() {
        return null;

    }

    public void insertGalaxies(Galaxy[] galaxies) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not add galaxies", e);
        }
    }

    public void insertPlanetarySystems(PlanetarySystem[] systems) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not add planetary systems", e);
        }
    }

    public void insertManmadeBodies(ManmadeBody[] bodies) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not add bodies", e);
        }
    }

    public void updateSimulationData(Object[] objects) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not update simulation data", e);
        }
    }

    public void updateGalaxies(Galaxy[] galaxies) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not update galaxies", e);
        }
    }

    public void updatePlanetarySystems(PlanetarySystem[] systems) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not update planetary systems", e);
        }
    }

    public void updateManmadeBodies(ManmadeBody[] bodes) {
        try {
            mConnection.setAutoCommit(false);
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not roll back", e);
            }
            unexpectedError("Could not update bodies", e);
        }
    }

    /*
     * 
     * try { mConnection.setAutoCommit(false);
     * 
     * selectFileSt.setString(1, filename); ResultSet rs =
     * selectFileSt.executeQuery(); if (rs.next()) { // The file already exists
     * int fileid = rs.getInt("fileid"); String previousOwner =
     * rs.getString("ownerid"); rs.close();
     * 
     * if (!owner.equals(previousOwner)) { transferOwnershipSt.setString(1,
     * owner); transferOwnershipSt.setInt(2, fileid);
     * 
     * int rc = transferOwnershipSt.executeUpdate(); assert rc == 1; }
     * 
     * clearWordsSt.setInt(1, fileid); clearWordsSt.executeUpdate();
     * 
     * addWordSt.setInt(1, fileid); for (Map.Entry<String, Integer> entry :
     * words.entrySet()) { addWordSt.setString(2, entry.getKey());
     * addWordSt.setInt(3, entry.getValue()); int rc =
     * addWordSt.executeUpdate(); assert rc == 1; }
     * 
     * } else { // The file does not exist rs.close();
     * 
     * addFileSt.setString(1, filename); addFileSt.setString(2, owner);
     * addFileSt.executeUpdate();
     * 
     * for (Map.Entry<String, Integer> entry : words.entrySet()) {
     * addWordNewFileSt.setString(1, entry.getKey()); addWordNewFileSt.setInt(2,
     * entry.getValue()); int rc = addWordNewFileSt.executeUpdate(); assert rc
     * == 1; } }
     * 
     * mConnection.commit(); mConnection.setAutoCommit(true); } catch
     * (SQLException e) { if (e.getErrorCode() != FOREIGN_KEY_CONSTRAINT)
     * unexpectedError("Could not add file", e); try { mConnection.rollback();
     * mConnection.setAutoCommit(true); } catch (SQLException e2) {
     * unexpectedError("Could not rollback", e2); } return false; }
     */
    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mSelectUserStatement;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mUpdateUserPreferenceStatement;
    private PreparedStatement mDeleteUserStatement; // cascade update
    private PreparedStatement mAddParentObjectStatement;
    private PreparedStatement mAddGalaxyStatement;
    private PreparedStatement mAddPlanetarySystemStatement;
    private PreparedStatement mAddManmadeBodyStatement;
    // private PreparedStatement mAddPhenomenonStatement;
    private PreparedStatement mGetGalaxiesStatement;
    private PreparedStatement mGetPlanetarySystemsStatement;
    private PreparedStatement mGetManmadeBodiesStatement;
    // private PreparedStatement mGetPhenomenonStatement;
    private PreparedStatement mUpdateSimDataStatement;
    private PreparedStatement mUpdateGalaxyStatement;
    private PreparedStatement mUpdatePlanetarySystemStatement;
    private PreparedStatement mUpdatePhenomenonStatement;
    private PreparedStatement mGetColorsStatement;

    // private PreparedStatement mGetObjectParentStatement;
    // private PreparedStatement mGetObjectChildrenStatement;
}
