package backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class LoginRequest {

    /**
     * the user's username
     */
    public String username;

    /**
     * The user's password, hashed?
     */
    public String password;
}