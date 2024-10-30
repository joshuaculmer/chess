package dataaccess;
import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class UserDAOSQL implements UserDAO{

    public UserDAOSQL() throws ResponseException{
        configureTable();
    }

    @Override
    public UserData getUserData(String userName) {
        String statement = "SELECT * FROM userdb WHERE name = '" + userName + "';";
        try {
            return readUserData(queryDatabase(statement));
        } catch (ResponseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private UserData readUserData(ResultSet rs) {
        try {
            if(rs.next()) {
                var name=rs.getString("name");
                var password=rs.getString("password");
                var email=rs.getString("email");
                return new UserData(name, password, email);
            }
        }
        catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public void addUserData(UserData data) {
        String statement = "INSERT INTO userDB (name, password, email, json) VALUES (?, ?, ?, ?)";
        Object json = new Gson().toJson(data);
        try {
            executeUpdate(statement, data.username(), data.password(), data.email(), json);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
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
              `json` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureTable() throws ResponseException {
        try {
            var conn = DatabaseManager.getConnection();
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
