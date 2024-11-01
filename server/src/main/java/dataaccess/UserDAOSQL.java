package dataaccess;
import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class UserDAOSQL implements UserDAO{

    public UserDAOSQL() throws ResponseException{
        configureTable(createStatementsUserDB);
    }

    @Override
    public UserData getUserData(String userName) {
        String statement = "SELECT * FROM userDB WHERE name = '" + userName + "';";
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
        String statement = "DELETE FROM userDB";
        try {
            executeUpdate(statement);
        }
        catch (ResponseException ignored) {
            System.out.println(ignored.messageToJSON());
        }
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

}
