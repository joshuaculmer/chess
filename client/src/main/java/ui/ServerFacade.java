package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.*;
import java.io.*;
import java.util.ArrayList;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData registerUser(UserData user) throws ResponseException{
        String path = "/user";
        if(!user.isValid()) throw new ResponseException(400, "Error: Bad Request");
        return makeRequest("POST", path, user, null,  AuthData.class);
    }

    public AuthData loginUser(UserData user) throws ResponseException{
        String path = "/session";
        return makeRequest("POST", path, user, null,  AuthData.class);
    }

    public void logout(String authToken) throws ResponseException{
        String path = "/session";
        makeRequest("DELETE", path, null, authToken, null);
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException{
        String path = "/game";
        record GamesList (ArrayList<GameData> games) {};
        var temp = makeRequest("GET", path, null, authToken, GamesList.class);
        return temp.games();
    }

    public int createGame(String authToken, String gameName) throws ResponseException{
        String path = "/game";
        record GameCreateRequest (String gameName) {}
        GameCreateRequest createRequest = new GameCreateRequest(gameName);
        record GameID (int gameID) {};
        return makeRequest("POST", path, createRequest, authToken, GameID.class).gameID;
    }

    public void joinGame(String authToken, ChessGame.TeamColor color, int gameID) throws ResponseException {
        String path = "/game";
        record JoinRequest (ChessGame.TeamColor playerColor, int gameID){}
        JoinRequest request = new JoinRequest(color, gameID);
        makeRequest("PUT", path, request, authToken,  null);
    }

    public void clearAll(String... params) throws ResponseException{
        String path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }


    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeHeaderAndBody(request, header, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeHeaderAndBody(Object body, String header, HttpURLConnection http) throws IOException {
        String reqData = "";
        if (header != null) {
            http.addRequestProperty("Authorization", header);
        }
        if (body != null) {
            http.addRequestProperty("Content-Type", "application/json");
            reqData += new Gson().toJson(body);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "Error: " + http.getResponseMessage());
        }
    }


    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
