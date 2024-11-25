package websocket.commands;

import chess.ChessGame;

public class ConnectUserCommand extends  UserGameCommand{

    ChessGame.TeamColor color;
    public ConnectUserCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(commandType, authToken, gameID);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        return this.color;
    }
}
