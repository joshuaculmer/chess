package service;

import dataaccess.UserDAOMemory;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class UserServiceTests {

    @Test
    public void SuccessRegisterUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.register(user, userDB);
        } catch (ResponseException e) {
            fail("Could not register the user successfully, returned error" + e.toString());
        }
        UserData result = userDB.getUserData(user.username());
        assertEquals(result, user);
    }

    @Test
    public void RegisterUserTwice() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.register(user, userDB);
            UserService.register(user, userDB);
            fail("Did not throw an error for registering twice");
        } catch (ResponseException e) {
            assertEquals(e, new ResponseException(403, "Username already taken"));
        }
    }

    @Disabled
    public void RegisterUserDBInvalid() {
        System.out.println("TODO");
    }

    @Test
    public void SuccessLoginUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.register(user, userDB);
            AuthData result = UserService.login(user, userDB);
            assertEquals(result, new AuthData( Integer.toString(2147483647 + user.hashCode()), user.username()));
        } catch (ResponseException e) {
            fail("Could not log in User when supposed to");
        }
    }

    @Test
    public void LoginUserInvalid() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.login(user, userDB);
            fail("Did not throw error when User was not in DB");
        } catch (ResponseException e) {
            assertEquals(e, new ResponseException(401, "User login information is invalid"));
        }
    }

    @Disabled
    public void LoginUserDBInvalid() {
        UserDAOMemory userDB = new UserDAOMemory(); // fail here
        UserData user = new UserData("name", "pw", "mail");
    }


    @Test
    public void SuccessLogoutUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.register(user, userDB);
            AuthData auth = UserService.login(user, userDB);
            assertEquals(auth, new AuthData( Integer.toString(2147483647 + user.hashCode()), user.username()));
//            UserService.logout(auth);
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void LogoutUserNotLoggedIn() {
        UserDAOMemory userDB = new UserDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        try {
            UserService.register(user, userDB);
            AuthData auth =new AuthData( Integer.toString(2147483647 + user.hashCode()), "Joe");
//            UserService.logout();
            fail();
        } catch (ResponseException e) {
            assertEquals(e, new ResponseException(401, "User logout information is invalid"));
        }
    }

    @Disabled
    public void LogoutUserDBInvalid() {
        UserDAOMemory userDB = new UserDAOMemory(); // fail here
        UserData user = new UserData("name", "pw", "mail");
    }
}
