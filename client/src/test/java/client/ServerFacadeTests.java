package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.util.ArrayList;
import com.google.gson.internal.LinkedTreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static String validPort;
    private static final String INVALID_PORT = "2123";

    @BeforeAll
    public static void init() {
        server = new Server();
        server.clear();
        int portNumber = server.run(0);
        validPort = Integer.toString(portNumber);
        System.out.println("Started test HTTP server on port " + validPort);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPositive() {
        ServerFacade facade = new ServerFacade(validPort);
        boolean registerResult = facade.register(
                "userRegPos", "pass", "pos@beans.com");
        assertTrue(registerResult);
        assertEquals("LOGGED_IN", facade.getUserLoggedIn());
    }

    @Test
    public void testRegisterNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        boolean registerResult = invalidFacade.register(
                "userRegNeg", "pass", "neg@beans.com");
        assertFalse(registerResult);
        assertEquals("LOGGED_OUT", invalidFacade.getUserLoggedIn());
    }

    @Test
    public void testLoginPositive() {
        ServerFacade facade = new ServerFacade(validPort);
        assertTrue(facade.register(
                "userLoginPos", "pass", "loginpos@beans.com"));
        assertTrue(facade.logout());

        boolean loginResult = facade.login("userLoginPos", "pass");

        assertTrue(loginResult);
        assertEquals("LOGGED_IN", facade.getUserLoggedIn());
    }

    @Test
    public void testLoginNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        boolean loginResult = invalidFacade.login("userLoginNeg", "pass");
        assertFalse(loginResult);
        assertEquals("LOGGED_OUT", invalidFacade.getUserLoggedIn());
    }

    @Test
    public void testLogoutPositive() {
        ServerFacade facade = new ServerFacade(validPort);
        assertTrue(facade.register(
                "userLogoutPos", "pass", "logoutpos@beans.com"));
        boolean logoutResult = facade.logout();
        assertTrue(logoutResult);
        assertEquals("LOGGED_OUT", facade.getUserLoggedIn());
    }

    @Test
    public void testLogoutNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        boolean logoutResult = invalidFacade.logout();
        assertFalse(logoutResult);
        assertEquals("LOGGED_OUT", invalidFacade.getUserLoggedIn());
    }

    @Test
    public void testCreateGamePositive() {
        ServerFacade facade = new ServerFacade(validPort);
        assertTrue(facade.register(
                "userCreateGamePos", "pass", "createpos@beans.com"));
        double gameID = facade.createGame("TestGamePos");
        assertNotEquals(0, gameID);
    }

    @Test
    public void testCreateGameNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        double gameID = invalidFacade.createGame("TestGameNeg");
        assertEquals(0, gameID);
    }

    @Test
    public void testListGamesPositive() {
        ServerFacade facade = new ServerFacade(validPort);
        assertTrue(facade.register(
                "userListGamePos", "pass", "listpos@beans.com"));
        double gameID = facade.createGame("ListGamePos");
        assertNotEquals(0, gameID);

        ArrayList<LinkedTreeMap> games = facade.listGames();
        boolean found = false;
        for (LinkedTreeMap game : games) {
            if (game.containsKey("gameID") && ((Number) game.get("gameID")).doubleValue() == gameID) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testListGamesNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        ArrayList<LinkedTreeMap> games = invalidFacade.listGames();
        assertNull(games);
    }

    @Test
    public void testJoinGamePositive() {
        ServerFacade facade = new ServerFacade(validPort); // for client

        assertTrue(facade.register("userJoinPos", "pass", "joinpos@beans.com"));
        double gameID = facade.createGame("JoinGamePos");
        assertNotEquals(0, gameID);

        boolean joinResult = facade.joinGame("BLACK", gameID);
        assertTrue(joinResult);
    }

    @Test
    public void testJoinGameNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);
        boolean joinResult = invalidFacade.joinGame("blue", 12345);
        assertFalse(joinResult);
    }

    @Test
    public void testViewGamePositive() {
        ServerFacade facade = new ServerFacade(validPort);
        assertTrue(facade.register(
                "userViewPos", "pass", "viewpos@beans.com"));
        double gameID = facade.createGame("ViewGamePos");
        assertNotEquals(0, gameID);

        boolean viewResult = facade.viewGame(gameID);
        assertTrue(viewResult);
    }

    @Test
    public void testViewGameNegative() {
        ServerFacade invalidFacade = new ServerFacade(INVALID_PORT);

        boolean viewResult = invalidFacade.viewGame(1235545);
        assertFalse(viewResult);
    }
}
