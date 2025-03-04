package passoff.server;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

public class GameServiceTests {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;
    static AuthService authService;
    static UserService userService;
    static GameService gameService;
    static UserData userData;
    static UserData userData2;
    static UserData failUserData;

    @BeforeAll
    static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        authService = new AuthService(authDAO, userDAO);
        userService = new UserService(authDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @BeforeEach
    void setup() {
        authService.clear();
        userService.clear();
        gameService.clear();
        userData = new UserData("Sigma boy", "password", "sigmaboy@hotmail.com");
        userData2 = new UserData("Alpha male", "password", "alphamail@email.com");
        failUserData = new UserData("Failure of a human", null, "f@gmail.com");
    }

    @Test
    @DisplayName("Positive Create Game Test")
    void successfulCreateGame() throws BadRequestException, DataAccessException, UnauthorizedException {
        RegisterResult registerResult = userService.register(userData);

        int gameID = gameService.createGame(registerResult.authToken(), "game_name");
        Assertions.assertEquals(1, gameID);
    }

    @Test
    @DisplayName("Negative Create Game Test")
    void failCreateGame() throws BadRequestException, DataAccessException, UnauthorizedException {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.createGame("false auth token", "game_name"));
    }

    @Test
    @DisplayName("Positive List Games Test")
    void successfulListGames() throws BadRequestException, DataAccessException, UnauthorizedException {
        RegisterResult registerResult = userService.register(userData);
        ArrayList<GameData> gameList = gameService.listGames(registerResult.authToken());

        Assertions.assertEquals(new ArrayList<GameData>(), gameList);
    }

    @Test
    @DisplayName("Negative List Games Test")
    void failListGames() throws BadRequestException, DataAccessException, UnauthorizedException {
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames("false auth token"));
    }

    @Test
    @DisplayName("Positive Join Game Test")
    void successfulJoinGame() throws BadRequestException, DataAccessException, UnauthorizedException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(userData);
        RegisterResult registerResult2 = userService.register(userData2);
        int gameID = gameService.createGame(registerResult2.authToken(), "game_name");

        gameService.joinGame(registerResult.authToken(), "BLACK", 1);
        gameService.joinGame(registerResult2.authToken(), "WHITE", 1);

        Assertions.assertEquals(gameDAO.getGame(1), gameService.listGames(registerResult.authToken()).getFirst());
    }

    @Test
    @DisplayName("Negative Join Game Test")
    void failJoinGame() throws BadRequestException, DataAccessException, UnauthorizedException, AlreadyTakenException {
        RegisterResult registerResult = userService.register(userData);
        RegisterResult registerResult2 = userService.register(userData2);
        int gameID = gameService.createGame(registerResult2.authToken(), "game_name");

        gameService.joinGame(registerResult.authToken(), "BLACK", 1);

        Assertions.assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(registerResult2.authToken(), "BLACK", 1));
    }

    @Test
    @DisplayName("Positive Clear Test")
    void successfulClear() throws BadRequestException, DataAccessException, UnauthorizedException {
        RegisterResult registerResult = userService.register(userData);
        gameService.createGame(registerResult.authToken(), "game_name");
        gameService.clear();
        Assertions.assertEquals(1, gameDAO.getNextGameID());
    }

    @Test
    @DisplayName("Negative List Games Test")
    void failClear() throws BadRequestException, DataAccessException, UnauthorizedException {
        Assertions.assertDoesNotThrow(() -> gameService.clear());
    }

}
