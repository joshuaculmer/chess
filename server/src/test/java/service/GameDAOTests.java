package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import dataaccess.GameDAOSQL;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Test;

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
}
