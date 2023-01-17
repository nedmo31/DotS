package backend;
/**
 * A class representing a user's session in the app. Has a unique 
 * sessionID and their userID.
 */
public class Session {

    /**
     * The sessionID associated with this session Object 
     */
    public int sessionID;

    /**
     * The user associated with this session
     */
    public int userID;

    /**
     * Constructor simply takes the sessionID and userID
     * @param s the sessionID
     * @param u the userID
     */
    public Session(int s, int u) {
        sessionID = s;
        userID = u;
    }
}
