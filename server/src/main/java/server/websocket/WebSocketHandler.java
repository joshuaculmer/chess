package server.websocket;
import chess.ChessGame;
import com.google.gson.Gson;

import dataaccess.*;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    UserService userService;
    GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.printf("Received: %s", message);
        UserGameCommand usercmd = new Gson().fromJson(message, UserGameCommand.class);
        switch(usercmd.getCommandType()) {
            case CONNECT -> connect(usercmd, session);
            case LEAVE -> leave(usercmd, session);
        }
//        session.getRemote().sendString("WebSocket response: " + message);
    }

    private void connect(UserGameCommand usercmd, Session session) {
        connections.add(usercmd.getUserName(), session, usercmd.getAuthToken(), usercmd.getGameID() );
        ServerMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, usercmd.getUserName() + " joined the game");
        try {
            ChessGame game = gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID()).game();
            ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(usercmd.getGameID(), notification);
            connections.broadcast(usercmd.getGameID(), loadGameMessage);
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }

    private  void leave(UserGameCommand usercmd, Session session) {
        try {
            gameService.leaveGame(usercmd.getAuthToken(), usercmd.getGameID());
            connections.remove(usercmd.getUserName());
            ServerMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, usercmd.getUserName() + " left the game!");
            try {
                connections.broadcast(usercmd.getGameID(), notification);
            }
            catch (Exception e) {
                System.out.print("Couldn't broadcast notification\n");
            }
        }
        catch (Exception e) {
            System.out.println("Couldn't leave game: " + e);
        }



    }
}