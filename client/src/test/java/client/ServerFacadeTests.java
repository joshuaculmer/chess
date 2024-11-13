package client;

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

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port + "/");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void setUp() throws ResponseException {
        facade.clearAll();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
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


}
