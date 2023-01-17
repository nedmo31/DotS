package edu.lehigh.cse216.nje225.admin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * This class represents the postgres database running on Heroku.
 * It uses prepared statements to query the database
 */
public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the Users table
     */
    private PreparedStatement mUsersSelectAll;

    /**
     * A prepared statement for getting all data in the Comments table
     */
    private PreparedStatement mOwnershipsSelectAll;

    /**
     * A prepared statement for getting all data in the Mlikes table
     */
    private PreparedStatement mTeamsSelectAll;

    /**
     * A prepared statement for getting one row from the Users table
     */
    private PreparedStatement mUsersSelectOne;

    /**
     * A prepared statement for getting one row from the Comments table
     */
    private PreparedStatement mOwnershipsSelectOne;

    /**
     * A prepared statement for getting one row from the Mlikes table
     */
    private PreparedStatement mTeamsSelectOne;

    /**
     * A prepared statement for deleting a row from the Users table
     */
    private PreparedStatement mUsersDeleteOne;

    /**
     * A prepared statement for deleting a row from the Comments table
     */
    private PreparedStatement mOwnershipsDeleteOne;

    /**
     * A prepared statement for deleting a row from the Mlikes table
     */
    private PreparedStatement mTeamsDeleteOne;

    /**
     * A prepared statement for inserting a row into the Users table
     */
    private PreparedStatement mUsersInsertOne;

    /**
     * A prepared statement for inserting a row into the Comments table
     */
    private PreparedStatement mOwnershipsInsertOne;

    /**
     * A prepared statement for inserting a row into the Mlikes table
     */
    private PreparedStatement mTeamsInsertOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateUsers;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateTeams;    

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateOwnerships;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTables;

    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param db_url the url of the database
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "Messages"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception
            db.mCreateUsers = db.mConnection.prepareStatement(
                    "CREATE TABLE Users (id SERIAL PRIMARY KEY, title VARCHAR(50)) " + 
                    "NOT NULL, content VARCHAR(1024) NOT NULL");
            db.mDropTables = db.mConnection.prepareStatement("DROP TABLE ?");

            // Added statement for checking if table exists
            db.mTableExists = db.mConnection.prepareStatement("SELECT EXISTS ( SELECT FROM pg_tables" + 
                                                              "WHERE tablename = '?' )");

            // Delete prepared statements
            db.mMessageDeleteOne = db.mConnection.prepareStatement("DELETE FROM Messages WHERE mid = ?");
            db.mUsersDeleteOne = db.mConnection.prepareStatement("DELETE FROM Users WHERE uid = ?");
            db.mCommentDeleteOne = db.mConnection.prepareStatement("DELETE FROM Comments WHERE cid = ?");
            db.mLikeDeleteOne = db.mConnection.prepareStatement("DELETE FROM Mlikes WHERE mid = ? AND uid = ?");
            
            // Insert prepared statements
            db.mUsersInsertOne = db.mConnection.prepareStatement("INSERT INTO Users (uid, email, username, gender, sexualor, note, uvalid) VALUES (default, ?, ?, ?, ?, ?, true)");
            db.mCommentInsertOne = db.mConnection.prepareStatement("INSERT INTO Comments (cid, mid, uid, content) VALUES (default, ?, ?, ?)");
            db.mLikeInsertOne = db.mConnection.prepareStatement("INSERT INTO Mlikes (mid, uid, like_val) VALUES (?, ?, ?)");
            db.mMessageInsertOne = db.mConnection.prepareStatement("INSERT INTO Messages (mid, uid, title, content, mvalid) VALUES (default, ?, ?, ?, true)");
            
            // Select all prepared statements
            db.mMessageSelectAll = db.mConnection.prepareStatement("SELECT * FROM Messages ORDER BY mid");
            db.mUsersSelectAll = db.mConnection.prepareStatement("SELECT * FROM Users ORDER BY uid");
            db.mCommentSelectAll = db.mConnection.prepareStatement("SELECT * FROM Comments ORDER BY cid");
            db.mLikeSelectAll = db.mConnection.prepareStatement("SELECT * FROM Mlikes ORDER BY mid");
            
            // Select one prepared statements
            db.mMessageSelectOne = db.mConnection.prepareStatement("SELECT * from Messages WHERE mid = ?");
            db.mUsersSelectOne = db.mConnection.prepareStatement("SELECT * FROM Users WHERE uid = ?");
            db.mCommentSelectOne = db.mConnection.prepareStatement("SELECT * FROM Comments WHERE cid = ?");
            db.mLikeSelectOne = db.mConnection.prepareStatement("SELECT * FROM Mlikes WHERE mid = ? AND uid = ?");
            
            // Invalidate prepared statements
            db.mInvalidateIdea = db.mConnection.prepareStatement("UPDATE Messages SET mvalid = ? WHERE mid = ?");
            db.mInvalidateUser = db.mConnection.prepareStatement("UPDATE Users SET uvalid = ? WHERE uid = ?");
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert a row into the Messages table
     * 
     * @param uid The user id for this new row
     * @param title The title for this new row
     * @param content The content body for this new row
     * 
     * @return The number of rows that were inserted
     */
    int messageInsertRow(int uid, String title, String content) {
        int count = 0;
        try {
            mMessageInsertOne.setInt(1, uid);
            mMessageInsertOne.setString(2, title);
            mMessageInsertOne.setString(3, content);
            count += mMessageInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a row into the Users table
     * 
     * @param email The email for this new row
     * @param username The username for this new row
     * @param gender The gender for this new row
     * @param sexual The sexualor for this new row
     * @param note The note for this new row
     * 
     * @return The number of rows that were inserted
     */
    int usersInsertRow(String email, String username, String gender, String sexual, String note) {
        int count = 0;
        try {
            mUsersInsertOne.setString(1, email);
            mUsersInsertOne.setString(2, username);
            mUsersInsertOne.setString(3, gender);
            mUsersInsertOne.setString(4, sexual);
            mUsersInsertOne.setString(5, note);
            count += mUsersInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a row into the Comments table
     * 
     * @param mid The message id for this new row
     * @param uid The user id for this new row
     * @param content The content for this new row
     * 
     * @return The number of rows that were inserted
     */
    int commentInsertRow(int mid, int uid, String content) {
        int count = 0;
        try {
            mCommentInsertOne.setInt(1, mid);
            mCommentInsertOne.setInt(2, uid);
            mCommentInsertOne.setString(3, content);
            count += mCommentInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a row into the Mlikes table
     * 
     * @param mid The message id for this new row
     * @param uid The user id for this new row
     * @param like_val the like value for this new row
     * 
     * @return The number of rows that were inserted
     */
    int likeInsertRow(int mid, int uid, int like_val) {
        int count = 0;
        try {
            mLikeInsertOne.setInt(1, mid);
            mLikeInsertOne.setInt(2, uid);
            mLikeInsertOne.setInt(3, like_val);
            count += mLikeInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query Messages for a list of all the content in the table.
     * 
     * @return All message rows, as an ArrayList
     */
    ArrayList<DataRow> messageSelectAll() {
        ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            ResultSet rs = mMessageSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new DataRow(rs.getInt("mid"), rs.getInt("uid"), rs.getString("title"),
                 rs.getString("content"), rs.getBoolean("mvalid")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query Users for a list of all the content in the table.
     * 
     * @return All user rows, as an ArrayList
     */
    ArrayList<UserRow> usersSelectAll() {
        ArrayList<UserRow> res = new ArrayList<UserRow>();
        try {
            ResultSet rs = mUsersSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new UserRow(rs.getInt("uid"), rs.getString("email"),
                 rs.getString("username"), rs.getString("gender"),
                  rs.getString("sexualor"), rs.getString("note"),
                  rs.getBoolean("uvalid")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query Comments for a list of all the content in the table.
     * 
     * @return All comment rows, as an ArrayList
     */
    ArrayList<CommentRow> commentSelectAll() {
        ArrayList<CommentRow> res = new ArrayList<CommentRow>();
        try {
            ResultSet rs = mCommentSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new CommentRow(rs.getInt("cid"), rs.getInt("mid"), 
                rs.getInt("uid"), rs.getString("content")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query Mlikes for a list of all the content in the table.
     * 
     * @return All like rows, as an ArrayList
     */
    ArrayList<LikeRow> likeSelectAll() {
        ArrayList<LikeRow> res = new ArrayList<LikeRow>();
        try {
            ResultSet rs = mLikeSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new LikeRow(rs.getInt("mid"), rs.getInt("uid"),
                rs.getInt("like_val")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row in the Messages table, by mid
     * 
     * @param id The mid of the row being requested
     * 
     * @return The data for the requested row, or null if the mid was invalid
     */
    DataRow messageSelectOne(int id) {
        DataRow res = null;
        try {
            mMessageSelectOne.setInt(1, id);
            ResultSet rs = mMessageSelectOne.executeQuery();
            if (rs.next()) {
                res = new DataRow(rs.getInt("mid"), rs.getInt("uid"), rs.getString("title"),
                 rs.getString("content"), rs.getBoolean("mvalid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row in the Users table, by uid
     * 
     * @param id The uid of the row being requested
     * 
     * @return The data for the requested row, or null if the uid was invalid
     */
    UserRow usersSelectOne(int id) {
        UserRow res = null;
        try {
            mUsersSelectOne.setInt(1, id);
            ResultSet rs = mUsersSelectOne.executeQuery();
            if (rs.next()) {
                res = new UserRow(rs.getInt("uid"), rs.getString("email"),
                rs.getString("username"), rs.getString("gender"),
                 rs.getString("sexualor"), rs.getString("note"),
                 rs.getBoolean("uvalid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row in the Comments table, by cid
     * 
     * @param id The cid of the row being requested
     * 
     * @return The data for the requested row, or null if the cid was invalid
     */
    CommentRow commentSelectOne(int id) {
        CommentRow res = null;
        try {
            // mSelectOne.setString(1, "Comments");
            // mSelectOne.setString(2, "cid");
            mCommentSelectOne.setInt(1, id);
            ResultSet rs = mCommentSelectOne.executeQuery();
            if (rs.next()) {
                res = new CommentRow(rs.getInt("cid"), rs.getInt("mid"), 
                rs.getInt("uid"), rs.getString("content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row in the Mlikes table, by mid and uid
     * 
     * @param mid The mid of the row being requested
     * @param uid The uid of the row being requested
     * 
     * @return The data for the requested row, or null if the mid/uid was invalid
     */
    LikeRow likeSelectOne(int mid, int uid) {
        LikeRow res = null;
        try {
            mLikeSelectOne.setInt(1, mid);
            mLikeSelectOne.setInt(2, uid);
            ResultSet rs = mLikeSelectOne.executeQuery();
            if (rs.next()) {
                res = new LikeRow(rs.getInt("mid"), rs.getInt("uid"),
                rs.getInt("like_val"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row in the Messages table by mid
     * 
     * @param id The mid of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int messageDeleteRow(int id) {
        int res = -1;
        try {
            mMessageDeleteOne.setInt(1, id);
            res = mMessageDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row in the Users table by uid
     * 
     * @param id The uid of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int usersDeleteRow(int id) {
        int res = -1;
        try {
            mUsersDeleteOne.setInt(1, id);
            res = mUsersDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row in the Comments table by cid
     * 
     * @param id The cid of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int commentDeleteRow(int id) {
        int res = -1;
        try {
            mCommentDeleteOne.setInt(1, id);
            res = mCommentDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row in the Mlikes table by mid and uid
     * 
     * @param mid The mid of the row to delete
     * @param uid The uid of the row to delete
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int likeDeleteRow(int mid, int uid) {
        int res = -1;
        try {
            mLikeDeleteOne.setInt(1, mid);
            mLikeDeleteOne.setInt(2, uid);
            res = mLikeDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Update the content for a row in the Messages table
     * 
     * @param id The mid of the row to update
     * @param title The new title
     * @param message The new message
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int messageUpdateOne(int id, String title, String message) {
        int res = -1;
        try {
            mMessageUpdateOne.setString(1, title);
            mMessageUpdateOne.setString(2, message);
            mMessageUpdateOne.setInt(3, id);
            res = mMessageUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Users table
     * 
     * @param uid The uid of the row to update
     * @param email The new email
     * @param username The new username
     * @param gender The new gender
     * @param sexual The new sexualor
     * @param note The new note
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int usersUpdateOne(int uid, String email, String username, String gender, String sexual, String note) {
        int res = -1;
        try {
            mUsersUpdateOne.setString(1, email);
            mUsersUpdateOne.setString(2, username);
            mUsersUpdateOne.setString(3, gender);
            mUsersUpdateOne.setString(4, sexual);
            mUsersUpdateOne.setString(5, note);
            mUsersUpdateOne.setInt(6, uid);
            res = mUsersUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Comments table
     * 
     * @param cid The cid of the row to update
     * @param content The new content
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int commentUpdateOne(int cid, String content) {
        int res = -1;
        try {
            mCommentUpdateOne.setString(1, content);
            mCommentUpdateOne.setInt(2, cid);
            res = mCommentUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Mlikes table
     * 
     * @param mid The mid of the row to update
     * @param uid The uid of the row to update
     * @param like_val The new like_val
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int likeUpdateOne(int mid, int uid, int like_val) {
        int res = -1;
        try {
            mLikeUpdateOne.setInt(1, like_val);
            mLikeUpdateOne.setInt(2, mid);
            mLikeUpdateOne.setInt(3, uid);
            res = mLikeUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Invalidates an idea in the Messages table
     * 
     * @param mid The mid of the row to invalidate
     * @param valid The value to change valid to (true/false)
     * 
     * @return The number of rows invalidated
     */
    int invalidateIdea(int mid, boolean valid) {
        int res = -1;
        try {
            mInvalidateIdea.setBoolean(1, valid);
            mInvalidateIdea.setInt(2, mid);
            res = mInvalidateIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Invalidates an user in the Users table
     * 
     * @param uid The uid of the row to invalidate
     * @param valid The value to change valid to (true/false)
     * 
     * @return The number of rows invalidated
     */
    int invalidateUser(int uid, boolean valid) {
        int res = -1;
        try {
            mInvalidateUser.setBoolean(1, valid);
            mInvalidateUser.setInt(2, uid);
            res = mInvalidateUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create a new table in the database. Prints an error if the statement doesn't work.
     * NOTE: this will always create an `id SERIAL PRIMARY KEY` column, so that may have
     * to be changed in the future.
     * e.g. createTable("Messages", "title VARCHAR(50) NOT NULL, content VARCHAR(1024) NOT NULL");
     * 
     * @param tableName the name of the table to be added
     * @param columns A string containing arguments to add columns to the table, SQL formatted
     */
    void createTable(String tableName, String columns) {
        try {
            // NB: This may have to be changed if we don't want id to be the serial primary key for every table
            mCreateTable = mConnection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " ("+columns+")");
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove specified table from the database.  If it does not exist, this will print
     * an error. 
     * 
     * @param tableName the name of the table to drop
     */
    void dropTable(String tableName) {
        try {
            mDropTable = mConnection.prepareStatement("DROP TABLE "+tableName);
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the default SQL calls with a new table name
     * As of 9/26, it just defaults to Messages because that's
     * the only table we have.
     * 
     * @param tableName the new default table's name
     */
    void changeTable(String tableName) {

        // try {

        //     // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
        //     // creation/deletion, so multiple executions will cause an exception
        //     mCreateTable = mConnection.prepareStatement(
        //             "CREATE TABLE " + tableName + " (id SERIAL PRIMARY KEY, title VARCHAR(50)) " + 
        //             "NOT NULL, content VARCHAR(1024) NOT NULL");
        //     mDropTable = mConnection.prepareStatement("DROP TABLE ?");

        //     // Standard CRUD operations
        //     mDeleteOne = mConnection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");
        //     // NB: Insert only really works for Messages, so it will have to be updated because of the values at the end
        //     mInsertOne = mConnection.prepareStatement("INSERT INTO " + tableName + " VALUES (default, ?, ?, 0)");
        //     mSelectAll = mConnection.prepareStatement("SELECT * FROM " + tableName + "");
        //     mSelectOne = mConnection.prepareStatement("SELECT * from " + tableName + " WHERE id=?");
        //     mUpdateOne = mConnection.prepareStatement("UPDATE " + tableName + " SET content = ? WHERE id = ?");
        // } catch (SQLException e) {
        //     System.err.println("Error creating prepared statement");
        //     e.printStackTrace();
        //     disconnect();
        // }
    }

    /**
     * Checks if a table exists in the database
     * 
     * @param tableName the name of the table
     * @return true if the table exists, else false
     */
    boolean tableExists(String tableName) {
        try {
            // This updates our prepared statement to use the lowercase name of the table
            // It must be lowercase or else it will not find it
            mTableExists = mConnection.prepareStatement("SELECT EXISTS ( SELECT FROM pg_tables " + 
                                                              "WHERE tablename = '"+tableName.toLowerCase()+"')");
            // The results of the query, next to point to the first result
            ResultSet rs = mTableExists.executeQuery();
            rs.next();
            // return the boolean value
            return rs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}