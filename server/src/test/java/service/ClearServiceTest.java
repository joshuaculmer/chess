package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ClearServiceTest {

    @Test
    public void clearAll() {
        AuthDAO authDB = new AuthDAOMemory();
        GameDAO gameDB = new GameDAOMemory();
        UserDAO userDB = new UserDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        authDB.addAuthData(new AuthData("54321", "other"));
        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(userDB, authDB);
        try{
            gameService.createGame("12345", "Jerry");
            gameService.joinGame("12345", ChessGame.TeamColor.WHITE, 1);
            gameService.joinGame("54321", ChessGame.TeamColor.BLACK, 1);
            userService.register(new UserData("username", "password", "mail"));

            assert(authDB.getAuthData("12345") != null);
            assert(gameDB.getGameDataByID(1) != null);
            assert(userDB.getUserData("username") != null);

            ClearService.clearAll(userDB, authDB, gameDB);
            assertEquals(null, authDB.getAuthData("12345"));
            assertEquals(null, gameDB.getGameDataByID(1));
            assertEquals(null, userDB.getUserData("username"));
        }
        catch (ResponseException e){
            fail();
        }
    }
}
