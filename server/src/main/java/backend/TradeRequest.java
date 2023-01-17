package backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class TradeRequest {
    /**
     * The uid of the client
     */
    public int uid;

    /**
     * True for a buy request, false for a sell
     */
    public boolean isBuy;

    /**
     * The tid being provided by the client.
     */
    public int tid;

    /**
     * The amount being provided by the client
     */
    public int amount;
}