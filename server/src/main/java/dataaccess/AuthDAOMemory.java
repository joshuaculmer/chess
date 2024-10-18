package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class AuthDAOMemory {

    HashMap<String, AuthData> dataBase = new HashMap<String, AuthData>();

    public void addAuthData(AuthData authdata) {
        dataBase.put(authdata.authToken(), authdata);
    }

    public AuthData getAuthData(String authToken) {
        return dataBase.get(authToken);
    }

    public void removeAuthData(String authToken) {
        dataBase.remove(authToken);
    }

    public void clearAuthData() {
        dataBase.clear();
    }


}
