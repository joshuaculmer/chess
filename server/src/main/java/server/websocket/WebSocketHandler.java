package server.websocket;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.printf("Received: %s", message);
        UserGameCommand usercmd = new Gson().fromJson(message, UserGameCommand.class);
        switch(usercmd.getCommandType()) {
            case CONNECT -> connect(usercmd, session);
            case LEAVE -> leave(usercmd, session);
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    private void connect(UserGameCommand usercmd, Session session) {
        connections.add(usercmd.getUserName(), session, usercmd.getAuthToken(), usercmd.getGameID() );
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(usercmd.getUserName() + " joined the game!");
        try {
            connections.broadcast(usercmd.getGameID(), notification);
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }

    private  void leave(UserGameCommand usercmd, Session session) {
        connections.remove(usercmd.getUserName());
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(usercmd.getUserName() + " left the game!");
        try {
            connections.broadcast(usercmd.getGameID(), notification);
        }
        catch (Exception e) {
            System.out.print(usercmd.getUserName() + " couldn't leave the game " + e);
        }
    }
}