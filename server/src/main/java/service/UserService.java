package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {

    private UserDAO userDB;
    private AuthDAO authDB;

    public UserService(UserDAO userDB, AuthDAO authDB){
        this.authDB = authDB;
        this.userDB = userDB;
    }

    public AuthData register(UserData user) throws ResponseException {

        if(!user.isValid()) { throw new ResponseException(400,"Bad Request, Invalid User Data");}
        if(userDB.getUserData(user.username()) != null ) { throw new ResponseException(403,"Username already taken");}

        userDB.addUserData(user);
        return createAuth(user);

    }

    public AuthData login(UserData user) throws ResponseException{

        if(userDB.getUserData(user.username()) == null) { throw new ResponseException(401, "User login information is invalid");}

        return createAuth(user);
    }

    public void logout(AuthData auth) throws ResponseException{
        if(authDB.getAuthData(auth.authToken()) == null) { throw new ResponseException(401, "User logout information is invalid");}

        authDB.removeAuthData(auth);
        throw new ResponseException(500, "Not implemented");
    }

    private AuthData createAuth(UserData user) {
        return new AuthData(makeAuthToken(user),user.username());
    }

    private String makeAuthToken(UserData user) {
        int randomNumber = 2147483647;

        String result = "";
        result += randomNumber + user.hashCode();

        return result;
    }
}
