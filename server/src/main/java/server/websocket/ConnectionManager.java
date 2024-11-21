package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, String authtoken, int gameID, ChessGame.TeamColor color) {
        var connection = new Connection(username, session, authtoken, gameID);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(int gameID, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (connection.gameID == gameID) {
                    connection.send(notification.toString());
                }
            } else {
                removeList.add(connection);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }
}