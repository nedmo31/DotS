package edu.lehigh.cse216.nje225.admin;

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
        System.out.println("  [T] Create table");
        System.out.println("  [D] Drop table");
        System.out.println("  [1] Query for a specific row");
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
        System.out.println("Messages Menu");
        System.out.println("  [T] Create table");
        System.out.println("  [D] Drop table");
        System.out.println("  [1] Query for a specific row");
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
        System.out.println("Comments Menu");
        System.out.println("  [T] Create table");
        System.out.println("  [D] Drop table");
        System.out.println("  [1] Query for a specific row");
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
            } else if (action == 'T') {
                String columns = getString(in, "Enter the list of columns as you would in SQL" +                    // uid int PRIMARY KEY NOT NULL
                                            "(e.g. title VARCHAR(50) NOT NULL, content VARCHAR(1024) NOT NULL");    // email VARCHAR(50) NOT NULL
                db.createTable("Users", columns);                                                        // username VARCHAR(50) NOT NULL
            } else if (action == 'D') {                                                                             // gender VARCHAR(50) NOT NULL
                db.dropTable("Users");                                                                   // sexualor VARCHAR(50) NOT NULL
            } else if (action == 'C') {                                                                             // note VARCHAR(1024) NOT NULL
                db.changeTable("Users");                                                                 // uvalid BOOL NOT NULL
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                UserRow res = db.usersSelectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mUid + "] Email: " + res.mEmail);
                    System.out.println("  --> Username: " + res.mUsername);
                    System.out.println("  --> Gender: " + res.mGender);
                    System.out.println("  --> Sexualor: " + res.mSexual);
                    System.out.println("  --> Note: " + res.mNote);
                    System.out.println("  --> Valid: " + res.mValid);
                }
            } else if (action == '*') {
                ArrayList<UserRow> res = db.usersSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (UserRow rd : res) {
                    System.out.println("  [" + rd.mUid + "] Email: " + rd.mEmail + ", Username: " + rd.mUsername + ", Gender: " + rd.mGender + ", Seuxal: " + rd.mSexual + ", Note: " + rd.mNote + ", Valid: " + rd.mValid);
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
                String email = getString(in, "Enter the email");
                String username = getString(in, "Enter the username");
                String gender = getString(in, "Enter the gender identity");
                String sexual = getString(in, "Enter the sexual orientation");
                String note = getString(in, "Enter the note");
                if(email.equals("") || username.equals("") || gender.equals("") || sexual.equals("") || note.equals(""))
                    continue;
                int res = db.usersInsertRow(email, username, gender, sexual, note);
                System.out.println("  " + res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                String newEmail = getString(in, "Enter the new email");
                String newUsername = getString(in, "Enter the new username");
                String newGender = getString(in, "Enter the new gender identity");
                String newSexual = getString(in, "Enter the new sexual orientation");
                String newNote = getString(in, "Enter the new note");
                int res = db.usersUpdateOne(id, newEmail, newUsername, newGender, newSexual, newNote);
                if (res == -1)
                   continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'I') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                boolean valid = !(db.usersSelectOne(id).mValid);
                db.invalidateUser(id, valid);
            }
        }
    }

    /**
     * The messages table menu selection
     * 
     * @param db The database on which we are working on
     * @param in The BufferedReader being used
     */
    static void messagesTable(Database db, BufferedReader in) {
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in, "TD1*-+~Iq?");
            if (action == '?') {
                messagesMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                String columns = getString(in, "Enter the list of columns as you would in SQL" +                        // mid int PRIMARY KEY NOT NULL
                                                "(e.g. title VARCHAR(50) NOT NULL, content VARCHAR(1024) NOT NULL");    // title VARCHAR(50) NOT NULL
                db.createTable("Messages", columns);                                                         // content VARCHAR(1024) NOT NULL
            } else if (action == 'D') {                                                                                 // uid int NOT NULL FOREIGN KEY REFERENCES Users(uid)
                db.dropTable("Messages");                                                                    // mvalid BOOL NOT NULL
            } else if (action == 'C') {
                db.changeTable("Messages");
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                DataRow res = db.messageSelectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] Title: " + res.mTitle);
                    System.out.println("  --> Content: " + res.mContent);
                    System.out.println("  --> Valid: " + res.mValid);
                }
            } else if (action == '*') {
                ArrayList<DataRow> res = db.messageSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (DataRow rd : res) {
                    System.out.println("  [" + rd.mId + "] Title: " + rd.mTitle + ", Message: " + rd.mContent + "\tValid: " + rd.mValid);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.messageDeleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int uid = getInt(in, "Enter the User ID");
                if (uid == -1)
                    continue;
                String subject = getString(in, "Enter the subject");
                String content = getString(in, "Enter the content");
                if (subject.equals("") || content.equals(""))
                    continue;
                int res = db.messageInsertRow(uid, subject, content);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newtitle = getString(in, "Enter the new title");
                String newcontent = getString(in, "Enter the new content");
                int res = db.messageUpdateOne(id, newtitle, newcontent);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            } else if (action == 'I') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                boolean valid = !db.messageSelectOne(id).mValid;
                db.invalidateIdea(id, valid);
            }
        }
    }

    /**
     * The commments table menu selection
     * 
     * @param db The database on which we are working on
     * @param in The BufferedReader being used
     */
    static void commentsTable(Database db, BufferedReader in) {
        while (true) {
            char action = prompt(in, "TD1*-+~q?");
            if (action == '?') {
                commentsMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                String columns = getString(in, "Enter the list of columns as you would in SQL " +   // cid int NOT NULL PRIMARY KEY
                "(e.g. title VARCHAR (50) NOT NULL, content VARCHAR(1024) NOT NULL");               // mid int NOT NULL FOREIGN KEY REFERENCES Messages(mid)
                db.createTable("Comments", columns);                                     // uid int NOT NULL FOREIGN KEY REFERENCES Users(uid)
            } else if (action == 'D') {                                                             // content VARCHAR(1024) NOT NULL
                db.dropTable("Comments");
            } else if (action == 'C') {
                db.changeTable("Comments");
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                CommentRow res = db.commentSelectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mCid + "] Mid: " + res.mMid + ", Uid: " + res.mUid);
                    System.out.println("  --> Content: " + res.mContent);
                }
            } else if (action == '*') {
                ArrayList<CommentRow> res = db.commentSelectAll();
                if (res == null) 
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (CommentRow rd : res) {
                    System.out.println("  [" + rd.mCid + "] Mid: " + rd.mMid + ", Uid: " + rd.mUid + ", Content: " + rd.mContent);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.commentDeleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int mid = getInt(in, "Enter the Message ID");
                int uid = getInt(in, "Enter the User ID");
                if (mid == -1 || uid == -1)
                    continue;
                String content = getString(in, "Enter the content");
                if (content.equals(""))
                    continue;
                int res = db.commentInsertRow(mid, uid, content);
                System.out.println("  " + res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                String newcontent = getString(in, "Enter the new content");
                if (newcontent.equals(""))
                    continue;
                int res = db.commentUpdateOne(id, newcontent);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
        }
    }

    /**
     * The likes table menu selection
     * 
     * @param db The database on which we are working on
     * @param in The BufferedReader being used
     */
    static void likesTable(Database db, BufferedReader in) {
        while (true) {
            char action = prompt(in, "TD1*q-+~?");
            if (action == '?') {
                likesMenu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                String columns = getString(in, "Enter the list of columns as you would in SQL " +   // uid int NOT NULL FOREIGN KEY REFERENCES Users(uid)
                "(e.g. title VARCHAR (50) NOT NULL, content VARCHAR(1024) NOT NULL");               // mid int NOT NULL FOREIGN KEY REFERENCES Messages(mid)
                db.createTable("Mlikes", columns);                                       // like_val int NOT NULL
            } else if (action == 'D') {
                db.dropTable("Mlikes");
            } else if (action == '1') {
                int mid = getInt(in, "Enter the Message ID");
                int uid = getInt(in, "Enter the User ID");
                if (mid == -1 || uid == -1)
                    continue;
                OwnershipsTable res = db.likeSelectOne(mid, uid);
                if (res != null) {
                    System.out.println("  Mid: " + res.mMid);
                    System.out.println("  --> Uid: " + res.mUid);
                    System.out.println("  --> Like_val: " + res.mLike_val);
                }
            } else if (action == '*') {
                ArrayList<OwnershipsTable> res = db.likeSelectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (OwnershipsTable rd : res) {
                    System.out.println("  Mid: " + rd.mMid + ", Uid: " + rd.mUid + ", Like_val: " + rd.mLike_val);
                }
            } else if (action == '-') {
                int mid = getInt(in, "Enter the Message ID");
                int uid = getInt(in, "Enter the User ID");
                if (mid == -1 || uid == -1)
                    continue;
                int res = db.likeDeleteRow(mid, uid);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                int mid = getInt(in, "Enter the Message ID");
                int uid = getInt(in, "Enter the User ID");
                int like_val = getInt(in, "Enter the like val (1 for like, -1 for dislike)");
                if (mid == -1 || uid == -1)
                    continue;
                int res = db.likeInsertRow(mid, uid, like_val);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows added");
            } else if (action == '~') {
                int mid = getInt(in, "Enter the Message ID");
                int uid = getInt(in, "Enter the User ID");
                int like_val = getInt(in, "Enter the like val (1 for like, -1 for dislike)");
                if (mid == -1 || uid == -1)
                    continue;
                int res = db.likeUpdateOne(mid, uid, like_val);
                if (res == -1)
                    continue;
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
        
        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // Loop trough the menu selection
        while (true) {
            char tableAction = prompt(in, "UMCLq?");
            if (tableAction == 'q') {
                break;
            } else if (tableAction == '?') {
                tableMenu();
            } else if (tableAction == 'U') {
                usersTable(db, in);
            } else if (tableAction == 'M') {
                messagesTable(db, in);
            } else if (tableAction == 'C') {
                commentsTable(db, in);
            } else if (tableAction == 'L') {
                likesTable(db, in);
            } else {
                continue;
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }
}