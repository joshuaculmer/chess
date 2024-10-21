package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import java.util.ArrayList;

public class GameService {
    private AuthDAO authDB;
    private GameDAO gameDB;

    public GameService(AuthDAO authdb, GameDAO gamedb) {
        this.authDB = authdb;
        this.gameDB = gamedb;
    }

    public ArrayList<GameData> listGames() {
        return (ArrayList<GameData>) gameDB.listGames();
    }

    public String createGame() {
        return "";
    }

    public void joinGame() {

    }
}
