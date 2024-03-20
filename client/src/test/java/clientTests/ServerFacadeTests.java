package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authData.getAuthToken());
    }

    @Test
    void registerNegative() {
        assertThrows(Exception.class, () -> facade.register("", "", ""));
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player2", "password", "p2@email.com");
        var authData = facade.login("player2", "password");
        assertNotNull(authData.getAuthToken());
    }

    @Test
    void loginNegative() {
        assertThrows(Exception.class, () -> facade.login("nonexistentuser", "password"));
    }

    @Test
    void createGamePositive() throws Exception {
        facade.register("player3", "password", "p3@email.com");
        assertTrue(facade.createGame("Game1"));
    }

    @Test
    void createGameNegative() {
        facade.storeAuthToken(null);
        assertThrows(Exception.class, () -> facade.createGame("Game2"));
    }

    @Test
    void listGamesPositive() throws Exception {
        facade.register("player7", "password", "p6@email.com");
        facade.createGame("Game7");
        assertTrue(facade.listGames());
    }

    @Test
    void listGamesNegative() throws Exception {
        facade.storeAuthToken(null);
        assertThrows(Exception.class, () -> facade.listGames());
    }

    @Test
    void joinGamePositive() throws Exception {
        facade.register("player4", "password", "p4@email.com");
        facade.createGame("Game3");
        assertTrue(facade.joinGame(1, "white"));
    }

    @Test
    void joinGameNegative() {
        facade.storeAuthToken(null);
        assertThrows(Exception.class, () -> facade.joinGame(1, "white"));
    }

    @Test
    void observeGamePositive() throws Exception {
        facade.register("player5", "password", "p5@email.com");
        facade.createGame("Game4");
        assertTrue(facade.observeGame(1));
    }

    @Test
    void observeGameNegative() {
        facade.storeAuthToken(null);
        assertThrows(Exception.class, () -> facade.observeGame(1));
    }

    @Test
    void logoutPositive() throws Exception {
        facade.register("player6", "password", "p6@email.com");
        assertTrue(facade.logout());
    }

    @Test
    void logoutNegative() {
        facade.storeAuthToken(null);
        assertThrows(Exception.class, () -> facade.logout());
    }
}