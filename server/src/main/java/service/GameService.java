package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import dataaccess.GameDAOSQL;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;
import java.util.Objects;

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

    public GameData getGame(String authToken, int gameID) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}

        return gameDB.getGameDataByID(gameID);
    }

    public int createGame(String authToken, String gameName) throws  ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}
        if(gameDB instanceof GameDAOSQL) {
            int gameID = gameDB.addGame(-1, null, null, gameName, new ChessGame());
            return gameID;
        }
        else {
            int gameID=nextGameID();
            gameDB.addGame(gameID, null, null, gameName, new ChessGame());
            return gameID;
        }
    }

    public void joinGame(String authToken, ChessGame.TeamColor color, int gameID) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}
        GameData gameData = gameDB.getGameDataByID(gameID);
        if(gameData == null || color == null) { throw new ResponseException(400, "Error: bad request");}
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
            default -> throw new ResponseException(400, "Error: bad request");
        }
        if(gameDB instanceof GameDAOMemory) {
            gameDB.addGame(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        else if(gameDB instanceof GameDAOSQL) {
            ((GameDAOSQL) gameDB).setGameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }
    }
    public void leaveGame(String authToken, int gameID) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}
        GameData gameData = gameDB.getGameDataByID(gameID);
        if(gameData == null) { throw new ResponseException(400, "Error: bad request");}
        if (Objects.equals(gameData.whiteUsername(), confirmed.username())){
            if(gameDB instanceof GameDAOMemory) {
                gameDB.addGame(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
            else if(gameDB instanceof GameDAOSQL) {
                ((GameDAOSQL) gameDB).setGameData(gameData.gameID(), null, gameData.blackUsername(),
                        gameData.gameName(), gameData.game());
            }
        }
        if (Objects.equals(gameData.blackUsername(), confirmed.username())) {
            if(gameDB instanceof GameDAOMemory) {
                gameDB.addGame(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            }
            else if(gameDB instanceof GameDAOSQL) {
                ((GameDAOSQL) gameDB).setGameData(gameData.gameID(), gameData.whiteUsername(), null,
                        gameData.gameName(), gameData.game());
            }
        }

    }

    public int nextGameID() {
        return gameIDCounter++;
    }
}
