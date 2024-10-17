package service;

import model.AuthData;
import model.UserData;

public class UserService {

    public static AuthData register(UserData user) {

        return new AuthData("default","default");
    }

    public static AuthData login(UserData user) {
        return new AuthData("default", "default");
    }

    public static void logout(AuthData auth) {

    }

}
