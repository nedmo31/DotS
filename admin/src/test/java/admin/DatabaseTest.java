package admin;

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

        // Get the database url for testing
        String db_url = "postgres://aoyglvlm:uYWE0iYxFKGqSITz_3s0TbkQq80CaHuj@queenie.db.elephantsql.com/aoyglvlm";
        
        Database db = Database.getDatabase(db_url);
        assertTrue("Creation of Database", db.createTables());
    }
    
}
