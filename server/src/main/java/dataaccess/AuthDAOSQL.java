package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;

import static dataaccess.DatabaseManager.configureTable;
import static dataaccess.DatabaseManager.executeUpdate;

public class AuthDAOSQL implements AuthDAO{

    public AuthDAOSQL() throws ResponseException {
        configureTable(createStatementsAuthDB);
    }

    @Override
    public void addAuthData(AuthData authdata) {
        String statement = "INSERT INTO authDB (authToken, username, json) VALUES (?, ?, ?)";
        Object json = new Gson().toJson(authdata);
        try {
            executeUpdate(statement, authdata.authToken(), authdata.username(), json);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return null;
    }

    @Override
    public void removeAuthData(AuthData authData) {

    }

    @Override
    public void clearAuthData() {
        String statement = "DELETE FROM authDB";
        try {
            executeUpdate(statement);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
    }

    private final String[] createStatementsAuthDB = {
            """
            CREATE TABLE IF NOT EXISTS  authDB (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
