package backend;

import java.util.ArrayList;

/**
 * The UserRow to mimic a row from the Users table
 */
public class UserRow {

    public final int uid, money, networth;

    public String username;

    public ArrayList<NamedOwnership> ownerships;

    UserRow(int uid, String username, int networth, int money, ArrayList<NamedOwnership> ownerships) {
        this.uid = uid; this.money = money;
        this.username = username; this.networth = networth;
        this.ownerships = ownerships;
    }
}
