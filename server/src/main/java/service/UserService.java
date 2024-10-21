package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {

    private UserDAO userDB;
    private AuthDAO authDB;

    public UserService(UserDAO userDB, AuthDAO authDB){
        this.authDB = authDB;
        this.userDB = userDB;
    }

    public AuthData register(UserData user) throws ResponseException {

        if(!user.isValid()) { throw new ResponseException(400,"Error: Bad Request");}
        if(userDB.getUserData(user.username()) != null ) { throw new ResponseException(403,"Error: already taken");}

        userDB.addUserData(user);
        AuthData auth = createAuth(user);
        authDB.addAuthData(auth);
        return auth;

    }

    public AuthData login(UserData user) throws ResponseException{
        UserData userData = userDB.getUserData(user.username());
        if(userData == null) { throw new ResponseException(401, "Error: unauthorized");}
        if(!Objects.equals(userData.password(), user.password())) { throw new ResponseException(401, "Error: unauthorized");}
        AuthData authData= createAuth(user);
//        if(authDB.getAuthData(authData.authToken()) != null) { throw new ResponseException(403, "Already signed in");}
        authDB.addAuthData(authData);

        return authData;
    }

    public void logout(String auth) throws ResponseException{
        AuthData confirmed = authDB.getAuthData(auth);
        if(confirmed == null) { throw new ResponseException(401, "Error: unauthorized");}

        authDB.removeAuthData(confirmed);
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
