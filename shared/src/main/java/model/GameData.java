package model;

import chess.ChessGame;

import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername,
                       String gameName, ChessGame game) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameData gameData=(GameData) o;

        if (gameID != gameData.gameID) return false;
        if (!Objects.equals(whiteUsername, gameData.whiteUsername))
            return false;
        if (!Objects.equals(blackUsername, gameData.blackUsername))
            return false;
        if (!Objects.equals(gameName, gameData.gameName)) return false;
        return game.equals(gameData.game);
    }

    @Override
    public int hashCode() {
        int result=gameID;
        result=31 * result + (whiteUsername != null ? whiteUsername.hashCode() : 0);
        result=31 * result + (blackUsername != null ? blackUsername.hashCode() : 0);
        result=31 * result + (gameName != null ? gameName.hashCode() : 0);
        result=31 * result + game.hashCode();
        return result;
    }
}