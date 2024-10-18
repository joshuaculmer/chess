package service;

import dataaccess.AuthDAOMemory;
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
        UserService testService = new UserService(userDB, null);
        try {
            testService.register(user);
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
        UserService testService = new UserService(userDB, null);
        try {
            testService.register(user);
            testService.register(user);
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
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            testService.register(user);
            AuthData result = testService.login(user);
            assertEquals(result, new AuthData( Integer.toString(2147483647 + user.hashCode()), user.username()));
        } catch (ResponseException e) {
            fail("Could not log in User when supposed to");
        }
    }

    @Test
    public void LoginUserInvalid() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            testService.login(user);
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
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            testService.register(user);
            AuthData auth = testService.login(user);
            assertEquals(auth, new AuthData( Integer.toString(2147483647 + user.hashCode()), user.username()));
//            UserService.logout(auth);
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void LogoutUserNotLoggedIn() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            AuthData auth = testService.register(user);
            AuthData authExpected =new AuthData( Integer.toString(2147483647 + user.hashCode()), "Joe");

            testService.logout(auth);
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
