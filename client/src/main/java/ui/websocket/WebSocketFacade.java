package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.ChessClient;
import websocket.commands.ConnectUserCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;


public class WebSocketFacade extends Endpoint {

    Session session;
    ChessClient client;

    public WebSocketFacade(String url, ChessClient client) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String jsonServerMessage) {
                    ServerMessage serverMessage = new Gson().fromJson(jsonServerMessage, ServerMessage.class);
                    ServerMessage.ServerMessageType type = serverMessage.getServerMessageType();

                    switch (type) {
                        case ServerMessage.ServerMessageType.NOTIFICATION -> {
                            NotificationMessage notificationMessage = new Gson().fromJson(jsonServerMessage, NotificationMessage.class);
                            String message = notificationMessage.getMessage();
                            System.out.println(SET_TEXT_COLOR_LIGHT_GREY + message);
                        }
                        case ServerMessage.ServerMessageType.LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = new Gson().fromJson(jsonServerMessage, LoadGameMessage.class);
                            ChessGame game = loadGameMessage.getGame();
                            client.updateGame(game);
                        }
                        case ServerMessage.ServerMessageType.ERROR -> {
                            ErrorMessage errorMessage = new Gson().fromJson(jsonServerMessage, ErrorMessage.class);
                            System.out.println(SET_TEXT_COLOR_RED + errorMessage.getMessage());
                        }
                    }

                }
            });
            this.client = client;
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken, String userName, ChessGame.TeamColor color, int gameID) throws ResponseException {

        ConnectUserCommand usercmd=new ConnectUserCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, color);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, ConnectUserCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 83" + e.toString());
        }
    }

//    public void onUpdateGame(lam)

    public void leaveGame(String authToken, String userName, ChessGame.TeamColor color, int gameID) throws ResponseException {
        UserGameCommand usercmd=new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, UserGameCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 83" + e.toString());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        MakeMoveCommand usercmd = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken, gameID, move);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, MakeMoveCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 94" + e.toString());
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void resign(String authToken, int gameID) {
        UserGameCommand usercmd=new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, UserGameCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 83" + e.toString());
        }
    }
}
