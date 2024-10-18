package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {

    public static AuthData register(UserData user, UserDAO userDB) throws ResponseException {

        if(!user.isValid()) { throw new ResponseException(400,"Bad Request, Invalid User Data");}
        if(userDB.getUserData(user.username()) != null ) { throw new ResponseException(403,"Username already taken");}

        userDB.addUserData(user);
        return createAuth(user);

    }

    public static AuthData login(UserData user, UserDAO userDB) throws ResponseException{

        if(userDB.getUserData(user.username()) == null) { throw new ResponseException(401, "User login information is invalid");}

        return createAuth(user);
    }

    public static void logout(AuthData auth, AuthDAO authDB) throws ResponseException{
        throw new ResponseException(500, "Not implemented");
    }

    private static AuthData createAuth(UserData user) {
        return new AuthData(makeAuthToken(user),user.username());
    }

    private static String makeAuthToken(UserData user) {
        int randomNumber = 2147483647;

        String result = "";
        result += randomNumber + user.hashCode();

        return result;
    }
}
