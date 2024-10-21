package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.GameDAOMemory;
import model.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDAOTests {

    @Test
    public void GetGameDataNoDataMemory() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
    }

    @Test
    public void AddGameToDB() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
        GameData testData =new GameData(1,"white", "black", "testGame", new ChessGame());
        testdb.addGame(testData);
        assert(!testdb.listGames().isEmpty());
    }

    @Test
    public void GetGameFromDB() {
        GameDAO testdb = new GameDAOMemory();
        testdb.listGames();
        assert(testdb.listGames().isEmpty());
        GameData testData =new GameData(1,"white", "black", "testGame", new ChessGame());
        testdb.addGame(testData);
        assert(!testdb.listGames().isEmpty());
        assertEquals(testData, testdb.getGameDataByID(1));
    }


}
