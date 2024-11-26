package ui;

import chess.*;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import ui.websocket.WebSocketFacade;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.*;


public class ChessClient {

    private State clientState = State.LOGGED_OUT;
    private final ServerFacade facade;
    private String authToken = "";
    private ChessGame game = null;
    private ChessGame.TeamColor teamColor;
    private int gameID;
    private String userName;
    private ArrayList<GameData> gamesList;
    private WebSocketFacade wsFacade;
    private final String url;


    private final String loggedOutIntro = SET_TEXT_COLOR_WHITE + "Logged Out>>>" ;
    private final String loggedInIntro = SET_TEXT_COLOR_WHITE + "Logged In>>>" ;

    enum State {
        LOGGED_OUT,
        LOGGED_IN,
        IN_GAME
    }

    public ChessClient(String url) {
        facade = new ServerFacade(url);
        this.url = url;

    }
    // this runs all the logic for the chess client


    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "default";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (clientState) {
            case LOGGED_OUT -> switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "uit" -> "quit";
                case "help" -> helpLoggedOut();
                default -> SET_TEXT_COLOR_RED + "Please enter a valid command, type help to view commands\n";
            };
            case LOGGED_IN -> switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "help" -> helpLoggedIn();
                default -> SET_TEXT_COLOR_RED + "Please enter a valid command, type help to view commands\n";
            };
            case IN_GAME -> switch(cmd) {
                case "redraw" -> redraw();
                case "move" -> move(params);
                case "leave" -> leave();
                case "resign" -> resign();
                case "help" -> helpInGame();
                case "highlight" -> highlight();
                default -> SET_TEXT_COLOR_RED + "Please enter a valid command, type help to view commands\n";
            };
        };
    }

    public String register(String... params) {
        if(params.length != 3) {
            return SET_TEXT_COLOR_RED + "Use format: register __username__ __password__ __email__\n";
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        UserData user = new UserData(username, password, email);
        try {
            authToken = facade.registerUser(user).authToken();
            clientState = State.LOGGED_IN;
            this.userName = username;
            return loggedInIntro;
        }
        catch (ResponseException e) {
            return e.getMessage() + "\n";
        }

    }

    public String login(String... params) {
        if(params.length != 2) {
            return SET_TEXT_COLOR_RED + "Use format: login __username__ __password__ __email__";
        }
        String username = params[0];
        String password = params[1];
        UserData user = new UserData(username, password, null);
        try {
            authToken = facade.loginUser(user).authToken();
            clientState = State.LOGGED_IN;
            this.userName = username;
            return loggedInIntro;
        }
        catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + e.getMessage()+ "\n";
        }
    }

    private String createGame(String... params) {
        String gameName = "";
        if(params.length >= 1) {
            for(int i = 0; i < params.length; i++) {
                gameName += params[i];
            }
        }
        try {
            facade.createGame(authToken, gameName);
            return  "Game created";
        }
        catch (ResponseException e) {
            return e.getMessage()+ "\n";
        }
    }

    private String listGames() {
        try {
            gamesList =  facade.listGames(authToken);
            if(gamesList.isEmpty()) {
                return "No games have been created, type create *gameName* to start a new game!";
            }
            else {
                String result = "";
                int id = 1;
                for(GameData game : gamesList) {
                    result += SET_TEXT_COLOR_BLUE + id + ":" + SET_TEXT_COLOR_WHITE + " \n";
                    result += game.gameName() + "\n";
                    if(game.whiteUsername() != null) {
                        result += SET_TEXT_COLOR_YELLOW + "White: " + SET_TEXT_COLOR_BLUE + game.whiteUsername() + "\n";
                    }
                    else {
                        result += SET_TEXT_COLOR_YELLOW + "White: " + SET_TEXT_COLOR_BLUE + "White Available" + "\n";
                    }
                    if(game.blackUsername() != null) {
                        result += SET_TEXT_COLOR_YELLOW + "Black: " + SET_TEXT_COLOR_BLUE + game.blackUsername() + "\n";
                    }
                    else {
                        result += SET_TEXT_COLOR_YELLOW + "Black: " + SET_TEXT_COLOR_BLUE + "Black Available" + "\n";
                    }
                    id++;
                }
                return result;
            }
        }
        catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + e.getMessage()+ "\n";
        }
    }

    private String joinGame(String... params) {
        if(params.length != 2) {
            return SET_TEXT_COLOR_RED + "Use format Join __id__ __white/black__\n";
        }
        int id =0;
        try {
            id=Integer.parseInt(params[0]);
            if(id>gamesList.size() || id <= 0) {
                return SET_TEXT_COLOR_RED + "Please enter a valid id\n";
            }
        }
        catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Error Occured\n";
        }
        String color = params[1];
        ChessGame.TeamColor teamColor = color.equals("WHITE") || color.equals("white") || color.equals("W") || color.equals("w") ?
        ChessGame.TeamColor.WHITE : null;
        teamColor = color.equals("BLACK") || color.equals("black") || color.equals("B") || color.equals("b") ?
                ChessGame.TeamColor.BLACK : teamColor;
        if(teamColor == null) {
            return SET_TEXT_COLOR_RED + "Use format Join __id__ __white/black__\n";
        }
        if(id-1 < gamesList.size()) {
            id = gamesList.get(id-1).gameID();
        }
        else {
            return SET_TEXT_COLOR_RED + "Please enter a valid game ID";
        }
        try {
            facade.joinGame(authToken, teamColor, id);
            this.teamColor = teamColor;
            clientState = State.IN_GAME;
            if(wsFacade == null)
            {
                wsFacade = new WebSocketFacade(url, this);
            }
            wsFacade.joinGame(authToken, userName, teamColor, id);
            gameID = id;
            return "Joined Game!\n";
        }
        catch (ResponseException e) {
            return e.getMessage()+ "\n";
        }
    }

    private String observeGame(String... params) {
        if( params.length != 1) {
            return SET_TEXT_COLOR_RED + "Use format observe __id__\n";
        }
        int id =0;
        try {
            id=Integer.parseInt(params[0]);
            if(id>gamesList.size() || id <= 0) {
                return SET_TEXT_COLOR_RED + "Please enter a valid id\n";
            }
        }
        catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Error Occured\n";
        }
        return renderGame(game, null);
    }

    private String logout() {
        try {
            facade.logout(authToken);
            authToken = "";
            clientState = State.LOGGED_OUT;
            this.userName = null;
            return loggedOutIntro;
        }
        catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Couldn't logout\n";
        }
    }

    public void updateGame(ChessGame game) {
        this.game = game;
        System.out.println((renderGame(game, teamColor)));
    }

    public String helpLoggedOut() {
        return SET_TEXT_COLOR_BLUE +"register <USERNAME> <PASSWORD> <EMAIL> "+ SET_TEXT_COLOR_YELLOW + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> "+ SET_TEXT_COLOR_YELLOW + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE + "quit "+ SET_TEXT_COLOR_YELLOW + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                loggedOutIntro;
    }

    public String helpLoggedIn() {
        return SET_TEXT_COLOR_BLUE +"create <NAME> "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "list "+ SET_TEXT_COLOR_YELLOW + "- games\n" +
                SET_TEXT_COLOR_BLUE +"join <ID> [WHITE|BLACK] "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "observe <ID> "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE +"logout "+ SET_TEXT_COLOR_YELLOW + "- when you are done\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                loggedInIntro;
    }

    public String move(String... params) {
        try {
            ChessMove move = null;
            if(params.length == 2) {
                ChessPosition start = new ChessPosition(params[0]);
                ChessPosition end = new ChessPosition(params[1]);
                move = new ChessMove(start, end, null);
            }
            else if(params.length == 3) {
                ChessPosition start = new ChessPosition(params[0]);
                ChessPosition end = new ChessPosition(params[1]);
                ChessPiece.PieceType promo = switch (params[2].toLowerCase()) {
                    case("n") -> ChessPiece.PieceType.KNIGHT;
                    case("r") -> ChessPiece.PieceType.ROOK;
                    case("q") -> ChessPiece.PieceType.QUEEN;
                    case("b") -> ChessPiece.PieceType.BISHOP;
                    case("knight") -> ChessPiece.PieceType.KNIGHT;
                    case("rook") -> ChessPiece.PieceType.ROOK;
                    case("queen") -> ChessPiece.PieceType.QUEEN;
                    case("bishop") -> ChessPiece.PieceType.BISHOP;
                    default -> throw new ResponseException(402, "Unexpected value: " + params[2]);
                };
                move = new ChessMove(start, end, promo);
            }
            else {
                return SET_TEXT_COLOR_RED + "Use format move <START> <END> <PROMO>\n";
            }
            wsFacade.makeMove(authToken, gameID, move);
        }
        catch (Exception e) {
            return SET_TEXT_COLOR_RED + "An error occured: " + e.getMessage() + "\n";
        }
        return "TODO: implement move\n";
    }

    public String redraw() {
        return renderGame(game, teamColor);
    }

    public String leave() {
        clientState = State.LOGGED_IN;
        try {
            wsFacade.leaveGame(authToken, userName, teamColor, gameID);
        }
        catch (Exception e) {
            System.out.print("Couldn't leave game" + e);
        }
        return "You have left the game\n";
    }

    public String resign() {
        return "TODO: implement resign\n";
    }

    public String highlight() {
        return "TODO: implement highlight\n";
    }

    public String helpInGame() {
        return SET_TEXT_COLOR_BLUE +"Move <START> <END> <PROMO>"+ SET_TEXT_COLOR_YELLOW + "- a piece to another spot\n" +
                SET_TEXT_COLOR_BLUE + "Redraw "+ SET_TEXT_COLOR_YELLOW + "- the board\n" +
                SET_TEXT_COLOR_BLUE +"Leave "+ SET_TEXT_COLOR_YELLOW + "- the game\n" +
                SET_TEXT_COLOR_BLUE + "Resign <ID> "+ SET_TEXT_COLOR_YELLOW + "- the game\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                loggedInIntro;
    }


    public String renderGame(ChessGame game, ChessGame.TeamColor color) {
        if(color == null) {
            return renderBlack(game) + renderWhite(game);
        }
        else if (color == ChessGame.TeamColor.WHITE) {
            return renderWhite(game);
        }
        else if (color == ChessGame.TeamColor.BLACK) {
            return renderBlack(game);
        }
        return null;
    }

    public String renderWhite(ChessGame game) {
        String result = "\n";
        ChessBoard board = game.getBoard();
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " +  SET_BG_COLOR_DARK_GREY + "\n";
        for(int row = 7; row >=  0; row--) {
            result += renderPieces(board, row);
        }
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "    a  b  c  d  e  f  g  h    " +  SET_BG_COLOR_DARK_GREY + "\n";

        return result;
    }

    public String renderBlack(ChessGame game) {
        String result = "\n";
        ChessBoard board = game.getBoard();
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " +  SET_BG_COLOR_DARK_GREY + "\n";
        for(int row = 0; row <8; row++) {
            result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " +(row + 1) + " ";
            for (int col=7; col >= 0; col--) {
                result += renderPiece(board, row, col);
            }
            result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " +(row + 1) + " " +  SET_BG_COLOR_DARK_GREY;
            result+="\n";
        }
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "    h  g  f  e  d  c  b  a    " +  SET_BG_COLOR_DARK_GREY + "\n";

        return result;
    }

    public String renderPieces(ChessBoard board, int row) {
        String result = "";
        result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + (row + 1) + " ";
        for (int col=0; col < 8; col++) {
            result += renderPiece(board, row, col);
        }
        result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " +(row + 1) + " " +  SET_BG_COLOR_DARK_GREY;
        result+="\n";
        return result;
    }

    public String renderPiece(ChessBoard board, int row, int col) {
        String result = "";
        ChessPiece piece = board.getPiece(new ChessPosition(row+1, col+1));
        if((col + row) % 2 == 0) {
            result += SET_BG_COLOR_BLACK;
        }
        else {
            result += SET_BG_COLOR_DARK_GREY;
        }
        if(piece != null) {
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                result += SET_TEXT_COLOR_BLUE;
            }
            else {
                result += SET_TEXT_COLOR_RED;
            }
            result += " " + piece + " ";
        }
        else {
            result += "   ";
        }
        return result;
    }

}
