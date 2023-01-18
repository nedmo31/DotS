package admin;

import java.util.Map;

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
        Map<String, String> env = System.getenv();
        String db_url = env.get("TEST_DATABASE_URL");
        
        Database db = Database.getDatabase(db_url);
        assertTrue("Creation of Database", db.createTables());
    }
    
}
