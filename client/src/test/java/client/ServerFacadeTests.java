package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static UserData existingUser;
    static String existingAuth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:" + port + "/");

        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void setUp() throws ResponseException {
        facade.clearAll();
        AuthData regResult = facade.registerUser(existingUser);
        existingAuth = regResult.authToken();

    }


    @Test
    void register() throws Exception {
        AuthData authData = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerInvalid() throws Exception {
        try {
            AuthData authData=facade.registerUser(new UserData("player1", "password", null));
            fail();
        }
        catch (ResponseException ex) {
            assertEquals(400, ex.statusCode());
        }
    }


    @Test
    void login() throws Exception {
        AuthData result = facade.loginUser(existingUser);
        assertEquals(result.username(), existingUser.username());
    }

    @Test
    void loginInvalid() throws Exception {
        try {
            facade.loginUser(new UserData("test", "testpw", "boogus@gmail.com"));
            fail();
        }
        catch (ResponseException ex) {
            assertEquals("Error: Unauthorized", ex.getMessage());
        }
    }

    @Test
    void logout() throws Exception {
        facade.logout(existingAuth);
    }

    @Test
    void logoutNotSignedIn() throws Exception {
        UserData newUser = new UserData("player1", "password", "p1@email.com");
        try {
            facade.logout(newUser.password());
            fail();
        }
        catch (ResponseException e) {
            assertEquals(e.statusCode(), 401);
        }
    }

    @Test
    void createGame() throws Exception {
        record GameCreateRequest (String gameName) {}
        GameCreateRequest createRequest = new GameCreateRequest("new Game");
        int gameID = facade.createGame(existingAuth, createRequest);
        System.out.println(gameID);
    }

    @Test
    void createGameInvalid() throws Exception {
        record GameCreateRequest (String gameName) {}
        GameCreateRequest createRequest = new GameCreateRequest("new Game");
        try {
            facade.createGame("existingAuth", createRequest);
            fail();
        } catch (ResponseException e) {
            assertEquals(e.statusCode(), 401);
        }
    }

    @Test
    void listGamesEmpty() throws Exception {
        assert( facade.listGames(existingAuth).size() == 0 );
    }

    @Test
    void listGamesNotEmpty() throws Exception {
        assert( facade.listGames(existingAuth).size() == 0 );

        createGame();
        assert(facade.listGames(existingAuth).size() == 1);
    }



}
