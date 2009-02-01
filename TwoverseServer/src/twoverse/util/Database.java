package twoverse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String dbClassName = "com.mysql.jdbc.Driver";
    private Connection mConnection = null;
    private Properties mConfigFile;
    private PreparedStatement mAddObjectStatement;
    private PreparedStatement mAddUserStatement;
    private PreparedStatement mUpdateUserStatement;
    private PreparedStatement mUpdateUserPreferenceStatement;
    private PreparedStatement mAddGalaxyStatement;
    private PreparedStatement mAddPlanetarySystemStatement;
    private PreparedStatement mGetObjectParentStatement;
    private PreparedStatement mGetObjectChildrenStatement;
    private PreparedStatement mAddPlanetStatement;
    private PreparedStatement mAddManmadeBodyStatement;
    private PreparedStatement mAddGenericBodyStatement;
    private PreparedStatement mDeleteUserStatement;
    private PreparedStatement mAddPhenomenonStatement;

    public Database() {
        mConfigFile.load(this.getClass().
                            getClassLoader().
                            getResourceAsStream()
                            ("../config/util/Database.properties"));
        // Load the oracle driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (Exception e) {
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
        } catch(SQLException e) {
            throw new FileFinderException("Connection to database failed: " + 
                                          e.getMessage());
        }
        prepareStatements();
    }

    private void prepareStatements() throws FileFinderException {
        try {
            addObjectStatement = mConnection.preparedStatement(
                "INSERT INTO filetable (fileid, filename, ownerid) " +
                "VALUES (fileidsequence.nextval, ?, ?)");
            addUserStatement;
            updateUserStatement;
            updateUserPreferenceStatement;
            addGalaxyStatement;
            addPlanetarySystemStatement;
            getObjectParentStatement;
            getObjectChildrenStatement;
            addPlanetStatement;
            addManmadeBodyStatement;
            addGenericBodyStatement;
            deleteUserStatement;
            addPhenomenonStatement;
        } catch (SQLException e) {
            throw new FileFinderException("Couldn't prepare statements: " + e.getMessage());
        }
    }

    private void closeConnection() throws java.sql.SQLException {
        if (mConnection != null) mConnection.close();
    }

    private void unexpectedError(String message, Throwable e) {
        if (VERBOSE) {
            System.err.println("***ERROR: " + message);
            e.printStackTrace();
        }
    }

    public boolean addFile(String filename, String owner) {
        owner = normalizeUserid(owner);
        filename = normalizeFilename(filename);
        if (owner == null || filename == null) return false;
        
        Map<String, Integer> words = countWords(filename);
        if (words == null) return false;
        
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
        return true;
    }
}
