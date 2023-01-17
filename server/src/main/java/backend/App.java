package backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

// Import Google's JSON library
import com.google.gson.*;

/**
 * For now, our app creates an HTTP server that can only get and add data and add/remove likes.
 */
public class App {

    /**
     * The main string of the program to be run
     * 
     * @param args The list of arguments from the command line
     */
    public static void main(String[] args) {

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        final Gson gson = new Gson();

        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");


        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // Set up the location for serving static files.  If the STATIC_LOCATION
        // environment variable is set, we will serve from it.  Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        Spark.port(getIntFromEnv("PORT", 4567));
        String cors_enabled = env.get("CORS_ENABLED");

        if ("True".equalsIgnoreCase(cors_enabled)) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET route that returns all message titles and Ids.  All we do is get 
        // the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/users", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            return gson.toJson(new StructuredResponse("ok", null, db.usersSelectAll()));
        });

        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested row ID.  If 
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.get("/users/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            UserRow user = db.getUser(idx);
            if (user == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, user));
            }
        });

        Spark.get("/teams", (request, response) -> {
            response.status(200);
            response.type("application/json");

            return gson.toJson(new StructuredResponse("ok", null, db.teamsSelectAll()));
        });

        // // Route to login and get a session ID using your verified email
        // Spark.post("/login", (request, response) -> {
        //     response.status(200);
        //     response.type("application/json");

        //     SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
        //     int uid;
        //     if (!db.userExists(req.mContent)) {
        //         uid = db.addUser(req.mContent);
        //     } else {
        //         uid = db.getUserId(req.mContent);
        //         removePastLogin(userHashMap, uid);
        //     }
            
        //     //TODO check for -1
        //     userHashMap.put(++lastSessionID, uid);

        //     return gson.toJson(new StructuredResponse("ok", null, new Session(lastSessionID, uid)));
        // });


        // POST route to make a trade
        Spark.post("/trade", (request, response) -> {

            response.status(200);
            response.type("application/json");

            TradeRequest req = gson.fromJson(request.body(), TradeRequest.class);

            int res;
            if (req.isBuy) {
                res = db.userPurchase(req.uid, req.tid, req.amount);
            } else {
                res = db.userSell(req.uid, req.tid, req.amount);
            }
            if (res == -1) {
                return gson.toJson(new StructuredResponse("error", "error on purchase/sell", null));
            }

            return gson.toJson(new StructuredResponse("ok", null, null));

        });

    }

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends.  This only needs to be called once.
     * 
     * @param origin The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any 
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * 
     * @param envar      The name of the environment variable to get.
     * @param defaultVal The integer value to use as the default if envar isn't found
     * 
     * @return The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    static void removePastLogin(HashMap<Integer, Integer> hm, int userID) {
        for (Integer i : hm.keySet()) {
            if (hm.get(i) == userID) {
                hm.remove(i);
            }
        }
    }

}