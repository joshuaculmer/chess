package ui.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;

    public WebSocketFacade(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.print("MEssage from server: " + message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken, String userName, ChessGame.TeamColor color, int gameID) throws ResponseException {

        UserGameCommand usercmd=new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        usercmd.setUserName(userName);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, UserGameCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 83" + e.toString());
        }
    }

    public void leaveGame(String authToken, String userName, ChessGame.TeamColor color, int gameID) throws ResponseException {
        UserGameCommand usercmd=new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        usercmd.setUserName(userName);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(usercmd, UserGameCommand.class));
        } catch (Exception e) {
            System.out.println("Couldn't convert websocket cmd to gson, line 83" + e.toString());
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }
}
