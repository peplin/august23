package twoverse.util;

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


public class Database {
    private static final String DB_CLASS_NAME = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mSelectUserStatement;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserLastLoginStatement;
    private PreparedStatement mUpdateUserPreferenceStatement;
    private PreparedStatement mDeleteUserStatement; //cascade update
    private PreparedStatement mAddParentObjectStatement;
    private PreparedStatement mAddGalaxyStatement;
    private PreparedStatement mAddPlanetarySystemStatement;
    private PreparedStatement mAddManmadeBodyStatement;
    //private PreparedStatement mAddPhenomenonStatement;
    private PreparedStatement mGetGalaxiesStatement;
    private PreparedStatement mGetPlanetarySystemsStatement;
    private PreparedStatement mGetManmadeBodiesStatement;
    //private PreparedStatement mGetPhenomenonStatement;
    private PreparedStatement mUpdateSimDataStatement;
    private PreparedStatement mUpdateGalaxyStatement;
    private PreparedStatement mUpdatePlanetarySystemStatement;
    private PreparedStatement mUpdatePhenomenonStatement;
    private PreparedStatement mGetColorsStatement;
    
    //private PreparedStatement mGetObjectParentStatement;
    //private PreparedStatement mGetObjectChildrenStatement;
    
    public User getUser(String username) {
		return new User("null");
    	
    }

	public void addUser(User user) {
    	
    }
    
    public void deleteUser(User user) {
    	
    }
    
    public void updateLoginTime(User user, Time time) {
    	
    }
    
    public Galaxy[] getGalaxies() {
		return null;
    	
    }
    
    public PlanetarySystem[] getPlanetarySystems() {
		return null;
    	
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
    
  
    public Database() throws DatabaseException {
    	try {
        mConfigFile.load(this.getClass().
        				getClassLoader().getResourceAsStream(
        							"../config/util/Database.properties"));
    	} catch (IOException e) {
    	
    	}
        // Load the oracle driver
        try {
            Class.forName(DB_CLASS_NAME);
        } catch (Exception e) {
            throw new DatabaseException(
                    "Failed to load MySQL driver (" + e.toString() + ")");
        }
        
        try {
            // Make a mConnection handle to the database
            mConnection = DriverManager.getConnection(
                                mConfigFile.getProperty("url"), 
                                mConfigFile.getProperty("DB_USER"), 
                                mConfigFile.getProperty("DB_PASSWORD"));
            mConnection.setAutoCommit(true);
        } catch(Exception e) {
        	throw new DatabaseException(
        			"Connection to database failed: " + e.getMessage());
        }
        prepareStatements();
    }

    private void prepareStatements() throws DatabaseException {
        try {
            mSelectUserStatement = mConnection.prepareStatement(
                "SELECT id, password FROM user " +
                "WHERE username = ?");    
            mAddUserStatement = mConnection.prepareStatement(
                "INSERT INTO user (username, password, email, sms) " +
                "VALUES (?, ?, ?, ?)");
            mUpdateUserLastLoginStatement = mConnection.prepareStatement(
                "UPDATE user " +
                "SET last_login = NOW() " +
                "WHERE username = ?");
            mDeleteUserStatement = mConnection.prepareStatement(
                "DELETE FROM user " +
                "WHERE username = ?");
            mAddParentObjectStatement = mConnection.prepareStatement(
                "INSERT INTO object (owner, parent, velocity_magnitude, " +
	                "velocity_vector_x, velocity_vector_y, velocity_vector_z, "+
	                "accel_magnitude, accel_vector_x, accel_vector_y, " +
	                "accel_vector_z, color, type) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            mAddGalaxyStatement = mConnection.prepareStatement(
                "INSERT INTO galaxy (id, shape, mass, density) " +
                "VALUES (?, ?, ?, ?)");
            mAddPlanetarySystemStatement = mConnection.prepareStatement(
                "INSERT INTO planetary_system (id, centerid, density) " +
                "VALUES (?, ?, ?)");
            mAddManmadeBodyStatement = mConnection.prepareStatement(
                "INSERT INTO manmade (id) " +
                "VALUES (?)");
            mGetGalaxiesStatement = mConnection.prepareStatement(
                "SELECT * FROM object, galaxy, galaxy_shapes, colors, user" +
                "WHERE object.id = galaxy.id " +
                	"AND galaxy.shape = galaxy_shapes.id" +
                	"AND object.color = colors.id" +
                	"AND object.owner = user.id");
            mGetPlanetarySystemsStatement = mConnection.prepareStatement(
            	"SELECT * FROM object, planetary_system, colors, user " +
		            "WHERE object.id = planetary_system.id " +
		        	"AND object.color = colors.id" +
		        	"AND object.owner = user.id");
            mGetManmadeBodiesStatement = mConnection.prepareStatement(
            	"SELECT * FROM object, manmade, colors, user " +
		            "WHERE object.id = body.id " +
		        	"AND object.color = colors.id" +
		        	"AND object.owner = user.id");
            mUpdateSimDataStatement = mConnection.prepareStatement(
                "UPDATE object " +
                "SET velocity_magnitude = ?," +
                	"velocity_vector_x = ?, " +
                	"velocity_vector_y = ?, " +
                	"velocity_vector_z = ?, " +
                	"accel_magnitude = ?, " +
                	"accel_vector_x = ?, " +
                	"accel_vector_y = ?, " +
                	"accel_vector_z = ?," +
                	"x = ?, " +
                	"y = ?, " +
                	"z = ? " +
            	"WHERE id = ?");
            mUpdateGalaxyStatement = mConnection.prepareStatement(
        		"UPDATE galaxy " +
                "SET shape = ?," +
                	"mass = ?, " +
                	"density = ? " +                	
            	"WHERE id = ?");
            mUpdatePlanetarySystemStatement = mConnection.prepareStatement(
            		"UPDATE galaxy " +
                    "SET centerid = ?," +
                    	"density = ? " +                	
                	"WHERE id = ?");
            mGetColorsStatement = mConnection.prepareStatement(
            	"SELECT * FROM colors");
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't prepare statements: " + e.getMessage());
        }
    }

    private void closeConnection() throws java.sql.SQLException {
        if (mConnection != null) mConnection.close();
    }

    private void unexpectedError(String message, Throwable e) {
            System.err.println("***ERROR: " + message);
            e.printStackTrace();
    }

    /*
        
        try {
            mConnection.setAutoCommit(false);
            
            selectFileSt.setString(1, filename);
            ResultSet rs = selectFileSt.executeQuery();
            if (rs.next()) { // The file already exists
                int fileid = rs.getInt("fileid");
                String previousOwner = rs.getString("ownerid");
                rs.close();
                
                if (!owner.equals(previousOwner)) {
                    transferOwnershipSt.setString(1, owner);
                    transferOwnershipSt.setInt(2, fileid);
                    
                    int rc = transferOwnershipSt.executeUpdate();
                    assert rc == 1;
                }

                clearWordsSt.setInt(1, fileid);
                clearWordsSt.executeUpdate();
                
                addWordSt.setInt(1, fileid);
                for (Map.Entry<String, Integer> entry : words.entrySet()) {
                    addWordSt.setString(2, entry.getKey());
                    addWordSt.setInt(3, entry.getValue());
                    int rc = addWordSt.executeUpdate();
                    assert rc == 1;
                }

            } else { // The file does not exist
                rs.close();
                
                addFileSt.setString(1, filename);
                addFileSt.setString(2, owner);
                addFileSt.executeUpdate();

                for (Map.Entry<String, Integer> entry : words.entrySet()) {
                    addWordNewFileSt.setString(1, entry.getKey());
                    addWordNewFileSt.setInt(2, entry.getValue());
                    int rc = addWordNewFileSt.executeUpdate();
                    assert rc == 1;
                }
            }
            
            mConnection.commit();
            mConnection.setAutoCommit(true);
        } catch (SQLException e) {
            if (e.getErrorCode() != FOREIGN_KEY_CONSTRAINT)
                unexpectedError("Could not add file", e);
            try {
                mConnection.rollback();
                mConnection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not rollback", e2);
            }
            return false;
        }
       */
}