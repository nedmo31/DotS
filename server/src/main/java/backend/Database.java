package backend;


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
     * A prepared statement for updating a row into the Users table
     */
    private PreparedStatement mUsersUpdateOne;

    /**
     * A prepared statement for updating a row into the Comments table
     */
    private PreparedStatement mOwnershipsUpdateOne;

    /**
     * A prepared statement for updating a row into the Mlikes table
     */
    private PreparedStatement mTeamsUpdateOne;

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

    private PreparedStatement mGetOwnerships;

    private PreparedStatement mGetTeam;
    
    private PreparedStatement mGetUser;

    private PreparedStatement mGetUserOwnership;

    private PreparedStatement mGetUserID;

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
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":5432" + dbUri.getPath();
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

            db.mCreateUsers = db.mConnection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Users( uid SERIAL PRIMARY KEY, username VARCHAR(20) " + 
                    "UNIQUE NOT NULL, password VARCHAR(25) NOT NULL, money INTEGER NOT NULL)");
            db.mDropTables = db.mConnection.prepareStatement("DROP TABLE ?");
            db.mCreateTeams = db.mConnection.prepareStatement("CREATE TABLE IF NOT EXISTS Teams( tid SERIAL PRIMARY KEY, name VARCHAR(30), " + 
            "price INTEGER NOT NULL, wins INTEGER NOT NULL, losses INTEGER NOT NULL, pointsfor INTEGER NOT NULL, pointsagainst INTEGER NOT NULL, lastprice INTEGER NOT NULL)");
            //TODO make the ids foreign keys
            db.mCreateOwnerships = db.mConnection.prepareStatement("CREATE TABLE IF NOT EXISTS Ownerships( uid INTEGER NOT NULL, tid INTEGER NOT NULL, count INTEGER NOT NULL)");

            // Delete prepared statements
            db.mUsersDeleteOne = db.mConnection.prepareStatement("DELETE FROM Users WHERE uid = ?");
            db.mTeamsDeleteOne = db.mConnection.prepareStatement("DELETE FROM Teams WHERE tid = ?");
            db.mOwnershipsDeleteOne = db.mConnection.prepareStatement("DELETE FROM Ownerships WHERE uid = ? AND tid = ?");
            
            // Insert prepared statements
            db.mUsersInsertOne = db.mConnection.prepareStatement("INSERT INTO Users (uid, username, password, money) VALUES (default, ?, ?, 200)");
            db.mTeamsInsertOne = db.mConnection.prepareStatement("INSERT INTO Teams (tid, name, price, wins, losses, pointsfor, pointsagainst, lastprice) VALUES (?, ?, ?, ?, ?, ?, ?, 0)");
            db.mOwnershipsInsertOne = db.mConnection.prepareStatement("INSERT INTO Ownerships (uid, tid, count) VALUES (?, ?, ?)");

            // Update prepared statements
            db.mUsersUpdateOne = db.mConnection.prepareStatement("UPDATE Users SET money = ? WHERE uid = ?");
            db.mTeamsUpdateOne = db.mConnection.prepareStatement("UPDATE Teams SET price = ?, wins = ?, losses = ?, pointsfor = ?, pointsagainst = ?, lastprice = ? WHERE tid = ?");
            db.mOwnershipsUpdateOne = db.mConnection.prepareStatement("UPDATE Ownerships SET count = ?, WHERE uid = ? AND tid = ?");
            
            // Select all prepared statements
            db.mUsersSelectAll = db.mConnection.prepareStatement("SELECT * FROM Users ORDER BY money");
            db.mTeamsSelectAll = db.mConnection.prepareStatement("SELECT * FROM Teams ORDER BY price");
            db.mOwnershipsSelectAll = db.mConnection.prepareStatement("SELECT * FROM Ownerships ORDER BY uid");

            db.mGetOwnerships = db.mConnection.prepareStatement("SELECT * FROM Ownerships WHERE uid = ?");
            db.mGetTeam = db.mConnection.prepareStatement("SELECT * FROM Teams WHERE tid=?");
            db.mGetUser = db.mConnection.prepareStatement("SELECT * FROM Users WHERE uid=?");

            db.mGetUserOwnership = db.mConnection.prepareStatement("SELECT * FROM Ownerships WHERE uid = ? AND tid = ?");
            db.mGetUserID = db.mConnection.prepareStatement("SELECT * FROM Users WHERE username = ?");

            
            // Might just not use these and only use select all
            /*  Select one prepared statements
            db.mMessageSelectOne = db.mConnection.prepareStatement("SELECT * from Messages WHERE mid = ?");
            db.mUsersSelectOne = db.mConnection.prepareStatement("SELECT * FROM Users WHERE uid = ?");
            db.mCommentSelectOne = db.mConnection.prepareStatement("SELECT * FROM Comments WHERE cid = ?");
            db.mLikeSelectOne = db.mConnection.prepareStatement("SELECT * FROM Mlikes WHERE mid = ? AND uid = ?");
            */
        
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

    int userPurchase(int uid, int tid, int amount) {
        int spendable = getUser(uid).money;
        int cost = getTeamPrice(tid) * amount;
        if (spendable < cost) {
            return 0;
        } 
        else {
            if (usersUpdateOne(uid, spendable-cost) == -1) {
                System.out.println("Failed to update user");
                return -1;
            }
            addOwnerships(uid, tid, amount);
        }
        return amount;
    }

    int addOwnerships(int uid, int tid, int amount) {
        int current = getUserOwnership(uid, tid);
        try {
            if (current <= 0) {
                mOwnershipsInsertOne.setInt(1, uid);
                mOwnershipsInsertOne.setInt(2, tid);
                mOwnershipsInsertOne.setInt(3, amount);
                mOwnershipsInsertOne.executeUpdate();
                return amount;
            } else {
                ownershipsUpdateOne(uid, tid, amount + current);
                return current + amount;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
        
    }

    int userSell(int uid, int tid, int amount) {
        int owned = getUserOwnership(uid, tid);
        int gained = 0;
        if (amount >= owned) {
            ownershipsDeleteRow(uid, tid);
            gained = owned * getTeamPrice(tid);
        } else {
            ownershipsUpdateOne(uid, tid, owned - amount);
            gained = amount * getTeamPrice(tid);
        }   
        if (usersUpdateOne(uid, gained + getUser(uid).money) == -1) {
            System.out.println("Failed to update user");
            return -1;
        }
        return gained;
    }

    
    /**
     * Method to either sign up or log in, since they're very similar 
     * 
     * @return -2 for denied access. -1 for error. userID on success
     */
    int signupOrLogin(String username, String password) {
        String name = "";
        String pass = "";
        int id = -1;
        try {
            mGetUserID.setString(1, username);
            ResultSet rs = mGetUserID.executeQuery();
            if (rs.next()) {
                name = rs.getString("username");
                pass = rs.getString("password");
                id = rs.getInt("uid");
            }
            // if username doesn't exist, make new user
            if (name.equals("")) {
                usersInsertRow(username, password);
                return getUserID(username);
            
            // else, check if the password is right
            } else {
                if (!password.equals(pass)) {
                    return -2;
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    int getUserID(String username) {
        try {
            mGetUserID.setString(1, username);
            ResultSet rs = mGetUserID.executeQuery();
            if (rs.next()) {
                return rs.getInt("uid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    int getUserOwnership(int uid, int tid) {
        
        try {
            mGetUserOwnership.setInt(1, uid);
            mGetUserOwnership.setInt(2, tid);
            ResultSet rs = mGetUserOwnership.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Insert a row into the Teams table
     * 
     * @return The number of rows that were inserted
     */
    int teamInsertRow(int tid, String name, int price, int wins, int losses, int pointsfor, int poinstagainst) {
        int count = 0;
        try {
            mTeamsInsertOne.setInt(1, tid);
            mTeamsInsertOne.setString(2, name);
            mTeamsInsertOne.setInt(3, price);
            mTeamsInsertOne.setInt(4, wins);
            mTeamsInsertOne.setInt(5, losses);
            mTeamsInsertOne.setInt(6, pointsfor);
            mTeamsInsertOne.setInt(7, poinstagainst);
            count += mTeamsInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a row into the Users table
     * 
     * @return The number of rows that were inserted
     */
    int usersInsertRow(String username, String password) {
        int count = 0;
        try {
            mUsersInsertOne.setString(1, username);
            mUsersInsertOne.setString(2, password);
            count += mUsersInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Insert a row into the Comments table
     * 
     * @return The number of rows that were inserted
     */
    int ownershipsInsertRow(int uid, int tid, int count) {
        int count_2 = 0;
        try {
            mOwnershipsInsertOne.setInt(1, uid);
            mOwnershipsInsertOne.setInt(2, tid);
            mOwnershipsInsertOne.setInt(3, count);
            count += mOwnershipsInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count_2;
    }

    /**
     * Query Messages for a list of all the content in the table.
     * 
     * @return All message rows, as an ArrayList
     */
    ArrayList<TeamRow> teamsSelectAll() {
        ArrayList<TeamRow> res = new ArrayList<TeamRow>();
        try {
            ResultSet rs = mTeamsSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new TeamRow(rs.getInt("tid"), rs.getString("name"), rs.getInt("price"),
                 rs.getInt("wins"), rs.getInt("losses"), rs.getInt("pointsfor"),
                  rs.getInt("pointsagainst"), rs.getInt("lastprice")));
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
                int uid = rs.getInt("uid");
                int money = rs.getInt("money");
                int networth = getUserStockValue(uid) + money;
                res.add(new UserRow(uid, rs.getString("username"), networth, money, new ArrayList<OwnershipsRow>()));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    UserRow getUser(int uid) {
        try {
            mGetUser.setInt(1, uid);
            ResultSet rs = mGetUser.executeQuery();
            if (rs.next()) {
                int money = rs.getInt("money");
                int networth = getUserStockValue(uid) + money;
                return new UserRow(uid, rs.getString("username"), networth, money, getUserOwnerships(uid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    int getUserStockValue(int uid) {
        int stockVal = 0;
        try {
            mGetOwnerships.setInt(1, uid);
            ResultSet rs = mGetOwnerships.executeQuery();
            while (rs.next()) {
                int thisTeamPrice = getTeamPrice(rs.getInt("tid"));
                stockVal += thisTeamPrice * rs.getInt("count");
            }
            return stockVal;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    int getTeamPrice(int tid) {
        try {
            mGetTeam.setInt(1, tid);
            ResultSet rs = mGetTeam.executeQuery();
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    ArrayList<OwnershipsRow> getUserOwnerships(int uid) {
        ArrayList<OwnershipsRow> res = new ArrayList<OwnershipsRow>();
        try {
            mGetOwnerships.setInt(1, uid);
            ResultSet rs = mGetOwnerships.executeQuery();
            while (rs.next()) {
                res.add(new OwnershipsRow(uid, rs.getInt("tid"), 
                rs.getInt("count")));
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
    ArrayList<OwnershipsRow> ownershipsSelectAll() {
        ArrayList<OwnershipsRow> res = new ArrayList<OwnershipsRow>();
        try {
            ResultSet rs = mOwnershipsSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new OwnershipsRow(rs.getInt("uid"), rs.getInt("tid"), 
                rs.getInt("count")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
     * Delete a row in the Teams table by cid
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int teamsDeleteRow(int id) {
        int res = -1;
        try {
            mTeamsDeleteOne.setInt(1, id);
            res = mTeamsDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row in the Ownerships table by uid and tid
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int ownershipsDeleteRow(int uid, int tid) {
        int res = -1;
        try {
            mOwnershipsDeleteOne.setInt(1, uid);
            mOwnershipsDeleteOne.setInt(2, tid);
            res = mOwnershipsDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Users table
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int usersUpdateOne(int uid, int money) {
        int res = -1;
        try {
            mUsersUpdateOne.setInt(1, money);
            mUsersUpdateOne.setInt(2, uid);
            res = mUsersUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Teams table
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int teamsUpdateOne(int tid, int price, int wins, int losses, int pointsfor, int pointsagainst) {
        int res = -1;
        int oldprice = getTeamPrice(tid);
        try {
            //price = ?, wins = ?, losses = ?, pointsfor = ?, pointsagainst = ?, lastprice = ? WHERE tid = ?
            mTeamsUpdateOne.setInt(1, price);
            mTeamsUpdateOne.setInt(2, wins);
            mTeamsUpdateOne.setInt(3, losses);
            mTeamsUpdateOne.setInt(4, pointsfor);
            mTeamsUpdateOne.setInt(5, pointsagainst);
            mTeamsUpdateOne.setInt(6, oldprice);
            mTeamsUpdateOne.setInt(7, tid);
            res = mTeamsUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the content for a row in the Ownerships table
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int ownershipsUpdateOne(int uid, int tid, int count) {
        int res = -1;
        try {
            mOwnershipsUpdateOne.setInt(1, uid);
            mOwnershipsUpdateOne.setInt(2, tid);
            mOwnershipsUpdateOne.setInt(3, count);
            res = mOwnershipsUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create a new table in the database. Prints an error if the statement doesn't work.
     * NOTE: as of now this just adds all the table I think I need to the database :)
     * 
     */
    boolean createTables() {
        try {
            //TODO remove if statements they're garbage
            if (mCreateUsers.execute()) {
                System.out.println("Couldn't add Users to DB");
            }
            if (mCreateTeams.execute()) {
                System.out.println("Couldn't add Teams to DB");
            }
            if (mCreateOwnerships.execute()) {
                System.out.println("Couldn't add Ownerships to DB");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove tables from the database
     */
    void dropTable(String name) {
        try {
            mDropTables = this.mConnection.prepareStatement("DROP TABLE "+name);
            if (mDropTables.execute()) 
                System.out.println("Couldn't drop "+name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}