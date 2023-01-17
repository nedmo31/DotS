package admin;

/**
 * The UserRow to mimic a row from the Users table
 */
public class UserRow {

    public final int uid, money;

    public String username, password;

    UserRow(int uid, int money, String username, String password) {
        this.uid = uid; this.money = money;
        this.username = username; this.password = password;
    }
}
