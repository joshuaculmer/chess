package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {

    public List<GameData> listGames();
    public int addGame(int gameID, String whiteUsername, String blackUserName, String gameName, ChessGame game);
    public GameData getGameDataByID(int gameID);
    public void clearGameData();
}
