package dataaccess;

import model.UserData;

public interface UserDAO {
    public UserData getUserData(String userName);
    public void addUserData(UserData data);
    public void clearUserData();
}
