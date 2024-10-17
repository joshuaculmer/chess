package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDAOMemory implements UserDAO{

    HashMap<String, UserData> dataBase = new HashMap<String, UserData>();


    @Override
    public UserData getUserData(String userName) {
        return dataBase.get(userName);
    }

    @Override
    public void addUserData(UserData data) {
        dataBase.put(data.username(), data);
    }

    @Override
    public void clearUserData() {

    }
}
