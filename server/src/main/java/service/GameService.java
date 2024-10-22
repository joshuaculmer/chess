package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;

public class GameService {
    private AuthDAO authDB;
    private GameDAO gameDB;
    private int gameIDCounter;

    public GameService(AuthDAO authdb, GameDAO gamedb) {
        this.authDB = authdb;
        this.gameDB = gamedb;
        gameIDCounter = 1;
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}

        return (ArrayList<GameData>) gameDB.listGames();
    }

    public int createGame(String authToken, String gameName) throws  ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}
        int gameID = nextGameID();
        gameDB.addGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    public void joinGame(String authToken, ChessGame.TeamColor color, int gameID) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}
        GameData gameData = gameDB.getGameDataByID(gameID);
        switch(color){
            case WHITE -> {
                if(gameData.whiteUsername() != null) {throw new ResponseException(403, "Error: already taken");}
                gameData = new GameData(gameID, confirmed.username(), gameData.blackUsername(),
                        gameData.gameName(), gameData.game());
            }
            case BLACK -> {
                if(gameData.blackUsername() != null) {throw new ResponseException(403, "Error: already taken");}
                gameData = new GameData(gameID, gameData.whiteUsername(), confirmed.username(),
                        gameData.gameName(), gameData.game());
            }
        }
        gameDB.addGame(gameData);
    }

    public int nextGameID() {
        return gameIDCounter++;
    }
}
