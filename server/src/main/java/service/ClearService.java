package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {

    public String clearAll(UserDAO userDB, AuthDAO authDB, GameDAO gameDB){
        clearAuthData(authDB);
        clearGameData(gameDB);
        clearUserData(userDB);

        return ""; // Success
    }

    public void clearUserData(UserDAO userDB) {
        userDB.clearUserData();
    }

    public void clearGameData(GameDAO gameDB) {
        gameDB.clearGameData();
    }

    public void clearAuthData(AuthDAO authDB) {
        authDB.clearAuthData();
    }
}
