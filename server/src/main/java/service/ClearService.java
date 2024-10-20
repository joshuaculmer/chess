package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;

public class ClearService {

    public static void clearAll(UserDAO userDB, AuthDAO authDB, GameDAO gameDB) throws ResponseException {
        clearAuthData(authDB);
        clearGameData(gameDB);
        clearUserData(userDB);
    }

    private static void clearUserData(UserDAO userDB) {
        userDB.clearUserData();
    }

    private static void clearGameData(GameDAO gameDB) {
        gameDB.clearGameData();
    }

    private static void clearAuthData(AuthDAO authDB) {
        authDB.clearAuthData();
    }
}
