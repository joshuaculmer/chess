package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public void addAuthData(AuthData authdata);
    public AuthData getAuthData(String AuthToken);
    public void removeAuthData(AuthData authData);
    public void clearAuthData();
}
