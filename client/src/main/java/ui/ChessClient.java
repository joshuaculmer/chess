package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.*;


public class ChessClient {

    private state clientState = state.LOGGED_OUT;
    private final ServerFacade facade;
    private String authToken = "";
    private ChessGame game;
    private ChessGame.TeamColor teamColor;
    private int gameID;

    private final String loggedOutIntro = SET_TEXT_COLOR_WHITE + "Logged Out:" ;
    private final String loggedInIntro = SET_TEXT_COLOR_WHITE + "Logged In:" ;

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
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> helpLoggedIn();
            };
            case IN_GAME -> "Game would render here";
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
            return loggedInIntro;
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
            return loggedInIntro;
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) {
        if(params.length > 1) {
            return helpLoggedIn();
        }
        String gameName = params[0];
        try {
            facade.createGame(authToken, gameName);
            return  "Game created";
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String listGames() {
        try {
            ArrayList<GameData> list =  facade.listGames(authToken);
            if(list.isEmpty()) {
                return "No games have been created, type create *gameName* to start a new game!";
            }
            else {
                String result = "";
                for(GameData game : list) {
                    result += game.gameID() + "\n";
                }
                return result;
            }

        }
        catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) {
        if(params.length != 2) {
            return helpLoggedIn();
        }
        int id = Integer.parseInt(params[0]);
        String color = params[1];
        ChessGame.TeamColor teamColor = color.equals("WHITE") || color.equals("white") || color.equals("W") || color.equals("w") ?
        ChessGame.TeamColor.WHITE : null;
        teamColor = color.equals("BLACK") || color.equals("black") || color.equals("B") || color.equals("b") ?
                ChessGame.TeamColor.BLACK : teamColor;
        try {
            facade.joinGame(authToken, teamColor, id);
            this.teamColor = teamColor;
            clientState = state.IN_GAME;
            return "Joined, need to render board" + renderGame(game, teamColor);
        }
        catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String observeGame() {
        return "Observe Game: TODO";
    }

    public String logout() {
        try {
            facade.logout(authToken);
            authToken = "";
            clientState = state.LOGGED_OUT;
            return loggedOutIntro;
        }
        catch (ResponseException e) {
            return "Couldn't logout :/";
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
                SET_TEXT_COLOR_BLUE + "quit "+ SET_TEXT_COLOR_YELLOW + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n" +
                loggedInIntro;
    }

    public String helpInGame() {
        return "TODO: Implement game commands";
    }

    public String renderGame(ChessGame game, ChessGame.TeamColor color) {
        return renderBlack(game) + renderWhite(game);
    }

    public String renderWhite(ChessGame game) {

        String result = "\n";
        result += SET_BG_COLOR_LIGHT_GREY + "    a " + " b " + " c " + " d " + " e " + " f " + " g    \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 8 a " + " b " + " c " + " d " + " e " + " f " + " g   8 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 7 a " + " b " + " c " + " d " + " e " + " f " + " g   7 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 6 a " + " b " + " c " + " d " + " e " + " f " + " g   6 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 5 a " + " b " + " c " + " d " + " e " + " f " + " g   5 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 4 a " + " b " + " c " + " d " + " e " + " f " + " g   4 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 3 a " + " b " + " c " + " d " + " e " + " f " + " g   3 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 2 a " + " b " + " c " + " d " + " e " + " f " + " g   2 \n";
        result += SET_BG_COLOR_LIGHT_GREY + " 1 a " + " b " + " c " + " d " + " e " + " f " + " g   1 \n";
        result += SET_BG_COLOR_LIGHT_GREY + "    a " + " b " + " c " + " d " + " e " + " f " + " g    \n";

        return result;
    }

    public String renderBlack(ChessGame game) {
        return "";
    }
}
