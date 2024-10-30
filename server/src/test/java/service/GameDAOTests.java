package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import model.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
