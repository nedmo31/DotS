package backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatabaseTest extends TestCase {
    /**
     * Create the test case
     * 
     * @param testName name of the test case
     */
    public DatabaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    public void testDatabase() {

        // String db_url = "postgres://aoyglvlm:uYWE0iYxFKGqSITz_3s0TbkQq80CaHuj@queenie.db.elephantsql.com/aoyglvlm";
        
        // Database db = Database.getDatabase(db_url);
        // assertTrue("Creation of Database", db.createTables());
        
        // int uid = db.signupOrLogin("test_user", "test_pass".hashCode());
        // assertTrue(uid >= 1);
        // int tid = 12345;
        // // try {
        // db.teamsDeleteRow(tid);
        // //db.usersDeleteRow(uid);
        // //db.ownershipsDeleteRow(uid, tid);// } catch (Exception e) {System.out.println("did the thing"); }

        // db.teamInsertRow(tid, "test_team", 40, 2, 1, 100, 60);
        // assertTrue("first buy", db.userPurchase(uid, tid, 1) == 1);
        // assertTrue("second buy", db.userPurchase(uid, tid, 1) == 2);

        // UserRow user = db.getUser(uid);
        // assertTrue("User Info", user.username.equals("test_user") && user.uid == uid);
        // assertTrue("Team Price Initial", db.getTeamPrice(tid) == 40);
        // assertTrue("Ownership count test", db.getUserOwnership(uid, tid) == 2);

        // db.teamsUpdateOne(tid, 45, 3, 1, 130, 80);
        // assertTrue("Team Price Change", db.getTeamPrice(tid) == 45);

        // assertTrue("first sell", db.userSell(uid, tid, 1) == 45);
        // assertTrue("second sell", db.userSell(uid, tid, 1) == 45);
        // assertTrue("money from sale", db.getUser(uid).money == 210);
        // assertTrue("Ownership count test 2", db.getUserOwnership(uid, tid) == 0);



        // db.teamsDeleteRow(tid);
        // db.usersDeleteRow(uid);
        // db.ownershipsDeleteRow(uid, tid);
        
    }
    
}
