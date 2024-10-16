package service;

public class ClearService {

    public String clearAll(){
        clearAuthData();
        clearGameData();
        clearUserData();

        return ""; // Success
    }

    public void clearUserData() {

    }

    public void clearGameData() {

    }

    public void clearAuthData() {

    }
}
