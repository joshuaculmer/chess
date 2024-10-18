package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class AuthDAOMemory implements AuthDAO {

    HashMap<String, AuthData> dataBase = new HashMap<String, AuthData>();

    public void addAuthData(AuthData authdata) {
        dataBase.put(authdata.authToken(), authdata);
    }

    public AuthData getAuthData(String authToken) {
        return dataBase.get(authToken);
    }

    @Override
    public void removeAuthData(AuthData authData) {
        dataBase.remove(authData.authToken());
    }

    public void clearAuthData() {
        dataBase.clear();
    }


}
