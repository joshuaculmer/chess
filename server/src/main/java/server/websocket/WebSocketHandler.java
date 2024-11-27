package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectUserCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.Collection;
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
        System.out.printf("Received: %s\n", message);
        UserGameCommand usercmd = new Gson().fromJson(message, UserGameCommand.class);
        switch(usercmd.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectUserCommand.class), session);
            case LEAVE -> leave(usercmd, session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case RESIGN -> resign(usercmd, session);
        }
    }

    private void connect(ConnectUserCommand usercmd, Session session) {
        try {
            String userName = userService.checkAuthToken(usercmd.getAuthToken());
            GameData gameData = gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID());
            if(gameData == null) {
                throw new ResponseException(401, "Error: Invalid GameID");
            }
            ChessGame.TeamColor color = usercmd.getColor();
            ServerMessage notification = null;
            if(color == null) {
                notification=new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " joined the game as observer");
            }
            else {
                notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " joined the game as " + color);
            }
            connections.broadcast(usercmd.getGameID(), notification);
            connections.add(userName, session, usercmd.getAuthToken(), usercmd.getGameID() );
            ChessGame game = gameData.game();
            ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.sendSingleMessage(userName, loadGameMessage);
        }
        catch (Exception e) {
            System.out.print("User couldn't connect" + e + "\n");
            connections.sendErrorMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "ERROR: " + e.getMessage()));
        }
    }

    private void leave(UserGameCommand usercmd, Session session) {
        try {
            String userName = userService.checkAuthToken(usercmd.getAuthToken());
            connections.remove(userName);
            ServerMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, userName + " left the game!");
            try {
                connections.broadcast(usercmd.getGameID(), notification);
            }
            catch (Exception e) {
                System.out.print("Couldn't broadcast notification\n");
            }
            GameData game = gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID());
            if (game != null) {
                gameService.leaveGame(usercmd.getAuthToken(), usercmd.getGameID());
            }
        }
        catch (Exception e) {
            System.out.println("Couldn't leave game: " + e + "\n");
            connections.sendErrorMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage()));
        }
    }

    private void makeMove(MakeMoveCommand usercmd, Session session) {
        try {
            String userName = userService.checkAuthToken(usercmd.getAuthToken());
            GameData gameData = gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID());
            if(gameData == null) {
                throw new ResponseException(401, "Error: Invalid GameID");
            }
            ChessGame game = gameData.game();
            ChessGame.TeamColor turn = game.getTeamTurn();

            if((turn == ChessGame.TeamColor.WHITE &&  ! Objects.equals(gameData.whiteUsername(), userName))) {
                throw new ResponseException(402, "Error: It is white's turn");
            }
            if((turn == ChessGame.TeamColor.BLACK && ! Objects.equals(gameData.blackUsername(), userName))) {
                throw new ResponseException(402, "Error: It is black's turn");
            }

            ChessMove move = usercmd.getMove();
            Collection<ChessMove> moves = game.validMoves(move.getStartPosition());
            if(!moves.contains(move)) {
                throw new ResponseException(402, "Error: Invalid move");
            }
            else {
                game.makeMove(move);
                gameService.updateGame(gameData.gameID(), game);
                String whiteUserName = gameData.whiteUsername();
                String blackUserName = gameData.blackUsername();

                if(game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "Checkmate! " + blackUserName + " has won the game. Better luck next time, " + whiteUserName + "\n"), userName);
                }
                else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "Checkmate! " + whiteUserName + " has won the game. Better luck next time, " + blackUserName + "\n"), userName);
                }
                else if(game.isInCheck(ChessGame.TeamColor.WHITE)) {
                    connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            whiteUserName + " is in check\n"), userName);
                }
                else if(game.isInCheck(ChessGame.TeamColor.BLACK)) {
                    connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            blackUserName + " is in check\n"), userName);
                }
                else {
                    connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            userName + " has made a move\n"), userName);
                }
                connections.broadcast(gameData.gameID(), new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
            }

        }
        catch (Exception e) {
            System.out.println("Couldn't make move: " + e + "\n");
            connections.sendErrorMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage()));
        }
    }

    private void resign(UserGameCommand usercmd, Session session) {
        try {
            String userName=userService.checkAuthToken(usercmd.getAuthToken());
            GameData gameData=gameService.getGame(usercmd.getAuthToken(), usercmd.getGameID());
            if (gameData == null) {
                throw new ResponseException(401, "Error: Invalid GameID");
            }
            if(!(gameData.whiteUsername().equals(userName)|| (gameData.blackUsername().equals(userName)))){
                throw new ResponseException(402, "Error: Invalid resign, "+ userName + " is not a player in this game");
            }
            if(gameData.game().isOver()) {
                throw new ResponseException(401, "Error: Game is already over");
            }

            ChessGame game = gameData.game();
            game.gameOver();
            gameService.updateGame(gameData.gameID(), game);
            connections.broadcast(gameData.gameID(), new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    userName + " has resigned\n"));

        }
        catch ( Exception e) {
            System.out.println("Couldn't make move: " + e + "\n");
            connections.sendErrorMessage(session, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage()));
        }
    }
}