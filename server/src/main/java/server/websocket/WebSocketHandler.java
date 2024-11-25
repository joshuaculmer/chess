package server.websocket;
import chess.ChessGame;
import com.google.gson.Gson;

import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import model.UserData;
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
import java.util.Objects;

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
    }

    private void connect(UserGameCommand usercmd, Session session) {
        try {
            GameData gameData = gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID());
            String userName = userService.checkAuthToken(usercmd.getAuthToken());
            ChessGame.TeamColor color = null;
            if(Objects.equals(gameData.whiteUsername(), userName)){
                color = ChessGame.TeamColor.WHITE;
            }
            if(Objects.equals(gameData.blackUsername(), userName)) {
                color = ChessGame.TeamColor.BLACK;
            }
            ServerMessage notification = null;
            if(color == null) {
                notification=new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " joined the game as " + color);
            }
            else {
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " joined the game as observer");
            }
            connections.broadcast(usercmd.getGameID(), notification);
            connections.add(userName, session, usercmd.getAuthToken(), usercmd.getGameID() );
            ChessGame game = gameData.game();
            ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.sendSingleMessage(userName, loadGameMessage);
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }

    private  void leave(UserGameCommand usercmd, Session session) {
        try {
            gameService.leaveGame(usercmd.getAuthToken(), usercmd.getGameID());
            String userName = userService.checkAuthToken(usercmd.getAuthToken());
            connections.remove(userName);
            ServerMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " left the game!");
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