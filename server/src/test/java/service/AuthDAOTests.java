package service;

import dataaccess.AuthDAOMemory;
import dataaccess.UserDAOMemory;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthDAOTests {

    @Test
    public void getAuthDataNoDataMemory() {
        AuthData expected = new AuthData("TokenValueHere", "username");
        AuthDAOMemory testdb = new AuthDAOMemory();
        AuthData returned = testdb.getAuthData(expected.authToken());
        assertNull(returned);
    }

    @Test
    public void successGetUserDataMemory() {
        AuthData expected = new AuthData("TokenValueHere", "username");
        AuthDAOMemory testdb = new AuthDAOMemory();
        testdb.addAuthData(expected);
        AuthData returned = testdb.getAuthData(expected.authToken());
        assertEquals(returned, expected);
    }

    @Test
    public void clearUserDataMemory() {
        AuthData value1 = new AuthData("token1", "value1");
        AuthData value2 = new AuthData("token2", "value2");
        AuthDAOMemory testdb = new AuthDAOMemory();
        testdb.addAuthData(value1);
        testdb.addAuthData(value2);
        AuthData returned = testdb.getAuthData(value1.authToken());
        assertEquals(returned, value1);

        testdb.clearAuthData();
        assertNull(testdb.getAuthData(value1.authToken()));
        assertNull(testdb.getAuthData(value2.authToken()));
    }
}