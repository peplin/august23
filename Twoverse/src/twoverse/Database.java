package twoverse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import twoverse.object.CelestialBody;
import twoverse.object.Galaxy;
import twoverse.object.ManmadeBody;
import twoverse.object.Planet;
import twoverse.object.PlanetarySystem;
import twoverse.util.GalaxyShape;
import twoverse.util.PhysicsVector3d;
import twoverse.util.Point;
import twoverse.util.User;
import twoverse.util.Point.TwoDimensionalException;

public class Database {
    private final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mSelectUserStatement;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mDeleteUserStatement; // cascade update
    private PreparedStatement mSelectAllUsersStatement;
    private PreparedStatement mAddCelestialBodyStatement;
    private PreparedStatement mAddGalaxyStatement;
    private PreparedStatement mAddPlanetarySystemStatement;
    private PreparedStatement mAddPlanetStatement;
    private PreparedStatement mAddManmadeBodyStatement;
    private PreparedStatement mDeleteObjectStatement;
    // private PreparedStatement mAddPhenomenonStatement;
    private PreparedStatement mGetCelestialBodyStatement;
    private PreparedStatement mGetGalaxiesStatement;
    private PreparedStatement mGetPlanetarySystemsStatement;
    private PreparedStatement mGetPlanetsStatement;
    private PreparedStatement mGetManmadeBodiesStatement;
    // private PreparedStatement mGetPhenomenonStatement;
    private PreparedStatement mUpdateSimDataStatement;
    private PreparedStatement mUpdateCelestialBodyStatement;
    private PreparedStatement mUpdateGalaxyStatement;
    private PreparedStatement mUpdatePlanetarySystemStatement;
    private PreparedStatement mUpdatePlanetStatement;
    private static Logger sLogger = Logger.getLogger(Database.class.getName());

    // private PreparedStatement mGetObjectParentStatement;
    // private PreparedStatement mGetObjectChildrenStatement;

    private ArrayList<CelestialBody> parseCelestialBodies(ResultSet resultSet) {
        ArrayList<CelestialBody> bodies = new ArrayList<CelestialBody>();
        try {
            while (resultSet.next()) {
                CelestialBody body;

                body =
                        new CelestialBody(resultSet.getInt("object.id"),
                                resultSet.getInt("object.owner"),
                                resultSet.getString("object.name"),
                                resultSet.getTimestamp("birth"),
                                resultSet.getTimestamp("death"),
                                resultSet.getInt("parent"),
                                new Point(resultSet.getInt("x"),
                                        resultSet.getInt("y"),
                                        resultSet.getInt("z")),
                                new PhysicsVector3d(resultSet.getDouble("velocity_vector_x"),
                                        resultSet.getDouble("velocity_vector_y"),
                                        resultSet.getDouble("velocity_vector_z"),
                                        resultSet.getDouble("velocity_magnitude")),
                                new PhysicsVector3d(resultSet.getDouble("accel_vector_x"),
                                        resultSet.getDouble("accel_vector_y"),
                                        resultSet.getDouble("accel_vector_z"),
                                        resultSet.getDouble("accel_magnitude")));
                bodies.add(body);
            }
        } catch (SQLException e) {
            sLogger.log(Level.WARNING,
                    "Unable to parse celestial bodies from set: " + resultSet,
                    e);
        }

        return bodies;
    }

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

    private void updateCelestialBody(CelestialBody body) throws SQLException {
        try {
            mUpdateCelestialBodyStatement.setString(1, body.getName());

            if(body.getOwnerId() != -1) {
                mUpdateCelestialBodyStatement.setInt(2, body.getOwnerId());
            } else {
                mUpdateCelestialBodyStatement.setNull(2, Types.INTEGER);
            }

            if(body.getParentId() != -1) {
                mUpdateCelestialBodyStatement.setInt(3, body.getParentId());
            } else {
                mUpdateCelestialBodyStatement.setNull(3, Types.INTEGER);
            }

            mUpdateCelestialBodyStatement.setDouble(4, body.getPosition()
                    .getX());
            mUpdateCelestialBodyStatement.setDouble(5, body.getPosition()
                    .getY());
            mUpdateCelestialBodyStatement.setDouble(6, body.getPosition()
                    .getZ());
            mUpdateCelestialBodyStatement.setDouble(7, body.getVelocity()
                    .getMagnitude());
            mUpdateCelestialBodyStatement.setDouble(8, body.getVelocity()
                    .getUnitDirection()
                    .getX());
            mUpdateCelestialBodyStatement.setDouble(9, body.getVelocity()
                    .getUnitDirection()
                    .getY());
            mUpdateCelestialBodyStatement.setDouble(10, body.getVelocity()
                    .getUnitDirection()
                    .getZ());
            mUpdateCelestialBodyStatement.setDouble(11, body.getAcceleration()
                    .getMagnitude());
            mUpdateCelestialBodyStatement.setDouble(12, body.getAcceleration()
                    .getUnitDirection()
                    .getX());
            mUpdateCelestialBodyStatement.setDouble(13, body.getAcceleration()
                    .getUnitDirection()
                    .getY());
            mUpdateCelestialBodyStatement.setDouble(14, body.getAcceleration()
                    .getUnitDirection()
                    .getZ());
            mUpdateCelestialBodyStatement.setInt(15, body.getId());
            mUpdateCelestialBodyStatement.executeUpdate();
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
            mSelectUserStatement =
                    mConnection.prepareStatement("SELECT * FROM user "
                            + "WHERE id = ?");
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
            mAddCelestialBodyStatement =
                    mConnection.prepareStatement("INSERT INTO object (name, owner, parent, x, y, z, velocity_magnitude, "
                            + "velocity_vector_x, velocity_vector_y, velocity_vector_z, "
                            + "accel_magnitude, accel_vector_x, accel_vector_y, "
                            + "accel_vector_z) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            mAddGalaxyStatement =
                    mConnection.prepareStatement("INSERT INTO galaxy (id, shape, mass, density) "
                            + "VALUES (?, ?, ?, ?)");
            mAddPlanetarySystemStatement =
                    mConnection.prepareStatement("INSERT INTO planetary_system (id, centerid, mass) "
                            + "VALUES (?, ?, ?)");
            mAddPlanetStatement =
                mConnection.prepareStatement("INSERT INTO planet (id, mass, radius) "
                        + "VALUES (?, ?, ?)");
            mAddManmadeBodyStatement =
                    mConnection.prepareStatement("INSERT INTO manmade (id) "
                            + "VALUES (?)");
            mDeleteObjectStatement =
                    mConnection.prepareStatement("DELETE FROM object "
                            + "WHERE id = ?");
            mGetCelestialBodyStatement =
                    mConnection.prepareStatement("SELECT * FROM object "
                            + "WHERE object.id = ?");
            mGetGalaxiesStatement =
                    mConnection.prepareStatement("SELECT * FROM object "
                            + "NATURAL JOIN galaxy "
                            + "LEFT JOIN (galaxy_shape) "
                            + "ON (galaxy.shape = galaxy_shape.id) "
                            + "LEFT JOIN (user) "
                            + "ON (object.owner = user.id)");
            mGetPlanetarySystemsStatement =
                    mConnection.prepareStatement("SELECT * FROM object "
                            + "NATURAL JOIN planetary_system "
                            + "LEFT JOIN (user) "
                            + "ON (object.owner = user.id)");
            mGetPlanetsStatement =
                mConnection.prepareStatement("SELECT * FROM object "
                        + "NATURAL JOIN planet "
                        + "LEFT JOIN (user) "
                        + "ON (object.owner = user.id)");
            mGetManmadeBodiesStatement =
                    mConnection.prepareStatement("SELECT * FROM object "
                            + "NATURAL JOIN manmade " + "LEFT JOIN (user) "
                            + "ON (object.owner = user.id)");
            mUpdateSimDataStatement =
                    mConnection.prepareStatement("UPDATE object "
                            + "SET x = ?, y = ?, z = ?, velocity_magnitude = ?, "
                            + "velocity_vector_x = ?, "
                            + "velocity_vector_y = ?, "
                            + "velocity_vector_z = ?, "
                            + "accel_magnitude = ?, " + "accel_vector_x = ?, "
                            + "accel_vector_y = ?, "
                            + "accel_vector_z = ? WHERE id = ?");
            mUpdateCelestialBodyStatement =
                    mConnection.prepareStatement("UPDATE object "
                            + "SET name = ?, owner = ?, parent = ?, x = ?, "
                            + "y = ?, z = ?, velocity_magnitude = ?, "
                            + "velocity_vector_x = ?, velocity_vector_y = ?,"
                            + "velocity_vector_z = ?, accel_magnitude = ?,"
                            + "accel_vector_x = ?, accel_vector_y = ?, "
                            + "accel_vector_z = ? WHERE id = ?)");
            mUpdateGalaxyStatement =
                    mConnection.prepareStatement("UPDATE galaxy "
                            + "SET shape = ?," + "mass = ?, " + "density = ? "
                            + "WHERE id = ?");
            mUpdatePlanetarySystemStatement =
                    mConnection.prepareStatement("UPDATE planetary_system "
                            + "SET centerid = ?," + " mass = ? "
                            + "WHERE id = ?");
            mUpdatePlanetStatement =
                mConnection.prepareStatement("UPDATE planet "
                        + "SET mass = ?," + " radius = ? "
                        + "WHERE id = ?");
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't prepare statements: "
                    + e.getMessage());
        }
    }

    private void addCelestialBody(CelestialBody body) throws SQLException {
        try {
            mAddCelestialBodyStatement.setString(1, body.getName());

            if(body.getOwnerId() != -1) {
                mAddCelestialBodyStatement.setInt(2, body.getOwnerId());
            } else {
                mAddCelestialBodyStatement.setNull(2, Types.INTEGER);
            }

            if(body.getParentId() != -1) {
                mAddCelestialBodyStatement.setInt(3, body.getParentId());
            } else {
                mAddCelestialBodyStatement.setNull(3, Types.INTEGER);
            }
            mAddCelestialBodyStatement.setDouble(4, body.getPosition().getX());
            mAddCelestialBodyStatement.setDouble(5, body.getPosition().getY());
            mAddCelestialBodyStatement.setDouble(6, body.getPosition().getZ());
            mAddCelestialBodyStatement.setDouble(7, body.getVelocity()
                    .getMagnitude());
            mAddCelestialBodyStatement.setDouble(8, body.getVelocity()
                    .getUnitDirection()
                    .getX());
            mAddCelestialBodyStatement.setDouble(9, body.getVelocity()
                    .getUnitDirection()
                    .getY());
            mAddCelestialBodyStatement.setDouble(10, body.getVelocity()
                    .getUnitDirection()
                    .getZ());
            mAddCelestialBodyStatement.setDouble(11, body.getAcceleration()
                    .getMagnitude());
            mAddCelestialBodyStatement.setDouble(12, body.getAcceleration()
                    .getUnitDirection()
                    .getX());
            mAddCelestialBodyStatement.setDouble(13, body.getAcceleration()
                    .getUnitDirection()
                    .getY());
            mAddCelestialBodyStatement.setDouble(14, body.getAcceleration()
                    .getUnitDirection()
                    .getZ());
            mAddCelestialBodyStatement.executeUpdate();
            ResultSet keySet = mAddCelestialBodyStatement.getGeneratedKeys();
            if(!keySet.next()) {
                throw new SQLException("Couldn't find key of object we just added");
            }
            body.setId(keySet.getInt(1));
            keySet.close();
            mGetCelestialBodyStatement.setInt(1, body.getId());
            ResultSet resultSet = mGetCelestialBodyStatement.executeQuery();
            if(!resultSet.next()) {
                throw new SQLException("Couldn't find object we just added");
            }
            body.setBirthTime(resultSet.getTimestamp("birth"));
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING,
                    "Add celestial body query failed for body: " + body,
                    e);
            throw new SQLException("Add celestial body query failed for body: "
                    + body);
        } catch (TwoDimensionalException e) {
            sLogger.log(Level.WARNING,
                    "Expected 3D point but was 2D: " + body,
                    e);
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
        try {
            mUpdateUserLastLoginStatement.setInt(1, user.getId());
            mUpdateUserLastLoginStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Update login time query failed", e);
        }
    }

    public synchronized void deleteUser(User user) {
        try {
            mDeleteUserStatement.setInt(1, user.getId());
            mDeleteUserStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Delete user query failed for user: "
                    + user, e);
        }
    }

    public synchronized HashMap<Integer, Galaxy> getGalaxies() {
        HashMap<Integer, Galaxy> galaxies = new HashMap<Integer, Galaxy>();
        try {
            ResultSet resultSet = mGetGalaxiesStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseCelestialBodies(resultSet);
            resultSet.beforeFirst();
            for (CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between galaxies and celestial bodies");
                }

                Galaxy galaxy =
                        new Galaxy(body,
                                new GalaxyShape(resultSet.getInt("galaxy_shape.id"),
                                        resultSet.getString("galaxy_shape.name"),
                                        resultSet.getString("galaxy_shape.texture")),
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("density"));
                galaxies.put(galaxy.getId(), galaxy);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get galaxies", e);
        }
        return galaxies;
    }

    public synchronized HashMap<Integer, PlanetarySystem> getPlanetarySystems() {
        HashMap<Integer, PlanetarySystem> systems =
                new HashMap<Integer, PlanetarySystem>();
        try {
            ResultSet resultSet = mGetPlanetarySystemsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseCelestialBodies(resultSet);
            resultSet.beforeFirst();
            for (CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between systems and celestial bodies");
                }
                PlanetarySystem system =
                        new PlanetarySystem(body,
                                resultSet.getInt("centerid"),
                                resultSet.getDouble("mass"));
                systems.put(system.getId(), system);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get planetary systems", e);
        }
        return systems;
    }
    
    public synchronized HashMap<Integer, Planet> getPlanets() {
        HashMap<Integer, Planet> planets =
                new HashMap<Integer, Planet>();
        try {
            ResultSet resultSet = mGetPlanetsStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseCelestialBodies(resultSet);
            resultSet.beforeFirst();
            for (CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between planets and celestial bodies");
                }
                Planet planet =
                        new Planet(body,
                                resultSet.getDouble("mass"),
                                resultSet.getDouble("radius"));
                planets.put(planet.getId(), planet);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get planets", e);
        }
        return planets;
    }

    public synchronized HashMap<Integer, ManmadeBody> getManmadeBodies() {
        HashMap<Integer, ManmadeBody> manmadeBodies =
                new HashMap<Integer, ManmadeBody>();
        try {
            ResultSet resultSet = mGetManmadeBodiesStatement.executeQuery();
            ArrayList<CelestialBody> bodies = parseCelestialBodies(resultSet);
            resultSet.beforeFirst();
            for (CelestialBody body : bodies) {
                if(!resultSet.next()) {
                    throw new SQLException("Mismatch between manmade and celestial bodies");
                }
                ManmadeBody manmadeBody = new ManmadeBody(body);
                manmadeBodies.put(manmadeBody.getId(), manmadeBody);
            }
            resultSet.close();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Unable to get manmade bodies", e);
            ;
        }
        return manmadeBodies;
    }

    public synchronized void add(Galaxy galaxy) {
        try {
            addCelestialBody(galaxy);

            mAddGalaxyStatement.setInt(1, galaxy.getId());
            mAddGalaxyStatement.setInt(2, galaxy.getShape().getId());
            mAddGalaxyStatement.setDouble(3, galaxy.getMass());
            mAddGalaxyStatement.setDouble(4, galaxy.getDensity());
            mAddGalaxyStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not add galaxy " + galaxy, e);
        }
    }

    public synchronized void add(PlanetarySystem system) {
        try {
            addCelestialBody(system);

            mAddPlanetarySystemStatement.setInt(1, system.getId());

            if(system.getCenterId() != -1) {
                mAddPlanetarySystemStatement.setInt(2, system.getCenterId());
            } else {
                mAddPlanetarySystemStatement.setNull(2, Types.INTEGER);
            }

            mAddPlanetarySystemStatement.setDouble(3, system.getMass());
            mAddPlanetarySystemStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + system, e);
        }
    }

    public void add(Planet planet) {
        try {
            addCelestialBody(planet);

            mAddPlanetStatement.setInt(1, planet.getId());
            mAddPlanetStatement.setDouble(2, planet.getMass());
            mAddPlanetStatement.setDouble(3, planet.getRadius());
            mAddPlanetStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not add system " + planet, e);
        }
    }

    public synchronized void add(ManmadeBody manmadeBody) {
        try {
            addCelestialBody(manmadeBody);

            mAddManmadeBodyStatement.setInt(1, manmadeBody.getId());
            mAddManmadeBodyStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not add manmade body "
                    + manmadeBody, e);
        }
    }

    public void delete(CelestialBody body) {
        try {
            mDeleteObjectStatement.setInt(1, body.getId());
            mDeleteObjectStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not delete body " + body, e);
        }
    }

    public synchronized void addGalaxies(Galaxy[] galaxies) {
        try {
            mConnection.setAutoCommit(false);
            for (Galaxy galaxy : galaxies) {
                add(galaxy);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not add galaxies", e);
        }
    }

    public synchronized void addPlanetarySystems(PlanetarySystem[] systems) {
        try {
            mConnection.setAutoCommit(false);
            for (PlanetarySystem system : systems) {
                add(system);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not add planetary systems", e);
        }
    }
    
    public synchronized void addPlanets(Planet[] planets) {
        try {
            mConnection.setAutoCommit(false);
            for (Planet planet : planets) {
                add(planet);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not add planets", e);
        }
    }

    public synchronized void addManmadeBodies(ManmadeBody[] bodies) {
        try {
            mConnection.setAutoCommit(false);
            for (ManmadeBody body : bodies) {
                add(body);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not add manmade bodies", e);
        }
    }

    public synchronized void updateSimulation(CelestialBody[] bodies) {
        try {
            mConnection.setAutoCommit(false);
            for (CelestialBody body : bodies) {
                updateSimulationBody(body);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not update simulation data", e);
        }
    }

    public synchronized void updateGalaxies(Galaxy[] galaxies) {
        try {
            mConnection.setAutoCommit(false);
            for (Galaxy galaxy : galaxies) {
                update(galaxy);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not update galaxies", e);
        }
    }

    public synchronized void updatePlanetarySystems(PlanetarySystem[] systems) {
        try {
            mConnection.setAutoCommit(false);
            for (PlanetarySystem system : systems) {
                update(system);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not update planetary systems", e);
        }
    }
    
    public synchronized void updatePlanets(Planet[] planets) {
        try {
            mConnection.setAutoCommit(false);
            for (Planet planet : planets) {
                update(planet);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not update planets", e);
        }
    }

    public synchronized void updateManmadeBodies(ManmadeBody[] bodies) {
        try {
            mConnection.setAutoCommit(false);
            for (ManmadeBody body : bodies) {
                update(body);
            }
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                sLogger.log(Level.WARNING, "Could not roll back transaction", e);
            }
            sLogger.log(Level.WARNING, "Could not update manmade bodies", e);
        }
    }

    private void update(PlanetarySystem system) {
        try {
            updateCelestialBody(system);

            mUpdatePlanetarySystemStatement.setInt(1, system.getCenterId());
            mUpdatePlanetarySystemStatement.setDouble(2, system.getMass());
            mUpdatePlanetarySystemStatement.setDouble(3, system.getId());
            mUpdatePlanetarySystemStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not update planetary system "
                    + system, e);
        }
    }
    
    private void update(Planet planet) {
        try {
            updateCelestialBody(planet);

            mUpdatePlanetStatement.setDouble(1, planet.getMass());
            mUpdatePlanetStatement.setDouble(2, planet.getRadius());
            mUpdatePlanetStatement.setDouble(3, planet.getId());
            mUpdatePlanetStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not update planetary system "
                    + planet, e);
        }
    }

    private void update(Galaxy galaxy) {
        try {
            updateCelestialBody(galaxy);

            mUpdateGalaxyStatement.setInt(1, galaxy.getShape().getId());
            mUpdateGalaxyStatement.setDouble(2, galaxy.getMass());
            mUpdateGalaxyStatement.setDouble(3, galaxy.getDensity());
            mUpdateGalaxyStatement.setInt(4, galaxy.getId());
            mUpdateGalaxyStatement.executeUpdate();
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not update galaxy " + galaxy, e);
        }
    }

    private void update(ManmadeBody manmadeBody) {
        try {
            updateCelestialBody(manmadeBody);
        } catch (SQLException e) {
            sLogger.log(Level.WARNING, "Could not update manmade body "
                    + manmadeBody, e);
        }
    }

    public class InvalidUserException extends Exception {
        private static final long serialVersionUID = 3985999412866921286L;

        public InvalidUserException(String e) {
            super(e);

        }
    }

    public class DatabaseException extends Exception {
        private static final long serialVersionUID = 5278742396430531540L;

        DatabaseException(String message) {
            super(message);
        }
    }
}
