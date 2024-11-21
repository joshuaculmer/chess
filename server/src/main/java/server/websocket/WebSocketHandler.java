package server.websocket;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.commands.UserGameCommand;


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
        }
        session.getRemote().sendString("WebSocket response: " + message);
    }

    private void connect(UserGameCommand usercmd, Session session) {
        System.out.print(usercmd);
    }
}