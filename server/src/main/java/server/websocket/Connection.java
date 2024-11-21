package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    private String userName;
    private String authToken;
    private int gameID;
    private ChessGame.TeamColor color;
    public Session session;

    public Connection(String username, Session session, String authToken, int gameID, ChessGame.TeamColor color) {
        this.userName = username;
        this.session = session;
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}