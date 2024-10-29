package dataaccess;
import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class UserDAOSQL implements UserDAO{

    public UserDAOSQL() throws ResponseException{
        configureTable();
    }

    @Override
    public UserData getUserData(String userName) {
        return null;
    }

    @Override
    public void addUserData(UserData data) {
        String statement = "INSERT INTO userDB (name, password, email, json) VALUES (?, ?, ?, ?)";
        Object json = new Gson().toJson(data);
        try {
            executeUpdate(statement, data.username(), data.password(), data.password(), json);
        }
        catch (ResponseException ignored) {}

    }

    @Override
    public void clearUserData() {

    }


    private final String[] createStatementsUserDB = {
            """
            CREATE TABLE IF NOT EXISTS  userDB (
              `name` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` TEXT NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureTable() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatementsUserDB) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex ) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        } catch (DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to connect to database: %s", ex.getMessage()));
        }
    }
}
