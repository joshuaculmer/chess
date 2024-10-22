package server;

import chess.ChessGame;
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
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearAll);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object registerUser(Request req, Response res) throws ResponseException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = userSerivceInstance.register(userData);
        return new Gson().toJson(authData);
    }

    public Object loginUser(Request req, Response res) throws ResponseException{
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = userSerivceInstance.login(userData);
        return new Gson().toJson(authData);
    }

    public Object logOut(Request req, Response res) throws ResponseException{
        String authToken = req.headers("Authorization");
        userSerivceInstance.logout(authToken);
        return new Gson().toJson(null);
    }

    public Object listGames(Request req, Response res) throws ResponseException {
        String authToken = req.headers("Authorization");
        return "{ \"games\": " + new Gson().toJson(gameServiceInstance.listGames(authToken)) + "}";
    }

    public Object createGame(Request req, Response res) throws ResponseException{
        String authToken = req.headers("Authorization");
        return "{\"gameID\": " + gameServiceInstance.createGame(authToken, req.body()) + " }";
    }

    public Object joinGame(Request req, Response res) throws ResponseException{
        String authToken = req.headers("Authorization");

        record JoinRequest (ChessGame.TeamColor playerColor, int gameID){}

        JoinRequest joinRequest  = new Gson().fromJson(req.body(), JoinRequest.class);
        gameServiceInstance.joinGame(authToken, joinRequest.playerColor, joinRequest.gameID);
        return new Gson().toJson(null);
    }

    public Object clearAll(Request req, Response res) throws ResponseException{
        ClearService.clearAll(userDB, authDB, gameDB);
        return new Gson().toJson(null);
    }

    private void exceptionHandler(ResponseException e, Request req, Response res) {
        res.body(e.messageToJSON());
        res.status(e.StatusCode());
    }
}
