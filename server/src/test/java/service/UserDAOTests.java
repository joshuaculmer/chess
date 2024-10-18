package service;

import dataaccess.UserDAOMemory;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class UserDAOTests {

    @Test
    public void GetUserDataNoData() {

        UserData expected = new UserData("default", "pw", "example");
        UserDAOMemory testdb = new UserDAOMemory();
        UserData returned = testdb.getUserData(expected.username());
        assertNull(returned);
    }
}
