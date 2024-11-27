package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, String authToken, int gameID) {
        var connection = new Connection(username, session, authToken, gameID);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(int gameID, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (connection.gameID == gameID) {
                    sendServerMessage(serverMessage, connection);
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

    public void broadcast(int gameID, ServerMessage serverMessage, String excludedUsername) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (connection.gameID == gameID && !Objects.equals(connection.userName, excludedUsername)) {
                    sendServerMessage(serverMessage, connection);
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

    private void sendServerMessage(ServerMessage serverMessage, Connection connection) throws IOException {
        ServerMessage.ServerMessageType type = serverMessage.getServerMessageType();
        switch (type) {
            case ServerMessage.ServerMessageType.NOTIFICATION -> {
                connection.send(new Gson().toJson(serverMessage, NotificationMessage.class));
            }
            case ServerMessage.ServerMessageType.LOAD_GAME -> {
                connection.send(new Gson().toJson(serverMessage, LoadGameMessage.class));
            }
            default -> System.out.println("Connection mananager broadcasting unknown servermessage\n");
        }
    }

    public void sendSingleMessage(String username, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (connection.userName == username) {
                    sendServerMessage(serverMessage, connection);
                }
            } else {
                removeList.add(connection);
            }
        }
    }

    public void sendErrorMessage(Session session, ErrorMessage errorMessage) {
        try {
            session.getRemote().sendString(new Gson().toJson(errorMessage, ErrorMessage.class));
        }
        catch (Exception ignore) {
            System.out.println("Couldn't send Error Message");
        }
    }
}