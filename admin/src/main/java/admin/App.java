package admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  As of phase 1, it can create a table, drop a table,
 * select a row or a whole table, and delete/insert/update a row. 
 */
public class App {

    /**
     * Table selection for which to work on
     */
    static void tableMenu() {
        System.out.println("Select which table to work on");
        System.out.println("  [+] Create tables");
        System.out.println("  [-] Drop tables");
        System.out.println("  [U] Users");
        System.out.println("  [O] Ownserships");
        System.out.println("  [T] Teams");
        System.out.println("  [q] Quit the program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Print the user menu for our program 
     */ 
    static void usersMenu() {
        System.out.println("Users Menu");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a row");
        System.out.println("  [~] Update a row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Print the message menu for our program
     */
    static void teamMenu() {
        System.out.println("Team Menu");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [I] Invalidate idea");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /** 
     * Print the comment menu for our program
     */
    static void ownershipsMenu() {
        System.out.println("Ownership Menu");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param actions The set of actions to prompt for
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in, String actions) {
        // The valid actions:
        // String actions = "TDC1*-+~q?";

        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * The users table menu selection
     * 
     * @param db The database on which we are working on
     * @param in The BufferedReader being used
     */
    static void usersTable(Database db, BufferedReader in) {
        while (true) {
            char action = prompt(in, "TD1*-+~Iq?");
            if (action == '?') {
                usersMenu();
            } else if (action == 'q') {
                break;
            } else if (action == '*') {
                ArrayList<UserRow> res = db.usersSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (UserRow rd : res) {
                    System.out.println("  [" + rd.uid + "] Username: " + rd.username + ", Password: " + rd.password + ", Money: " + rd.money);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.usersDeleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String username = getString(in, "Enter the username");
                String password = getString(in, "Enter the password");
                
                int res = db.usersInsertRow(username, password);
                System.out.println("  " + res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int uid = getInt(in, "Enter the user ID");
                int newMoney = getInt(in, "Enter the new money");
                int res = db.usersUpdateOne(uid, newMoney);
                if (res == -1)
                   continue;
                System.out.println("  " + res + " rows updated");
            }
        }
    }

    /**
     * The messages table menu selection
     * 
     * @param db The database on which we are working on
     * @param in The BufferedReader being used
     */
    static void teamsTable(Database db, BufferedReader in) {
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in, "TD1*-+~Iq?");
            if (action == '?') {
                teamMenu();
            } else if (action == 'q') {
                break;
            } else if (action == '*') {
                ArrayList<TeamRow> res = db.teamsSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (TeamRow rd : res) {
                    System.out.println("  [" + rd.tid + "] Name: " + rd.name + ", Record: " + rd.wins + "-" + rd.losses + "\tPoints Record: " + rd.pointsfor + "-" + rd.pointsagainst);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.teamsDeleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String name = getString(in, "Enter the name");
                int price = getInt(in, "Enter the price");
                int wins = getInt(in, "Enter the wins");
                int losses = getInt(in, "Enter the losses");
                int pointsfor = getInt(in, "Enter the pointsfor");
                int pointsagainst = getInt(in, "Enter the pointsagainst");
                int res = db.teamInsertRow(name, price, wins, losses, pointsfor, pointsagainst);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the team ID");
                int price = getInt(in, "Enter the price");
                int wins = getInt(in, "Enter the wins");
                int losses = getInt(in, "Enter the losses");
                int pointsfor = getInt(in, "Enter the pointsfor");
                int pointsagainst = getInt(in, "Enter the pointsagainst");
                int res = db.teamsUpdateOne(id, price, wins, losses, pointsfor, pointsagainst);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
        }
    }

    /**
     * The ownerships table menu selection
     */
    static void ownershipsTable(Database db, BufferedReader in) {
        while (true) {
            char action = prompt(in, "TD1*q-+~?");
            if (action == '?') {
                ownershipsMenu();
            } else if (action == 'q') {
                break;
            } else if (action == '*') {
                ArrayList<OwnershipsRow> res = db.ownershipsSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (OwnershipsRow rd : res) {
                    System.out.println("  uid: " + rd.uid + ", tid: " + rd.tid + ", count: " + rd.count);
                }
            } else if (action == '-') {
                int uid = getInt(in, "Enter the user ID");
                int tid = getInt(in, "Enter the team ID");
                int res = db.ownershipsDeleteRow(uid, tid);
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uid = getInt(in, "Enter the user ID");
                int tid = getInt(in, "Enter the team ID");
                int count = getInt(in, "Enter the count");
                int res = db.ownershipsInsertRow(uid, tid, count);
                System.out.println("  " + res + " rows added");
            } else if (action == '~') {
                int uid = getInt(in, "Enter the user ID");
                int tid = getInt(in, "Enter the team ID");
                int count = getInt(in, "Enter the count");
                int res = db.ownershipsUpdateOne(uid, tid, count);
                System.out.println("  " + res + " rows updated");
            }
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        
        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // Loop trough the menu selection
        while (true) {
            char tableAction = prompt(in, "+-UOTq?");
            if (tableAction == 'q') {
                break;
            } else if (tableAction == '?') {
                tableMenu();
            } else if (tableAction == 'U') {
                usersTable(db, in);
            } else if (tableAction == 'O') {
                ownershipsTable(db, in);
            } else if (tableAction == 'T') {
                teamsTable(db, in);
            } else if (tableAction == '+') {
                db.createTables();
            } else if (tableAction == '-') {
                db.dropTables();
            } else {
                continue;
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }
}