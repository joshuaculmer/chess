package service;

import dataaccess.UserDAOMemory;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserDAOTests {

    @Test
    public void getUserDataNoDataMemory() {

        UserData expected = new UserData("default", "pw", "example");
        UserDAOMemory testdb = new UserDAOMemory();
        UserData returned = testdb.getUserData(expected.username());
        assertNull(returned);
    }

    @Test
    public void successGetUserDataMemory() {

        UserData expected = new UserData("default", "pw", "example");
        UserDAOMemory testdb = new UserDAOMemory();
        testdb.addUserData(expected);
        UserData returned = testdb.getUserData(expected.username());
        assertEquals(returned, expected);
    }

    @Test
    public void clearUserDataMemory() {
        UserData value1 = new UserData("default", "pw", "example");
        UserData value2 = new UserData("test", "other", "gmail");
        UserDAOMemory testdb = new UserDAOMemory();
        testdb.addUserData(value1);
        testdb.addUserData(value2);
        UserData returned = testdb.getUserData(value1.username());
        assertEquals(returned, value1);
        testdb.clearUserData();
        assertNull(testdb.getUserData(value1.username()));
        assertNull(testdb.getUserData(value2.username()));
    }
}
