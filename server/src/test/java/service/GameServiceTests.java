package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameServiceTests {

    @Test
    public void ListEmptyDB() throws ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        assert(testService.listGames("12345").isEmpty());
    }

    @Test
    public void ListEmptyDBUnauthorized() throws ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.listGames("1234");
        }
        catch (ResponseException e){
            assertEquals(e, new ResponseException(401, "Error: Unauthorized"));
        }
    }

//    @Test
//    public void AddGameToDB() throws ResponseException {
//        AuthDAO authDB = new AuthDAOMemory();
//        authDB.addAuthData(new AuthData("12345","default"));
//        GameDAO gameDB = new GameDAOMemory();
//        GameService testService = new GameService(authDB, gameDB);
//        try{
//            testService.
//            testService.listGames("1234");
//        }
//        catch (ResponseException e){
//            assertEquals(e, new ResponseException(401, "Error: Unauthorized"));
//        }
//    }

    @Test
    public void AddGameToDB() throws ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "testName");
            assert(!testService.listGames("12345").isEmpty());
        }
        catch (ResponseException e){
            fail();
        }
    }

    @Test
    public void AddGameToDBUnauthorized() throws ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("1234", "testName");
        }
        catch (ResponseException e){
            assertEquals(e, new ResponseException(401, "Error: Unauthorized"));
        }
    }

    @Test
    public void ListAddedGames() throws ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "testName");
            ArrayList<GameData> gameList = testService.listGames("12345");
            assert(!gameList.isEmpty());
            testService.createGame("12345", "other");
            gameList = testService.listGames("12345");
            assert(gameList.size() == 2);

        }
        catch (ResponseException e){
            fail();
        }

    }


    @Test
    public void JoinGameU() throws  ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        authDB.addAuthData(new AuthData("54321", "other"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "Jerry");
            testService.joinGame("12345", ChessGame.TeamColor.WHITE, 1);
            testService.joinGame("54321", ChessGame.TeamColor.BLACK, 1);
        }
        catch (ResponseException e){
            fail();
        }
    }

    @Test
    public void JoinGameUnauthorized() throws  ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "Jerry");
            testService.joinGame("1234", ChessGame.TeamColor.WHITE, 1);
        }
        catch (ResponseException e){
            assertEquals(e, new ResponseException(401, "Error: Unauthorized"));
        }
    }

    @Test
    public void JoinGameWhiteInAlready() throws  ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        authDB.addAuthData(new AuthData("54321", "other"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "Jerry");
            testService.joinGame("12345", ChessGame.TeamColor.WHITE, 1);
            testService.joinGame("54321", ChessGame.TeamColor.WHITE, 1);
        }
        catch (ResponseException e){
            assertEquals(e, new ResponseException(403, "Error: already taken"));
        }
    }

    @Test
    public void JoinGameBlackInAlready() throws  ResponseException {
        AuthDAO authDB = new AuthDAOMemory();
        authDB.addAuthData(new AuthData("12345","default"));
        authDB.addAuthData(new AuthData("54321", "other"));
        GameDAO gameDB = new GameDAOMemory();
        GameService testService = new GameService(authDB, gameDB);
        try{
            testService.createGame("12345", "Jerry");
            testService.joinGame("12345", ChessGame.TeamColor.BLACK, 1);
            testService.joinGame("54321", ChessGame.TeamColor.BLACK, 1);
        }
        catch (ResponseException e){
            assertEquals(e, new ResponseException(403, "Error: already taken"));
        }
    }
}
