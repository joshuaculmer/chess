package service;

import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    public static AuthData register(UserData user, UserDAO userDB) {

        if(!user.isValid()) { throw new RuntimeException("Invalid User Data, cannot add user");}
        if(userDB.getUserData(user.username()) != null ) { throw new IllegalCallerException("User already in db");}

        userDB.addUserData(user);
        return new AuthData("default", "default");

    }

    public static AuthData login(UserData user) {
        return new AuthData("default", "default");
    }

    public static void logout(AuthData auth) {

    }

}
