package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.*;


public class ChessClient {

    private State clientState = State.LOGGED_OUT;
    private final ServerFacade facade;
    private String authToken = "";
    private ChessGame game = new ChessGame();
    private ChessGame.TeamColor teamColor;
    private int gameID;
    private ArrayList<GameData> gamesList;


    private final String loggedOutIntro = SET_TEXT_COLOR_WHITE + "Logged Out>>>" ;
    private final String loggedInIntro = SET_TEXT_COLOR_WHITE + "Logged In>>>" ;

    enum State {
        LOGGED_OUT,
        LOGGED_IN,
        IN_GAME
    }

    public ChessClient(String url) {
        facade = new ServerFacade(url);

    }
    // this runs all the logic for the chess client


    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (clientState) {
            case LOGGED_OUT -> switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "send" -> send(params);
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
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> "Game would render here";
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
            return loggedInIntro;
        }
        catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + e.getMessage()+ "\n";
        }
    }

    public String createGame(String... params) {
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

    public String listGames() {
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

    public String joinGame(String... params) {
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
            return "Joined, need to render board" + renderGame(new ChessGame(), teamColor);
        }
        catch (ResponseException e) {
            return e.getMessage()+ "\n";
        }
    }

    public String observeGame(String... params) {
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

    public String logout() {
        try {
            facade.logout(authToken);
            authToken = "";
            clientState = State.LOGGED_OUT;
            return loggedOutIntro;
        }
        catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Couldn't logout\n";
        }
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


    public String renderGame(ChessGame game, ChessGame.TeamColor color) {
        return renderBlack(game) + renderWhite(game);
    }

    public String renderWhite(ChessGame game) {
        String result = "\n";
        ChessBoard board = game.getBoard();
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "   a  b  c  d  e  f  g  h    " +  SET_BG_COLOR_DARK_GREY + "\n";
        for(int row = 7; row >=  0; row--) {
            result += renderPieces(board, row);
        }
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "   a  b  c  d  e  f  g  h    " +  SET_BG_COLOR_DARK_GREY + "\n";

        return result;
    }

    public String renderBlack(ChessGame game) {
        String result = "\n";
        ChessBoard board = game.getBoard();
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "   h  g  f  e  d  c  b  a    " +  SET_BG_COLOR_DARK_GREY + "\n";
        for(int row = 0; row <8; row++) {
            result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + (row + 1) + " ";
            for (int col=7; col >= 0; col--) {
                result += renderPiece(board, row, col);
            }
            result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " +(row + 1) + " " +  SET_BG_COLOR_DARK_GREY;
            result+="\n";
        }
        result += SET_BG_COLOR_LIGHT_GREY +SET_TEXT_COLOR_BLACK + "   h  g  f  e  d  c  b  a    " +  SET_BG_COLOR_DARK_GREY + "\n";

        return result;
    }

    public String renderPieces(ChessBoard board, int row) {
        String result = "";
        result+=SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + (row + 1) + " ";
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

    public String send(String... params) {
        try {
            facade.send(params[0]);
        } catch (Exception e) {
            System.out.print(e.toString());
        }

        return "sent message";
    }
}
