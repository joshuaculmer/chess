package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.DatabaseManager.*;

public class GameDAOSQL implements GameDAO{

    public GameDAOSQL() throws ResponseException {
        configureTable(createStatementGameDB);
    }

    @Override
    public List<GameData> listGames() {
        String statement = "SELECT * FROM gameDB";
        try {
            List<GameData> list = new ArrayList<>();
            ResultSet rs =  queryDatabase(statement);
            GameData data = readGameData(rs);
            while(data != null) {
                list.add(data);
                data = readGameData(rs);
            }
            return list;
        } catch (ResponseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    @Override
    public int addGame(int gameID, String whiteUsername, String blackUserName, String gameName, ChessGame game ) {
        String statement = "INSERT INTO gameDB (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        Object json = new Gson().toJson(game);
        try {
            int id = executeUpdate(statement, null, null, gameName, json);
            return id;
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
            return -1;
        }
    }

    @Override
    public GameData getGameDataByID(int gameID) {
        String statement = "SELECT * FROM gameDB WHERE gameID = '" + gameID + "';";
        try {
            return readGameData(queryDatabase(statement));
        } catch (ResponseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private GameData readGameData(ResultSet rs) {
        try {
            if(rs.next()) {
                var gameID=rs.getInt("gameID");
                var whiteUserName=rs.getString("whiteUserName");
                var blackUserName=rs.getString("blackUserName");
                var gameName = rs.getString("gameName");
                var json = rs.getString("json");
                return new GameData(gameID, whiteUserName, blackUserName, gameName, new Gson().fromJson(json, ChessGame.class));
            }
        }
        catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public void clearGameData() {
        String statement = "DELETE FROM gameDB";
        try {
            executeUpdate(statement);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
    }

    private final String[] createStatementGameDB = {
            """
            CREATE TABLE IF NOT EXISTS  gameDB (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `json` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
