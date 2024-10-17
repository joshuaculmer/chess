package model;

public record UserData (String username, String password, String email){
    public boolean isValid() {
        return username != null && password != null && email != null;
    }
}
