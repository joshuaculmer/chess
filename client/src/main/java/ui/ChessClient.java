package ui;

import static ui.EscapeSequences.*;

public class ChessClient {

    public ChessClient(String url) {

    }
    // this runs all the logic for the chess client


    public String eval(String cmd) {

        return switch(cmd) {
            case "register" -> register();
            case "login" -> login();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String register() {
        return "Register: TODO";
    }

    public String login() {
        return "Login: TODO";
    }

    public String help() {
        return SET_TEXT_COLOR_BLUE +"register <USERNAME> <PASSWORD> <EMAIL> "+ SET_TEXT_COLOR_YELLOW + "- to create an account\n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> "+ SET_TEXT_COLOR_YELLOW + "- to play chess\n" +
                SET_TEXT_COLOR_BLUE + "quit "+ SET_TEXT_COLOR_YELLOW + "- playing chess\n" +
                SET_TEXT_COLOR_BLUE +"help "+ SET_TEXT_COLOR_YELLOW + "- with possible commands\n";
    }
}
