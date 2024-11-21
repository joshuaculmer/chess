package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String userName;
    public String authToken;
    public int gameID;
    public Session session;

    public Connection(String username, Session session, String authToken, int gameID) {
        this.userName = username;
        this.session = session;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}