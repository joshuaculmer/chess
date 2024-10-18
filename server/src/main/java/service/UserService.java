package service;

import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {

    public static AuthData register(UserData user, UserDAO userDB) throws ResponseException {

        if(!user.isValid()) { throw new ResponseException(400,"Bad Request, Invalid User Data");}
        if(userDB.getUserData(user.username()) != null ) { throw new ResponseException(403,"Username already taken");}

        userDB.addUserData(user);
        return new AuthData("default", "default");

    }

    public static AuthData login(UserData user) {
        return new AuthData("default", "default");
    }

    public static void logout(AuthData auth) {

    }

}
