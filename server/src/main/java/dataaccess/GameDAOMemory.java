package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.io.CyclicTimeout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameDAOMemory implements GameDAO{

    HashMap<Integer, GameData> dataBase = new HashMap<>();

    public List<GameData> listGames() {
        return new ArrayList<GameData>(dataBase.values());
    }

    public void addGame(GameData game) {
        dataBase.put(game.gameID(), game);
    }

    public GameData getGameDataByID(String gameID){
        return new GameData(12345,"test","test","test",new ChessGame());
    }

    public void clearGameData()
    {
        System.out.println("Todo");
    }
}
