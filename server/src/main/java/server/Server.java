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
    private GameDAO gameDB = new GameDAOMemory();
    UserService userSerivceInstance = new UserService(userDB, authDB);
    GameService gameServiceInstance = new GameService(authDB, gameDB);

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
        Spark.delete("/session", this::logOut);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", (req, res) -> printAndReturn("Join Game Called"));
        Spark.delete("/db", this::clearAll);
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
            AuthData authData = userSerivceInstance.register(userData);
            return new Gson().toJson(authData);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }
    }

    public Object loginUser(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = userSerivceInstance.login(userData);
            return new Gson().toJson(authData);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }
    }

    public Object logOut(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            userSerivceInstance.logout(authToken);
            return new Gson().toJson(null);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }
    }

    public Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            gameServiceInstance.listGames(authToken);
            return new Gson().toJson(null);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }
    }

    public Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            return "{\"gameID\": " + gameServiceInstance.createGame(authToken, req.body()) + " }";
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }
    }

    public Object joinGame(Request req, Response res) {
        return null;
    }

    public Object clearAll(Request req, Response res) {
        try {
            ClearService.clearAll(userDB, authDB, gameDB);
            return new Gson().toJson(null);
        }
        catch (ResponseException e) {
            res.body(e.toString());
            res.status(e.StatusCode());
            return e.messageToJSON();
        }

    }
}
