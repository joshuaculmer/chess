package service;

import dataaccess.AuthDAOMemory;
import dataaccess.UserDAOMemory;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void successRegisterUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        UserService testService = new UserService(userDB, authDB);
        try {
            testService.register(user);
        } catch (ResponseException e) {
            fail("Could not register the user successfully, returned error" + e);
        }
        UserData result = userDB.getUserData(user.username());
        assertEquals(result, user);
    }

    @Test
    public void registerUserTwice() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserData user = new UserData("name", "pw", "mail");
        UserService testService = new UserService(userDB, authDB);
        try {
            testService.register(user);
            testService.register(user);
            fail("Did not throw an error for registering twice");
        } catch (ResponseException e) {
            assertEquals(e, new ResponseException(403, "Username already taken"));
        }
    }

    @Test
    public void successLoginUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            testService.register(user);
            var result = testService.login(user);
            assertNotNull(result);
            assert(result.getClass() == AuthData.class);
        } catch (ResponseException e) {
            fail("Could not log in User when supposed to");
        }
    }

    @Test
    public void loginUserInvalid() {
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


    @Test
    public void successLogoutUser() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            testService.register(user);
            AuthData result = testService.login(user);
            assertNotNull(result);
            assert(result.getClass() == AuthData.class);
            testService.logout(result.authToken());
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void logoutUserNotLoggedIn() {
        UserDAOMemory userDB = new UserDAOMemory();
        AuthDAOMemory authDB = new AuthDAOMemory();
        UserService testService = new UserService(userDB, authDB);
        UserData user = new UserData("name", "pw", "mail");
        try {
            AuthData auth = testService.register(user);
            testService.logout(auth.authToken());
            testService.logout(auth.authToken());
            fail();
        } catch (ResponseException e) {
            assertEquals(e, new ResponseException(401, "User logout information is invalid"));
        }
    }
}
