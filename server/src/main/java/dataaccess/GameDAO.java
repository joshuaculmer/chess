package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    public List<GameData> listGames();
    public void addGame(GameData game);
    public GameData getGameDataByID(int gameID);
    public void clearGameData();
}
