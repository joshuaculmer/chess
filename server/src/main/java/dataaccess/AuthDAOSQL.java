package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

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
        String statement = "SELECT * FROM authDB WHERE authToken = '" + authToken + "';";
        try {
            return readAuthData(queryDatabase(statement));
        } catch (ResponseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private AuthData readAuthData(ResultSet rs) {
        try {
            if(rs.next()) {
                var authToken=rs.getString("authToken");
                var username=rs.getString("username");
                return new AuthData(authToken,username);
            }
        }
        catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public void removeAuthData(AuthData authData) {
        String statement = "DELETE FROM authDB WHERE authToken = '" + authData.authToken() + "';";
        try {
            executeUpdate(statement);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
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
