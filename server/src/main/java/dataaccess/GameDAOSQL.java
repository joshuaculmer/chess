package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.util.List;

import static dataaccess.DatabaseManager.configureTable;
import static dataaccess.DatabaseManager.executeUpdate;

public class GameDAOSQL implements GameDAO{

    public GameDAOSQL() throws ResponseException {
        configureTable(createStatementGameDB);
    }

    @Override
    public List<GameData> listGames() {
        return null;
    }

    @Override
    public void addGame(GameData game) {
        String statement = "INSERT INTO gameDB (gameID, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        Object json = new Gson().toJson(game);
        try {
            int gameID = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }

    }

    @Override
    public GameData getGameDataByID(int gameID) {
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
