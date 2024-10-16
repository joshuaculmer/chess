package server;

import com.google.gson.Gson;
import service.ClearService;
import spark.*;

public class Server {

    public String printAndReturn(String input) {
        System.out.println(input);
        return input;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        var clearService = new ClearService();
        var serializer = new Gson();

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", (req, res) -> printAndReturn("Register User Called"));
        Spark.post("/session", (req,res) -> printAndReturn("Login User Called"));
        Spark.delete("/session", (req,res) -> printAndReturn("Logout User Called"));
        Spark.get("/game", (req, res) -> printAndReturn("List Games Called"));
        Spark.post("/game", (req, res) -> printAndReturn("Create Game Called"));
        Spark.put("/game", (req, res) -> printAndReturn("Join Game Called"));
        Spark.delete("/db", (req, res) -> serializer.toJson(clearService.clearAll()));
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
