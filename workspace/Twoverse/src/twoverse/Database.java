package twoverse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.PlanetarySystem;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;

class InvalidUserException extends Exception {
    public InvalidUserException(String e) {
        super(e);

    }
}

public class Database {
    public Database() throws DatabaseException {
        try {
            // Load properties file
            mConfigFile = new Properties();
            mConfigFile.load(this.getClass().getClassLoader()
                    .getResourceAsStream("twoverse/conf/Database.properties"));

            Class.forName(DB_CLASS_NAME);
        } catch (IOException e) {
            throw new DatabaseException("Unable to load config file");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            mConnection = DriverManager.getConnection(mConfigFile
                    .getProperty("CONNECTION"), mConfigFile
                    .getProperty("DB_USER"), mConfigFile
                    .getProperty("DB_PASSWORD"));
            mConnection.setAutoCommit(true);
        } catch (Exception e) {
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
            mSelectAllUsersStatement = mConnection
                    .prepareStatement("SELECT * FROM user");
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
                    .prepareStatement("SELECT * FROM object, galaxy, galaxy_shapes, colors "
                            + "WHERE object.id = galaxy.id "
                            + "AND galaxy.shape = galaxy_shapes.id"
                            + "AND object.color = colors.id"
                            + "AND object.owner = user.id");
            mGetPlanetarySystemsStatement = mConnection
                    .prepareStatement("SELECT * FROM object, planetary_system, colors "
                            + "WHERE object.id = planetary_system.id "
                            + "AND object.color = colors.id"
                            + "AND object.owner = user.id");
            mGetManmadeBodiesStatement = mConnection
                    .prepareStatement("SELECT * FROM object, manmade, colors "
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

    public HashMap<Integer, User> getUsers() throws InvalidUserException {
        HashMap<Integer, User> users = new HashMap<Integer, User>();
        try {
            ResultSet resultSet = mSelectAllUsersStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User(resultSet.getInt("id"), resultSet
                        .getString("username"),
                        resultSet.getString("password"), resultSet
                                .getString("email"),
                        resultSet.getString("sms"), resultSet.getInt("points"));

                users.put(user.getId(), user);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return users;
    }

    public void addUser(User user) {
        try {
            mAddUserStatement.setString(1, user.getUsername());
            mAddUserStatement.setString(2, user.getHashedPassword());
            mAddUserStatement.setString(3, user.getEmail());
            mAddUserStatement.setString(4, user.getPhone());
            assert (mAddUserStatement.executeUpdate() == 1);
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
            assert (mAddUserStatement.executeUpdate() == 1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        try {
            mDeleteUserStatement.setString(1, user.getUsername());
            assert (mAddUserStatement.executeUpdate() == 1);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CelestialBody[] parseCelestialBodies(ResultSet resultSet,
            HashMap<Integer, User> users) {
        ArrayList<CelestialBody> bodies = new ArrayList<CelestialBody>();
        try {
            while (resultSet.next()) {
                // TODO need to look up User object
                CelestialBody body;

                body = new CelestialBody(resultSet.getInt("object.id"), users
                        .get(resultSet.getInt("object.owner")), resultSet
                        .getTimestamp("birth"),
                        resultSet.getTimestamp("death"), resultSet
                                .getInt("parent"), new Point(resultSet
                                .getInt("x"), resultSet.getInt("y"), resultSet
                                .getInt("z")), new PhysicsVector3d(resultSet
                                .getDouble("velocity_vector_x"), resultSet
                                .getDouble("velocity_vector_y"), resultSet
                                .getDouble("velocity_vector_z"), resultSet
                                .getDouble("velocity_magnitude")),
                        new PhysicsVector3d(resultSet
                                .getDouble("accel_vector_x"), resultSet
                                .getDouble("accel_vector_y"), resultSet
                                .getDouble("accel_vector_z"), resultSet
                                .getDouble("accel_magnitude")));

                // TODO rather than create one for each, why not
                // share? could be very specific
                // maybe drop color/shape for now
                /*
                 * new Color(resultSet.getInt("colors.id"), resultSet
                 * .getString("colors.name"), resultSet .getInt("colors.r"),
                 * resultSet.getInt("colors.g"), resultSet.getInt("colors.b")));
                 */
                bodies.add(body);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (CelestialBody[]) bodies.toArray();
    }

    public HashMap<Integer, Galaxy> getGalaxies(HashMap<Integer, User> users) {
        HashMap<Integer, Galaxy> galaxies = new HashMap<Integer, Galaxy>();
        try {
            ResultSet resultSet = mGetGalaxiesStatement.executeQuery();
            CelestialBody[] bodies = parseCelestialBodies(resultSet, users);
            resultSet.first();
            for (CelestialBody body : bodies) {
                resultSet.next();
                Galaxy galaxy = new Galaxy(body, new GalaxyShape(resultSet
                        .getInt("galaxy_shapes.id"), resultSet
                        .getString("galaxy_shapes.name"), resultSet
                        .getString("galaxy_shapes.texture")), resultSet
                        .getDouble("mass"), resultSet.getDouble("density"));
                galaxies.put(galaxy.getId(), galaxy);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return galaxies;
    }

    public HashMap<Integer, PlanetarySystem> getPlanetarySystems(
            HashMap<Integer, User> users) {
        HashMap<Integer, PlanetarySystem> systems = new HashMap<Integer, PlanetarySystem>();
        try {
            ResultSet resultSet = mGetPlanetarySystemsStatement.executeQuery();
            CelestialBody[] bodies = parseCelestialBodies(resultSet, users);
            resultSet.first();
            for (CelestialBody body : bodies) {
                resultSet.next();
                PlanetarySystem system = new PlanetarySystem(body,
                        resultSet.getInt("center"), 
                        resultSet.getDouble("mass"));
                systems.put(system.getId(), system);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return systems;
    }

    public HashMap<Integer, ManmadeBody> getManmadeBodies(
            HashMap<Integer, User> users) {
        HashMap<Integer, ManmadeBody> manmadeBodies = new HashMap<Integer, ManmadeBody>();
        try {
            ResultSet resultSet = mGetManmadeBodiesStatement.executeQuery();
            CelestialBody[] bodies = parseCelestialBodies(resultSet, users);
            resultSet.first();
            for (CelestialBody body : bodies) {
                resultSet.next();
                ManmadeBody manmadeBody = new ManmadeBody(body);
                manmadeBodies.put(manmadeBody.getId(), manmadeBody);
            }
            resultSet.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return manmadeBodies;
    }

    public void insertGalaxies(Galaxy[] galaxies) {
        //TODO
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
        //TODO
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
        //TODO
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
        //TODO
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
        //TODO
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
        //TODO
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
        //TODO
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

    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mSelectUserStatement;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mUpdateUserPreferenceStatement;
    private PreparedStatement mDeleteUserStatement; // cascade update
    private PreparedStatement mSelectAllUsersStatement;
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
