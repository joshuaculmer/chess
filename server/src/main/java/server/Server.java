package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {


    private UserDAO userDB = new UserDAOMemory();
    private AuthDAO authDB = new AuthDAOMemory();
    UserService testService = new UserService(userDB, authDB);

    public String printAndReturn(String input) {
        System.out.println(input);
        return input;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", (req,res) -> printAndReturn("Logout User Called"));
        Spark.get("/game", (req, res) -> printAndReturn("List Games Called"));
        Spark.post("/game", (req, res) -> printAndReturn("Create Game Called"));
        Spark.put("/game", (req, res) -> printAndReturn("Join Game Called"));
        Spark.delete("/db", (req, res) -> new Gson().toJson("Not implemented yet"));
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object registerUser(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = testService.register(userData);
            return new Gson().toJson(authData);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return new Gson().toJson("Invalid UserData submitted");
        }
    }

    public Object loginUser(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = testService.login(userData);
            return new Gson().toJson(authData);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return new Gson().toJson("Invalid UserData submitted");
        }
    }

    public Object logOut(Request req, Response res) {
        return null;
    }

    public Object listGames(Request req, Response res) {
        return null;
    }

    public Object createGame(Request req, Response res) {
        return null;
    }

    public Object joinGame(Request req, Response res) {
        return null;
    }
}
