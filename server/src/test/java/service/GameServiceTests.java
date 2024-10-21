package service;

import dataaccess.AuthDAO;
import dataaccess.AuthDAOMemory;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.Test;

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

}
