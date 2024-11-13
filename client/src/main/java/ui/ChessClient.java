package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

import static ui.EscapeSequences.*;


public class ChessClient {

    private state clientState = state.LOGGED_OUT;
    private final ServerFacade facade;
    private String authToken = "";
    private ChessGame game;
    private int gameID;

    enum state {
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
                default -> helpLoggedOut();
            };
            case LOGGED_IN -> switch (cmd) {
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> helpLoggedIn();
            };
            case IN_GAME -> "todo";
        };
    }

    public String register(String... params) {
        if(params.length != 3) {
            return helpLoggedOut();
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        UserData user = new UserData(username, password, email);
        try {
            authToken = facade.registerUser(user).authToken();
            clientState = state.LOGGED_IN;
            return SET_TEXT_COLOR_WHITE + "Logged In: " ;
        }
        catch (ResponseException e) {
            return e.getMessage();
        }

    }

    public String login(String... params) {
        if(params.length != 2) {
            return helpLoggedOut();
        }
        String username = params[0];
        String password = params[1];
        UserData user = new UserData(username, password, null);
        try {
            authToken = facade.loginUser(user).authToken();
            clientState = state.LOGGED_IN;
            return SET_TEXT_COLOR_WHITE + "Logged In: " ;
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String createGame() {
        return "Create Game: TODO";
    }

    public String listGames() {
        return "List games: TODO";
    }

    public String joinGame() {
        clientState = state.IN_GAME;
        return "Join Game: TODO";
    }

    public String observeGame() {
        return "Observe Game: TODO";
    }

    public String logout() {
        clientState = state.LOGGED_OUT;
        return "Logout: TODO";
    }



    public String helpLoggedOut() {
        return SET_TEXT_COLOR_BLUE +"register <USERNAME> <PASSWORD> <EMAIL> "+ SET_TEXT_COLOR_YELLOW + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> "+ SET_TEXT_COLOR_YELLOW + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE + "quit "+ SET_TEXT_COLOR_YELLOW + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                SET_TEXT_COLOR_WHITE + "Logged Out:" ;
    }

    public String helpLoggedIn() {
        return SET_TEXT_COLOR_BLUE +"create <NAME> "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "list "+ SET_TEXT_COLOR_YELLOW + "- games\n" +
                SET_TEXT_COLOR_BLUE +"join <ID> [WHITE|BLACK] "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE + "observe <ID> "+ SET_TEXT_COLOR_YELLOW + "- a game\n" +
                SET_TEXT_COLOR_BLUE +"logout "+ SET_TEXT_COLOR_YELLOW + "- when you are done\n" +
                SET_TEXT_COLOR_BLUE + "quit "+ SET_TEXT_COLOR_YELLOW + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                SET_TEXT_COLOR_WHITE + "Logged In: " ;
    }

    public String helpInGame() {
        return "TODO: Implement game commands";
    }
}
