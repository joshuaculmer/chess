package dataaccess;

import dataaccess.AuthDAO;
import dataaccess.AuthDAOMemory;
import dataaccess.AuthDAOSQL;
import dataaccess.UserDAOMemory;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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


    @Test
    public void clearUserDataSQL() {
        AuthData value1 = new AuthData("token1", "value1");
        AuthData value2 = new AuthData("token2", "value2");
        AuthDAO testdb = null;
        try {
            testdb = new AuthDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }

        testdb.addAuthData(value1);
        testdb.addAuthData(value2);
        AuthData returned = testdb.getAuthData(value1.authToken());
        assertEquals(returned, value1);

        testdb.clearAuthData();
        assertNull(testdb.getAuthData(value1.authToken()));
        assertNull(testdb.getAuthData(value2.authToken()));
    }

    @Test
    public void getAuthDataNoDataSQL() {
        AuthData value1 = new AuthData("token1", "value1");

        AuthDAO testdb = null;
        try {
            testdb = new AuthDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearAuthData();
        AuthData returned = testdb.getAuthData(value1.authToken());
        assertNull(returned);
    }

    @Test
    public void successGetUserDataSQL() {
        AuthData value1 = new AuthData("token1", "value1");
        AuthDAO testdb = null;
        try {
            testdb = new AuthDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearAuthData();
        AuthData returned = testdb.getAuthData(value1.authToken());
        assertNull(returned);

        testdb.addAuthData(value1);

        returned = testdb.getAuthData(value1.authToken());
        assertEquals(value1, returned);
    }

    @Test
    public void failureGetUserDataSQL() {
        AuthData value1 = new AuthData("token1", "value1");
        AuthDAO testdb = null;
        try {
            testdb = new AuthDAOSQL();
        } catch (ResponseException e) {
            fail(e.getMessage());
        }
        testdb.clearAuthData();
        AuthData returned = testdb.getAuthData(value1.authToken());
        assertNull(returned);

        testdb.addAuthData(value1);

        returned = testdb.getAuthData("token2");
        assertNull(returned);
    }


}