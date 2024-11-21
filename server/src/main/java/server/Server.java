package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private UserDAO userDB = new UserDAOMemory();
    private AuthDAO authDB = new AuthDAOMemory();
    private GameDAO gameDB = new GameDAOMemory();
    private final WebSocketHandler webSocketHandler;
    UserService userSerivceInstance = null;
    GameService gameServiceInstance = null;


    public Server() {
        webSocketHandler = new WebSocketHandler();
        try {
            userDB=new UserDAOSQL();
        } catch (ResponseException ignored) {
            System.out.println("Couldn't connect to User Table");
        }
        try {
            authDB=new AuthDAOSQL();
        } catch (ResponseException ignored) {
            System.out.println("Couldn't connect to Auth Table");
        }
        try {
            gameDB=new GameDAOSQL();
        } catch (ResponseException ignored) {
            System.out.println("Couldn't connect to Game Table");
        }
        userSerivceInstance = new UserService(userDB, authDB);
        gameServiceInstance = new GameService(authDB, gameDB);
    }



    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.webSocket("/ws", webSocketHandler);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logOut);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearAll);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
        Spark.exception(ResponseException.class, this::exceptionHandler);

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

        record GameCreateRequest (String gameName) {}
        GameCreateRequest createRequest = new Gson().fromJson(req.body(), GameCreateRequest.class);
        String gameName =createRequest.gameName;

        return "{\"gameID\": " + gameServiceInstance.createGame(authToken, gameName) + " }";
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
        res.status(e.statusCode());
    }
}
