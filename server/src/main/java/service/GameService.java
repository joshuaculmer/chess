package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;

public class GameService {
    private AuthDAO authDB;
    private GameDAO gameDB;

    public GameService(AuthDAO authdb, GameDAO gamedb) {
        this.authDB = authdb;
        this.gameDB = gamedb;
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(authToken);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}

        return (ArrayList<GameData>) gameDB.listGames();
    }

    public String createGame() {
        return "";
    }

    public void joinGame() {

    }
}
