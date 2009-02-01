package filefinder;

//Students: Chris Peplin and Miles Kaufmann

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * The required FileFinder interface, as specified by the initial contents of 
 * the FileFinder.java file.
 */
interface FileFinderInterface {
    Connection getConnection();
    void closeConnection() throws java.sql.SQLException;
    boolean addUserDB(String userid);
    boolean deleteUserDB(String userid);
    boolean addFile(String filename, String owner);
    boolean deleteFileDB(String filename);
    boolean permitReader(String filename, String userid);
    boolean denyReader(String filename, String userid);
    Vector<WordSearchResult> findWord(String userid, String word) 
            throws FileFinderException;
    Vector<WordSearchResult> findAllWords(String userid, Vector<String> allwords)
            throws FileFinderException;
    boolean reinitalizeAllTables();
}



public class FileFinder implements FileFinderInterface {
    
    /* Do not change the declaration for Connection and url. The url is public, 
     * in case we need to change it to point to a different database or to a proxy
     * server for grading and testing.
     */
    Connection connection = null;
    public static String url = "jdbc:oracle:thin:@db8.engin.umich.edu:1521:muscle";
    
    private static final Pattern USERID_PAT = Pattern.compile("\\p{Alnum}{1,8}");
    private static final Pattern WORD_PAT = Pattern.compile("\\p{Alnum}{3,}");
    private static final Pattern DELIMITER_PAT = Pattern.compile("[^\\p{Alnum}+]");
    private static final int FILENAME_MAXLEN = 256;
    private static final int WORD_MAXLEN = 12;
    private static final int FOREIGN_KEY_CONSTRAINT = 2291;
    private static final int PROTECT_ADMIN = 20000;
    private static final int UNIQUE_CONSTRAINT = 1;
    private static final boolean VERBOSE = true;
    
    //parameters indicated in comments
    private PreparedStatement addUserSt; // userid
    private PreparedStatement deleteUserSt; // userid
    private PreparedStatement addFileSt; // filename, ownerid
    private PreparedStatement selectFileSt; // filename
    private PreparedStatement deleteFileSt; // filename
    private PreparedStatement grantPermissionSt; // fileid, userid
    private PreparedStatement revokePermissionSt; // fileid, userid
    private PreparedStatement transferOwnershipSt; // ownerid, fileid
    private PreparedStatement findFileIDSt; // filename
    private PreparedStatement validateUserIDSt;
    private PreparedStatement deleteAllFilesSt;
    private PreparedStatement deleteAllUsersSt;
    private PreparedStatement addWordNewFileSt; // word, occurences
    private PreparedStatement addWordSt; // fileid, word, occurences
    private PreparedStatement clearWordsSt; // fileid
    private PreparedStatement findWordSt; // userid, word
    
    private Map<Integer, PreparedStatement> findWordsSts
            = new HashMap<Integer, PreparedStatement>();
    
    /**
     * The constructor creates a connection to the database, using the userid
     * and the password.
     * 
     * @param userid
     * @param password
     * @throws FileFinderException
     */
    public FileFinder(String userid, String password)
        throws FileFinderException
    {
        // Load the oracle driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (Exception e) {
            throw new FileFinderException("Failed to load Oracle driver (" + 
                                          e.toString() + ")");
        }
        
        try {
            // Make a connection handle to the database
            connection = DriverManager.getConnection(FileFinder.url, userid, password);
            connection.setAutoCommit(true);
        } catch(SQLException e) {
            throw new FileFinderException("Connection to database failed: " + 
                                          e.getMessage());
        }
        prepareStatements();
    }
    
    /**
     * A do-nothing default constructor.  Use at your own peril!
     */
    public FileFinder() {}
    
    /**
     * Returns a reference to the database connection.
     * @return the reference to the connection object.
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Closes the connection that was created in the constructor. 
     * 
     * @throws java.sql.SQLException
     */
    public void closeConnection() throws java.sql.SQLException {
        if (connection != null) connection.close();
    }
    
    /**
     * Informs the database that the specified userid is valid.
     * @param userid
     * @return true if the userid was successfully added. Returns false if the userid was
     *            of illegal format or length.
     *            If the userid was already in the database, return false.
     *            On any other error, also return false.
     *            
     */
    public boolean addUserDB (String userid) {
        userid = normalizeUserid(userid);
        if (userid == null) return false;
   
        try {
            addUserSt.setString(1, userid);
            addUserSt.executeUpdate();
            return true;
        } catch (SQLException e) {
            int c = e.getErrorCode();
            if (c != FOREIGN_KEY_CONSTRAINT && c != UNIQUE_CONSTRAINT)
                unexpectedError("Could not add user", e);
            return false;
        }
    }
    
    /**
     * Informs the database that the specified userid is no longer valid. A side-effect of removing
     * the userid is that the ownership of any files owned by the userid is changed to userid 'admin'.
     * Any reader rights of the userid to files are removed.
     * 
     * @param userid
     * @return true if the userid was successfully removed. Also,
     *         return true if the userid of proper format was not found. 
     *         Return false on illegal format for userid or any database error.
     */
    
    public boolean deleteUserDB(String userid) {
        userid = normalizeUserid(userid);
        if (userid == null) return false;
        
        try {
            deleteUserSt.setString(1, userid);
            deleteUserSt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() != PROTECT_ADMIN)
                unexpectedError("Could not delete user: ", e);
            return false;
        }
        
        return true;
    }
    
    /**
     * Opens the file for reading, counts the frequency of words in the file, 
     * and adds the <word, count> tuples for the file, along with
     * the owner to the database. If the file is already in the database, it is
     * reindexed and added with the new owner, deleting existing readers if the
     * owner has changed.
     *
     * @param filename  Absolute path of the file, whose words should be indexed.
     * @param owner     The userid of the owner of the file, to be added to the database
     * @return          true if file added to the database (or replaced the info   
     *                  on the previous version).  False otherwise.
     */
    public boolean addFile(String filename, String owner) {
        
        owner = normalizeUserid(owner);
        filename = normalizeFilename(filename);
        if (owner == null || filename == null) return false;
        
        Map<String, Integer> words = countWords(filename);
        if (words == null) return false;
        
        try {
            connection.setAutoCommit(false);
            
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
            
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            if (e.getErrorCode() != FOREIGN_KEY_CONSTRAINT)
                unexpectedError("Could not add file", e);
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e2) {
                unexpectedError("Could not rollback", e2);
            }
            return false;
        }
        return true;
    }
    
    /**
     * Deletes information about a file from the database. The result is
     * as if the file were never added to the database. 
     * 
     * @param filename  name of the file whose info is being deleted from 
     *                  the database.
     * @return          true if the file was successfully deleted or did not  
     *                  exist in the DB. If the filename cannot be mapped to an 
     *                  absolute path, or if an error occurs, return false.
     * 
     */
    public boolean deleteFileDB(String filename)  {
        filename = normalizeFilename(filename);
        if (filename == null) return false;
        
        try {
            deleteFileSt.setString(1, filename);
            deleteFileSt.executeUpdate();
        } catch (SQLException e) {
            unexpectedError("Could not delete file", e);
            return false;
        }
        return true;
    }
    
    /**
     * Tells the database that the specified userid is explicitly 
     * authorized to read words in the file.
     * @param filename  name of the file
     * @param userid    name of the user being authorized to read the file
     * @return          true on success, false on failure. If the user was
     *                  already authorized, you should still return true; It is
     *                  Ok to add the owner or admin to the ReaderTable as an
     *                  explicit authorization. Return true in that case.
     *                  
     *                  Note that if the userid or file do not already exist 
     *                  in the database, the operation should return false.
     */
    public boolean permitReader(String filename, String userid)  {
        userid = normalizeUserid(userid);
        Integer fileid = getFileID(filename);
        if (userid == null || fileid == null) return false;
        
        try {
            grantPermissionSt.setInt(1, fileid);
            grantPermissionSt.setString(2, userid);
            grantPermissionSt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == UNIQUE_CONSTRAINT) return true;
            return false;
        }
        
        return true;
    }
    
    /**
     * Tells the database that the specified userid is no longer explicitly 
     * authorized to read words in the file. Returns true if the permission
     * successfully revoked.
     * 
     * In case of filename or userid that are not found in the database, 
     * the operation is considered to fail.
     * 
     * The operation is considered a success if the userid and filename were in the
     * database, but the userid did not have access to the file.
     * 
     * In case the userid is the owner or admin, the operation is still considered
     * to succeed. In that case, any explicit rights for admin or owner in 
     * ReaderTable should be removed.
     * 
     * @param filename  name of the file
     * @param userid    name of the user being authorized to read the file.
     * @return  true on successful completion. false on failure.
     */
    public boolean denyReader(String filename, String userid)  {
        userid = normalizeAndValidateUserid(userid);
        Integer fileid = getFileID(filename);
        if (userid == null || fileid == null) return false;
        
        try {
            revokePermissionSt.setInt(1, fileid);
            revokePermissionSt.setString(2, userid);
            revokePermissionSt.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns the vector containing <filename, count> pairs
     * sorted in descending order by count.
     * 
     * @param userid    The userid of the user attempting to do search.
     * @param word      The word to be searched for.
     * @return          a vector that contains results returned by the SQL query. 
     *                  If the word is not found in any file, null is returned.
     * @throws FileFinderException on any error, including invalid 
     *                  userid (but not nonexistent userid) or illegal word format
     */
    public Vector<WordSearchResult> findWord(String userid, String word)
        throws FileFinderException
    {
        Vector<String> words = new Vector<String>(1);
        words.add(word);
        return findAllWords(userid, words);
    }
    
    /**
     * Returns the vector containing <filename, count> pairs, sorted in descending order by count.
     * If count values are equal for two files, then the files that come earlier in lexicographic 
     * order are returned first. Only those files are returned that contain all the words.
     * 
     * The count value is the *sum*of frequencies of the given words in each file.  For example,
     * if the words that are search for are "hello" and "there", and file1's frequencies for those
     * words is 2 and 3, file2's frequencies are 1 and 6, and file3 only contains the word "there",
     * then the result should be <<file2, 7>, <file1, 5>>
     * 
     * @param userid    The userid of the user doing the search.
     * @param words     A vector of the words to be searched for.
     * @return          a vector that contains results returned by the SQL query,
     *                  or null if there were no results
     * @throws FileFinderException on any error, including illegal userid 
     *                  (wrong format) or illegal words. If the userid is 
     *                  correct format, but not in the database, then return normally.
     */
    public Vector<WordSearchResult> findAllWords(String userid, Vector<String> allwords)
        throws FileFinderException
    {
        userid = normalizeUserid(userid);
        if (userid == null) 
            throw new FileFinderException("Invalid or nonexistent userid");
        if (allwords == null) 
            throw new FileFinderException("Null word vector passed");
        int numWords = allwords.size();
        if (numWords < 1)
            throw new FileFinderException("Must specify at least one word");
        
        PreparedStatement st = getFindWordsStatement(numWords);
        
        try {
            st.setString(1, userid);
            for (int i = 0; i < numWords; i++) {
                String word = allwords.get(i);
                String normword = normalizeWord(word);
                if (normword == null)
                    throw new FileFinderException("Invalid word: " + word);
                st.setString(i+2, normword);
            }
            return resultSetToVector(st.executeQuery());
        } catch (SQLException e) {
            unexpectedError("Could not find words", e);
            throw new FileFinderException("Couldn't find words: " + e.getMessage());
        }
    }
        
    /**
     * Reinitialize the FileFinder database system, discarding all userid and file data.
     *
     * @return true if database successfully reinitialized. Else return false.
     */
    public boolean reinitalizeAllTables() {
        try {
            deleteAllFilesSt.execute();
            deleteAllUsersSt.execute();
        } catch (SQLException e) {
            unexpectedError("Could not reinitialize database", e);
            return false;
        }
        return true;
    }
    
    /**
     * Creates all prepared statements for use in other functions.
     * @throws FileFinderException 
     */
    private void prepareStatements() throws FileFinderException {
        try {
            addUserSt = connection.prepareStatement(
                "INSERT INTO useridtable (userid) " +
                "VALUES (?)");

            deleteUserSt = connection.prepareStatement(
                "DELETE FROM useridtable " +
                "WHERE userid = ?");

            addFileSt = connection.prepareStatement(
                "INSERT INTO filetable (fileid, filename, ownerid) " +
                "VALUES (fileidsequence.nextval, ?, ?)");

            selectFileSt = connection.prepareStatement(
                "SELECT fileid, ownerid " +
                "FROM filetable " +
                "WHERE filename = ?");

            deleteFileSt = connection.prepareStatement(
                "DELETE FROM filetable " +
                "WHERE filename = ?");

            grantPermissionSt = connection.prepareStatement(
                "INSERT INTO readertable (fileid, userid) " +
                "VALUES (?, ?)");

            revokePermissionSt = connection.prepareStatement(
                "DELETE FROM readertable " +
                "WHERE fileid = ? AND userid = ?");

            transferOwnershipSt = connection.prepareStatement(
                "UPDATE filetable SET ownerid = ? " +
                "WHERE fileid = ?");

            findFileIDSt = connection.prepareStatement(
                "SELECT fileid " +
                "FROM filetable " +
                "WHERE filename = ?");

            validateUserIDSt = connection.prepareStatement(
                "SELECT COUNT(*) " +
                "FROM useridtable " +
                "WHERE userid = ?");
            
            deleteAllFilesSt = connection.prepareStatement(
                "DELETE FROM filetable");

            deleteAllUsersSt = connection.prepareStatement(
                "DELETE FROM useridtable " +
                "WHERE userid != 'admin'");
            
            addWordSt = connection.prepareStatement(
                "INSERT INTO wordtable (fileid, word, count) " +
                    "VALUES (?, ?, ?)");
                
            clearWordsSt = connection.prepareStatement(
                "DELETE FROM wordtable WHERE fileid = ?");
                
            addWordNewFileSt = connection.prepareStatement(
                "INSERT INTO wordtable (fileid, word, count) " +
                "VALUES (fileidsequence.currval, ?, ?)");
                
            findWordSt = connection.prepareStatement(
                "SELECT f.filename, w.count " +
                "FROM wordtable w, filetable f " +
                "WHERE w.fileid = f.fileid " +
                "AND EXISTS (SELECT * FROM readerview " +
                "            WHERE userid = ? AND fileid = f.fileid) " +
                "AND w.word = ? " +
                "ORDER BY count DESC, f.filename ASC");
        } catch (SQLException e) {
            throw new FileFinderException("Couldn't prepare statements: " + e.getMessage());
        }
    }

    /**
     * If the input is a valid userid, converts it to lowercase and returns it.
     * @param userid
     * @return          normalized userid, or null.
     */
    private String normalizeUserid(String userid) {
        if (userid == null) return null;
        
        if (USERID_PAT.matcher(userid).matches()) {
            return userid.toLowerCase();
        } else {
            return null;
        }
    }
    
    /**
     * If the input is a userid that exists in the database, then converts it 
     * to lowercase and returns it.
     * @param userid
     * @return          normalized, existing userid, or null.
     */
    private String normalizeAndValidateUserid(String userid) {
        userid = normalizeUserid(userid);
        int count = getSingleInteger(validateUserIDSt, userid);
        if (count > 0) {
            return userid;
        } else {
            return null;
        }
    }
    
    /**
     * Returns the canonical, absolute path of a given filename. 
     * @param filename  
     * @return          a valid absolute filename, or null if not possible.
     */
    private String normalizeFilename(String filename) {
        try {
            File file = new File(filename);
            String normName;
            try {
                normName = file.getCanonicalPath();
            } catch (Exception e) {
                normName = file.getAbsolutePath();
            }
            if (normName.length() > FILENAME_MAXLEN) return null;
            return normName;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Truncates the input to 12 characters, and returns the lowercased version
     * of it if it's a valid word.
     * @param word
     * @return          a valid normalized word, or null.
     */
    private String normalizeWord(String word) {
        if (word == null) return null;
        if (WORD_PAT.matcher(word).matches()) {
            return word.toLowerCase().substring(0, Math.min(WORD_MAXLEN, word.length()));
        } else {
            return null;
        }
    }

    /**
     * Counts the words in the specified file.
     * @return  A map of words to the number of times they occur, or null if
     *          the file could not be read.
     */
    private Map<String, Integer> countWords(String filename) {
        
        Map<String, Integer> wordcounts = new HashMap<String, Integer>();
        Scanner scanner;
        try {
            scanner = new Scanner(new FileReader(filename));
        } catch (FileNotFoundException e) {
            return null;
        }
        scanner.useDelimiter(DELIMITER_PAT);
        while (scanner.hasNext()) {
            String word = normalizeWord(scanner.next());
            if (word != null) {
            
                Integer occurances = wordcounts.get(word);
            
                if (occurances == null)
                    wordcounts.put(word, 1);
                else
                    wordcounts.put(word, occurances + 1);
            }
        }
        return wordcounts;
    }
    

    
    
    private Integer getFileID(String filename) {
        filename = normalizeFilename(filename);
        if (filename == null) return null;
        return getSingleInteger(findFileIDSt, filename);
    }

    /**
     * A convenience method to execute a statement that returns a single
     * integer value.
     * @param statement         The statement to execute.
     * @param parameters        The parameters to the prepared statement
     * @return                  The result of the query, or null if no rows
     *                          were returned.
     */
    private Integer getSingleInteger(PreparedStatement statement,
                                     Object ... parameters)
    {
        Integer value = null;
        try {
            statement.clearParameters();
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i+1, parameters[i]);
            }
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                value = rs.getInt(1);
            rs.close();
        } catch (SQLException e) {
            // value = null;
        }
        return value;
    }
    
    /**
     * A helper method to get a prepared statement for findAllWords for the 
     * appropriate number of words.  Generates a query for a given vector 
     * size the first time it's requested, prepares it, and stores it in 
     * a Map, returning the same prepared statements on subsequent calls.
     * @param size      the number of search words the query should support
     * @return          a prepared statement corresponding to the input size.
     */
    private PreparedStatement getFindWordsStatement(int size) {
        assert size >= 1;
        // We can use a more efficient query for searches for a single word:
        if (size == 1) return findWordSt;
        
        // If we've already prepared the query, return it
        if (findWordsSts.containsKey(size)) return findWordsSts.get(size);
        
        StringBuilder query = new StringBuilder(
                "SELECT f.filename, SUM(w.count) as count " +
                "  FROM wordtable w, filetable f " +
                " WHERE w.fileid = f.fileid " +
                "   AND EXISTS (SELECT * FROM readerview " +
                "               WHERE userid = ? AND fileid = f.fileid) " +
                "   AND w.word in (?");
        for (int i = 1; i < size; i++)
            query.append(", ?");
        query.append(") GROUP BY f.filename HAVING COUNT(*) = ");
        query.append(size);
        query.append(" ORDER BY count DESC, f.filename ASC");
        PreparedStatement st;
        try {
            st = connection.prepareStatement(query.toString());
        } catch (SQLException e) {
            unexpectedError("Could not prepare statement for findAllWords", e);
            return null;
        }
        findWordsSts.put(size, st);
        return st;
    }

    /**
     * Converts a SQL result set into a vector of word search results.
     * @param rs        A SQL result set
     * @return          A Vector of WordSearchResults.
     */
    private Vector<WordSearchResult> resultSetToVector(ResultSet rs) {
        Vector<WordSearchResult> result = new Vector<WordSearchResult>();
        try {
            while (rs.next()) {
                WordSearchResult currentWord = new WordSearchResult();
                currentWord.filename = rs.getString("filename");
                currentWord.count = rs.getInt("count");
                result.add(currentWord);
            }
            rs.close();
        } catch (SQLException e) {
            unexpectedError("Could not get result vector", e);
            return null;
        }
         
        if (result.size() == 0)
            return null;
            
        return result;
    }

    /**
     * Print a backtrace for events that should not normally happen during
     * execution (such as a mid-session connection failure)
     * @param message   a description of what was being attempted
     * @param e         the thrown exception
     */
    private void unexpectedError(String message, Throwable e) {
        if (VERBOSE) {
            System.err.println("***ERROR: " + message);
            e.printStackTrace();
        }
    }

}

