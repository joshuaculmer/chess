package service;

import dataaccess.UserDAOMemory;
import exception.ResponseException;
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
    public void DatabaseInvalid() {
        System.out.println("TODO");
    }
}
