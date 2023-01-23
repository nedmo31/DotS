package backend;

import spark.Spark;

import java.util.Map;

import com.google.gson.*;

/**
 * Our app creates an HTTP server to handle requests.
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

        // get the database url from the environment so it's hidden
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

        // get the port and setting for CORS from the environment
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

        // GET route that returns a list of all the users in the database
        // See the route list for the format of the response
        Spark.get("/users", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");

            // uses gson object to send the data as JSON to front 
            return gson.toJson(new StructuredResponse("ok", null, db.usersSelectAll()));
        });

        // GET route that returns a single user and a list of the 
        // teams that they own
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

        // GET route that returns a list of all the teams
        Spark.get("/teams", (request, response) -> {
            response.status(200);
            response.type("application/json");

            return gson.toJson(new StructuredResponse("ok", null, db.teamsSelectAll()));
        });

        // POST route to login. Returns the user id of the new user
        Spark.post("/login", (request, response) -> {
            response.status(200);
            response.type("application/json");

            // Gets the json data sent with the post 
            LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);

            int uid = db.signupOrLogin(req.username, req.password.hashCode());
            if (uid == -2) {
                return gson.toJson(new StructuredResponse("error", "Incorrect Password", uid));
            } else if (uid == -1) {
                return gson.toJson(new StructuredResponse("error", "Error on login", uid));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, uid));
            }
        });


        // POST route to make a trade
        Spark.post("/trade", (request, response) -> {

            response.status(200);
            response.type("application/json");

            // gets the json data sent with the post
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

            return gson.toJson(new StructuredResponse("ok", null, res));

        });

        long sleepTime = 10000000;
        String apiKey = env.get("API_KEY");

        // We want to update the results somewhat regularly
        while(true) {
            sleepTime = db.getConfig(2); // see if the sleeptime has changed
            StatCollector sc = new StatCollector(db, (int)db.getConfig(3), db.getConfig(1), apiKey);
            System.out.println(sc.update()+" games read");

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }

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
}