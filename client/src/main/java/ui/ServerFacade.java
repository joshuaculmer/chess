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
        if(!user.isValid()) throw new ResponseException(400, "Error: Bad Request");
        return makeRequest("POST", path, user, null,  AuthData.class);
    }

    public void logout(String authToken) throws ResponseException{
        String path = "/session";
        makeRequest("DELETE", path, null, authToken, null);
    }

    public ArrayList<GameData> listGames(String authToken) throws ResponseException{
        String path = "/game";
        return makeRequest("POST", path, null, authToken, ArrayList.class);
    }

    public int createGame(String authToken, String gameName) throws ResponseException{
        String path = "/game";
        return makeRequest("POST", path, gameName, authToken, int.class);
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

            writeBody(request, http);
            writeHeader(header, http);
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
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeader(String header, HttpURLConnection http) throws IOException {
        if (header != null) {
            http.addRequestProperty("Authorization", header);
            String reqData = new Gson().toJson(header);
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
