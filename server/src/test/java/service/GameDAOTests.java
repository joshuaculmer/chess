package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import dataaccess.GameDAOSQL;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameDAOTests {

    @Test
    public void getGameDataNoDataMemory() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
    }

    @Test
    public void addGameToDB() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
        testdb.addGame(1,"white", "black", "testGame", new ChessGame());
        assert(!testdb.listGames().isEmpty());
    }

    @Test
    public void getGameFromDB() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
        GameData testData =new GameData(1,"white", "black", "testGame", new ChessGame());
        testdb.addGame(1,"white", "black", "testGame", new ChessGame());
        assert(!testdb.listGames().isEmpty());
        assertEquals(testData, testdb.getGameDataByID(1));
    }

    @Test
    public void getGameDataNoDataSQL() {
        GameDAO testdb = null;
        try {
            testdb = new GameDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearGameData();
        assert(testdb.listGames().isEmpty());
    }

    @Test
    public void addGameToDBSQL() {
        GameDAO testdb = null;
        try {
            testdb = new GameDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        assert(testdb.listGames().isEmpty());
        testdb.addGame(1,"white", "black", "testGame", new ChessGame());
        assert(!testdb.listGames().isEmpty());
    }

    @Test
    public void clearFromDBSQL() {
        GameDAO testdb = null;
        try {
            testdb = new GameDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.addGame(1,"white", "black", "testGame", new ChessGame());
        assert(!testdb.listGames().isEmpty());
        testdb.clearGameData();
        assert(testdb.listGames().isEmpty());
    }


    @Test
    public void getGameFromDBSQL() {
        GameDAO testdb = null;
        try {
            testdb = new GameDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearGameData();
        assert(testdb.listGames().isEmpty());
        GameData testData =new GameData(1,"white", "black", "testGame", new ChessGame());
        int gameID = testdb.addGame(1,"white", "black", "testGame", new ChessGame());
        List<GameData> list = testdb.listGames();
        assert(!list.isEmpty());
        assertEquals(testData.game(), testdb.getGameDataByID(gameID).game());
    }


    @Test
    public void updateGameFromDBSQL() {
        GameDAOSQL testdb = null;
        try {
            testdb = new GameDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearGameData();
        assert(testdb.listGames().isEmpty());
        int gameID = testdb.addGame(1,null, null, "testGame", new ChessGame());
        List<GameData> list = testdb.listGames();
        assert(!list.isEmpty());
        testdb.setGameData(gameID,"white", null, "testGame", new ChessGame());
        list = testdb.listGames();
        assert(list.size() == 1);
        GameData expected = new GameData(gameID,"white", null, "testGame", new ChessGame());
        GameData result = testdb.getGameDataByID(gameID);
        assert(expected.equals(result));
    }
}
