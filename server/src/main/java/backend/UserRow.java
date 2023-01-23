package backend;

import java.util.ArrayList;

/**
 * The UserRow to mimic a row from the Users table
 */
public class UserRow {

    public final int uid, money, networth;

    public final String username;

    public final ArrayList<NamedOwnership> ownerships;
    public final ArrayList<TransactionsRow> transactions;

    UserRow(int uid, String username, int networth, int money, ArrayList<NamedOwnership> ownerships, ArrayList<TransactionsRow> transactions) {
        this.uid = uid; this.money = money;
        this.username = username; this.networth = networth;
        this.ownerships = ownerships; this.transactions = transactions;
    }
}
